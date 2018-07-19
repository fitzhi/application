import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, tap} from 'rxjs/operators';

import {Collaborator} from './data/collaborator';
import {MOCK_COLLABORATORS} from './mock/mock-collaboraters';

import {Constants} from './constants';

import {Observable, of} from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class CollaboratorService {

  private collaborateresUrl = 'api/collaborater';  // URL to web api

  constructor(
    private http: HttpClient) {}

  /**
   * Return the global list of collaborators, working for our company.
   */
  getCollaborators(): Collaborator[] {
    if (Constants.DEBUG) {
      this.log('Fetching the collaborators');
    }
    //    return this.http.get<Collaborater[]>(this.collaborateresUrl).pipe(catchError(this.handleError('getCollaboraters', [])));
    return MOCK_COLLABORATORS;
  }

  /** GET collaborater by id. Will 404 if id not found */
  getCollaborater(id: number): Observable<Collaborator> {
    return null;
    /*
    const url = `${this.collaborateresUrl}/${id}`;
    return this.http.get<Collaborater>(url).pipe(
      tap(_ => this.log(`fetched collaborater id=${id}`)),
      catchError(this.handleError<Collaborater>(`getCollaborater id=${id}`))
    );
     */
  }

  private log(message: string) {
    console.log(message);
  }


  /**
 * Handle Http operation that failed.
 * Let the app continue.
 * @param operation - name of the operation that failed
 * @param result - optional value to return as the observable result
 */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }


  updateCollaborater(collaborater: Collaborator): Observable<any> {
    return this.http.put(this.collaborateresUrl, collaborater, httpOptions).pipe(
      tap(_ => this.log(`updated collaborater id=${collaborater.id}`)),
      catchError(this.handleError<any>('updateCollaborater'))
    );
  }

  /** POST: add a new collaborater to the server */
  addCollaborater(collaborater: Collaborator): Observable<Collaborator> {
    return this.http.post<Collaborator>(this.collaborateresUrl, collaborater, httpOptions).pipe(
      tap((collaborater: Collaborator) => this.log(`added collaborater w/ id=${collaborater.id}`)),
      catchError(this.handleError<Collaborator>('addCollaborater'))
    );
  }

  /** DELETE: delete the collaborater from the server */
  deleteCollaborater(collaborater: Collaborator | number): Observable<Collaborator> {
    const id = typeof collaborater === 'number' ? collaborater : collaborater.id;
    const url = `${this.collaborateresUrl}/${id}`;

    return this.http.delete<Collaborator>(url, httpOptions).pipe(
      tap(_ => this.log(`deleted collaborater id=${id}`)),
      catchError(this.handleError<Collaborator>('deleteCollaborater'))
    );
  }

  /* GET collaboraters whose name contains search term */
  searchCollaborateres(term: string): Observable<Collaborator[]> {
    if (!term.trim()) {
      // if not search term, return empty collaborater array.
      return of([]);
    }
    return this.http.get<Collaborator[]>(`${this.collaborateresUrl}/?name=${term}`).pipe(
      tap(_ => this.log(`found collaborateres matching "${term}"`)),
      catchError(this.handleError<Collaborator[]>('searchCollaborateres', []))
    );
  }
}
