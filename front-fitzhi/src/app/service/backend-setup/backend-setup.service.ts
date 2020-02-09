import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
	providedIn: 'root'
})
export class BackendSetupService {

	/**
     * Default URL for a local directory.
     */
	public defaultUrl = 'http://localhost:8080';

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
	public isVeryFirstConnection(urlCandidate: string): Observable<string> {
		return this.httpClient.get<string>(
			urlCandidate + '/api/admin/isVeryFirstConnection', { responseType: 'text' as 'json' });
	}
}
