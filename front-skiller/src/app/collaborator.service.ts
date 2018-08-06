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

  /**
   * GET staff member associated to this id. Will throw a 404 if id not found.
   */
  get(id: number): Observable<Collaborator> {
    const url = this.collaboratorUrl + '/' + id;
    if (Constants.DEBUG) {
      console.log('Fetching the collaborator ' + id + ' on the address ' + url);
    }
    return this.http.get<Collaborator>(url);
  }

  /**
   * POST: update or add a new collaborator to the server
   */
  save(collaborator: Collaborator): Observable<Collaborator> {
    if (Constants.DEBUG) {
      console.log('Saving the collaborator with id ' + collaborator.id);
    }
    return this.http.post<Collaborator>(this.collaboratorUrl + '/save', collaborator, httpOptions);

  }

  /**
   * DELETE delete a staff member from the server
   */
  delete(collaborater: Collaborator | number): Observable<Collaborator> {
    const id = typeof collaborater === 'number' ? collaborater : collaborater.id;
    const url = `${this.collaboratorUrl}/${id}`;

    return this.http.delete<Collaborator>(url, httpOptions).pipe(
      tap(_ => this.log(`deleted collaborater id=${id}`)),
      catchError(this.handleError<Collaborator>('deleteCollaborater'))
    );
  }
}
