import { Injectable } from '@angular/core';
import { InternalService } from 'src/app/internal-service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { take, switchMap, catchError } from 'rxjs/operators';
import { Token } from '../token/token';
import { Observable, of } from 'rxjs';
import { traceOn } from 'src/app/global';
import { TokenService } from '../token/token.service';

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
		private tokenService: TokenService,
		private httpClient: HttpClient) { super(); }

	public connect$(username: string, password: string): Observable<boolean> {

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
			localStorage.getItem('backendUrl') + '/oauth/token', '', 
				{ headers: headers, params: params })
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
						this.tokenService.saveToken(token);
						this.connected = true;
						return of (this.connected);
				}),
				catchError(
					error => {
						if (traceOn() && (error)) {
							console.log ('Error', error);
						}
						this.connected = false;
						return of (this.connected);
					}
				)
		);
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

}


