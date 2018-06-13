import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, tap} from 'rxjs/operators';

import {Collaborater} from './collaborater';
import {COLLABORATERS} from './mock-collaboraters';

import {Observable, of} from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class CollaboraterService {

  private collaborateresUrl = 'api/collaborater';  // URL to web api

  constructor(
    private http: HttpClient) {}

  getCollaboraters(): Observable<Collaborater[]> {
    this.log('Fetched collaboraters');

    //    return this.http.get<Collaborater[]>(this.collaborateresUrl).pipe(catchError(this.handleError('getCollaboraters', [])));
    return of(COLLABORATERS);
  }

  /** GET collaborater by id. Will 404 if id not found */
  getCollaborater(id: number): Observable<Collaborater> {
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


  updateCollaborater(collaborater: Collaborater): Observable<any> {
    return this.http.put(this.collaborateresUrl, collaborater, httpOptions).pipe(
      tap(_ => this.log(`updated collaborater id=${collaborater.id}`)),
      catchError(this.handleError<any>('updateCollaborater'))
    );
  }

  /** POST: add a new collaborater to the server */
  addCollaborater(collaborater: Collaborater): Observable<Collaborater> {
    return this.http.post<Collaborater>(this.collaborateresUrl, collaborater, httpOptions).pipe(
      tap((collaborater: Collaborater) => this.log(`added collaborater w/ id=${collaborater.id}`)),
      catchError(this.handleError<Collaborater>('addCollaborater'))
    );
  }

  /** DELETE: delete the collaborater from the server */
  deleteCollaborater(collaborater: Collaborater | number): Observable<Collaborater> {
    const id = typeof collaborater === 'number' ? collaborater : collaborater.id;
    const url = `${this.collaborateresUrl}/${id}`;

    return this.http.delete<Collaborater>(url, httpOptions).pipe(
      tap(_ => this.log(`deleted collaborater id=${id}`)),
      catchError(this.handleError<Collaborater>('deleteCollaborater'))
    );
  }

  /* GET collaboraters whose name contains search term */
  searchCollaborateres(term: string): Observable<Collaborater[]> {
    if (!term.trim()) {
      // if not search term, return empty collaborater array.
      return of([]);
    }
    return this.http.get<Collaborater[]>(`${this.collaborateresUrl}/?name=${term}`).pipe(
      tap(_ => this.log(`found collaborateres matching "${term}"`)),
      catchError(this.handleError<Collaborater[]>('searchCollaborateres', []))
    );
  }
}
