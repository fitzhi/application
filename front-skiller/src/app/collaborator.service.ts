import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, tap} from 'rxjs/operators';

import {Collaborator} from './data/collaborator';
import {MOCK_COLLABORATORS} from './mock/mock-collaborators';

import {Constants} from './constants';
import {Observable, of} from 'rxjs';

import {InternalService} from './internal-service';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class CollaboratorService extends InternalService {

  private collaboratorUrl = 'http://localhost:8080/staff';  // URL to web api

  constructor(
    private http: HttpClient) {
    super();
  }

  /**
   * Return the global list of ALL collaborators, working for the company.
   */
  getAll(): Observable<Collaborator[]> {
    if (Constants.DEBUG) {
      this.log('Fetching the collaborators');
    }
    return this.http.get<Collaborator[]>(this.collaboratorUrl + '/all');
  }

  /** GET collaborater by id. Will 404 if id not found */
  get(id: number): Observable<Collaborator> {
    const url = this.collaboratorUrl + '/' + id;
    if (Constants.DEBUG) {
      console.log('Fetching the collaborator ' + id + ' on the address ' + url);
    }
    return this.http.get<Collaborator>(url);
  }

  updateCollaborater(collaborater: Collaborator): Observable<any> {
    return this.http.put(this.collaboratorUrl, collaborater, httpOptions).pipe(
      tap(_ => this.log(`updated collaborater id=${collaborater.id}`)),
      catchError(this.handleError<any>('updateCollaborater'))
    );
  }

  /** POST: update or add a new collaborator to the server */
  save(collaborator: Collaborator): Observable<Collaborator> {
    if (Constants.DEBUG) {
      console.log('Saving the collaborator ' + collaborator.id);
    }
    return this.http.post<Collaborator>(this.collaboratorUrl + '/save', collaborator, httpOptions).pipe(
      tap( (collab: Collaborator) =>
        this.log('updated collaborator w/ id=${collab.id}')),
        catchError(this.handleError<Collaborator>('addCollaborater'))
    );
  }

  /** DELETE: delete the collaborater from the server */
  deleteCollaborater(collaborater: Collaborator | number): Observable<Collaborator> {
    const id = typeof collaborater === 'number' ? collaborater : collaborater.id;
    const url = `${this.collaboratorUrl}/${id}`;

    return this.http.delete<Collaborator>(url, httpOptions).pipe(
      tap(_ => this.log(`deleted collaborater id=${id}`)),
      catchError(this.handleError<Collaborator>('deleteCollaborater'))
    );
  }

  /* GET collaborators whose name contains search term */
  searchCollaborateres(term: string): Observable<Collaborator[]> {
    if (!term.trim()) {
      // if not search term, return empty collaborater array.
      return of([]);
    }
    return this.http.get<Collaborator[]>(`${this.collaboratorUrl}/?name=${term}`).pipe(
      tap(_ => this.log(`found collaborateres matching "${term}"`)),
      catchError(this.handleError<Collaborator[]>('searchCollaborateres', []))
    );
  }
}
