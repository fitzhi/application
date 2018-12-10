import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, tap} from 'rxjs/operators';
import {Constants} from './constants';
import {Observable, of} from 'rxjs';

export class InternalService {
 /**
 * Handle Http operation that failed.
 * Let the app continue.
 * @param operation - name of the operation that failed
 * @param result - optional value to return as the observable result
 */
  handleError<T>(operation = 'operation', result?: T) {
  	return (error: any): Observable<T> => {

		// TODO: send the error to remote logging infrastructure
		console.error(error); // log to console instead

		// TODO: better job of transforming error for user consumption
		this.log(operation + ' failed: ' + error.message);

		// Let the app keep running by returning an empty result.
		return of(result as T);
		};
  	}

	log(message: string) {
		console.log(message);
	}
}
