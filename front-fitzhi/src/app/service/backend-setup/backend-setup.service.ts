import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, take, tap } from 'rxjs/operators';
import { FirstConnection } from 'src/app/data/first-connection';
import { traceOn } from 'src/app/global';
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
	 *
	 * @param urlCandidate the url candidate for hosting the backend.
	 * @return an observable emitting a `FistConnection` object.
	 */
	public isVeryFirstConnection$(urlCandidate: string): Observable<FirstConnection> {
		return this.httpClient.get<string>(
			`${urlCandidate}/api/admin/isVeryFirstConnection`, { responseType: 'text' as 'json' }).
			pipe(
				switchMap(result => {
					return of(new FirstConnection(true, ((result === 'true') ? true : false)));
				}),
				catchError(error => {
					// Either the Server is offline, or the given URL is wrong. No response
					return of(new FirstConnection(false));
				})
			);
	}

	/**
	 * Inform the backend server that the very first installation has been successfully completed.
	 * The first user in Fitzhi has been registered.
	 * @returns an **observable** emitting a **TRUE** if the Rest CALL is OK.
	 */
	public saveVeryFirstConnection$(): Observable<Boolean> {

		return this.httpClient.post<Boolean>(`${this.url()}/admin/saveVeryFirstConnection`, '')
				.pipe(
					take(1),
					tap(veryFirstConnectionIsRegistered => {
						if (traceOn() && veryFirstConnectionIsRegistered) {
							console.log('The very first connection is registered into Fitzhi.');
						}
					})
				);
	}

}
