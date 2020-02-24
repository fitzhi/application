import { Injectable } from '@angular/core';
import { InternalService } from 'src/app/internal-service';
import { Constants } from 'src/app/constants';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { take, switchMap, catchError } from 'rxjs/operators';
import { Token } from './token';
import { Observable, of } from 'rxjs';
import { traceOn } from 'src/app/global';

@Injectable({
	providedIn: 'root'
})
export class AuthService extends InternalService {

	/**
     * This boolean is TRUE if the user is connected.
     */
	private connected = false;

	constructor(
		private backendSetupService: BackendSetupService,
		private httpClient: HttpClient) { super(); }

	public connect(username: string, password: string): Observable<boolean> {

		if (traceOn()) {
			console.log('Trying a connection with user/pass ' + username + ':' + password
				+ ' on url ' + this.backendSetupService.url() + '/oauth/token');
		}

		let headers: HttpHeaders = new HttpHeaders();
		headers = headers.append('Content-Type', 'application/x-www-urlencoded');
		headers = headers.append('Authorization', 'Basic ' + btoa('fitzhi-trusted-client' + ':secret'));

		const params = new HttpParams()
			.set('username', username)
			.set('password', password)
			.set('grant_type', 'password');

		return this.httpClient.post<Token>(
			localStorage.getItem('backendUrl') + '/oauth/token', '', { headers: headers, params: params })
			.pipe(
				take(1),
				switchMap(
					token => {
						if (traceOn()) {
							console.groupCollapsed('Identifity retrieved : ');
							console.log('access_token', token.access_token);
							console.log('refresh_token', token.refresh_token);
							console.log('expires_in', token.expires_in);
							console.groupEnd();
						}
						// store the new tokens
						localStorage.setItem('refresh_token', token.refresh_token);
						localStorage.setItem('access_token', token.access_token);
						this.connected = true;
						return of (this.connected);
					}),
					catchError(
						error => {
							console.log ('error   !!!', error);
							if (traceOn()) {
								if (typeof error !== 'undefined') {
									console.log ('error', error);
								}
							}
							this.connected = false;
							return of (this.connected);
						} ));
	}

	/**
     * @returns TRUE if the user is connected, FALSE otherwise.
     */
	public isConnected() {

		// For development convenience, we deactivate the security control.
		if (localStorage.getItem('dev') === '1') {
			return true;
		}
		return this.connected;
	}

	getAccessToken() {
		return localStorage.getItem('access_token');
	}

	/**
	 * Return an `*observable*` emitting the new access token.
	 * Refresh the access token.
	 */
	refreshToken(): Observable<string> {
		/*
            The call that goes in here will use the existing refresh token to call
            a method on the oAuth server (usually called refreshToken) to get a new
            authorization token for the API calls.
        */
		if (traceOn()) {
			console.log('refresh current token', localStorage.getItem('access_token'));
		}

		let headers: HttpHeaders = new HttpHeaders();
		headers = headers.append('Content-Type', 'application/x-www-urlencoded');
		headers = headers.append('Authorization', 'Basic ' + btoa('fitzhi-trusted-client' + ':secret'));

		let access_token = 'empty';

		const params = new HttpParams()
			.set('access_token', localStorage.getItem('access_token'))
			.set('refresh_token', localStorage.getItem('refresh_token'))
			.set('grant_type', 'refresh_token');

		return this.httpClient.post<Token>(
			this.backendSetupService.url() + '/oauth/token', '', { headers: headers, params: params })
			.pipe(
				take(1),
				switchMap(token => {
					if (traceOn()) {
						console.groupCollapsed('Identifity retrieved : ');
						console.log('access_token', token.access_token);
						console.log('refresh_token', token.refresh_token);
						console.log('expires_in', token.expires_in);
						console.groupEnd();
					}
					// store the new tokens
					access_token = token.access_token;
					localStorage.setItem('refresh_token', token.refresh_token);
					localStorage.setItem('access_token', token.access_token);
					this.connected = true;

					return of(token.access_token);
				}
			));
	}
}


