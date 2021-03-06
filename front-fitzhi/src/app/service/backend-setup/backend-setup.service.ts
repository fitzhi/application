import { Injectable } from '@angular/core';
import { EMPTY, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { FirstConnection } from 'src/app/data/first-connection';
import { catchError, switchMap } from 'rxjs/operators';
import { Constants } from 'src/app/constants';
import { environment } from '../../../environments/environment';

@Injectable({
	providedIn: 'root'
})
export class BackendSetupService {

	public environment = environment;

	/**
     * Default URL of the API Rest server.
	 * 
	 * This URL is provided at build time.
     */
	public defaultUrl = environment.apiUrl;

	constructor(private  httpClient: HttpClient) {}

	/**
     * A URL has already been saved in the localstorage.
     */
	hasSavedAnUrl() {
		return (localStorage.getItem('backendUrl') !== null);
	}

	/**
     * @return the back-end URL
     */
	public url() {
		return localStorage.getItem('backendUrl') + '/api';
	}

	/**
     * @param validated URL pointing to the back-end server.
     */
	public saveUrl(url: string) {
		localStorage.setItem('backendUrl', url);
	}

	/**
     * Remove the URL saved inside the localstorage
     */
	public removeUrl() {
		localStorage.removeItem('backendUrl');
	}

	/**
	 * Test the passed URL and check if it is the very first connection.
	 * @param urlCandidate the url candidate for hosting the backend.
	 */
	public isVeryFirstConnection(urlCandidate: string): Observable<FirstConnection> {
		return this.httpClient.get<string>(
			urlCandidate + '/api/admin/isVeryFirstConnection', { responseType: 'text' as 'json' }).
			pipe(
				switchMap(result => {
					return of(new FirstConnection(((result === 'true') ? true : false), null)); 
				}),
				catchError(response => {
					// Server is offline. No response
					if (response === Constants.SERVER_DOWN) {
						return of(new FirstConnection(false, null)); 
					} else {
						// Throw other kinds of error
						return of(new FirstConnection(false, null)); 
					}
				})
			);
	}
}
