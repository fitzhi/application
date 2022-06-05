import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, take, tap } from 'rxjs/operators';
import { OpenIdCredentials } from 'src/app/data/open-id-credentials';
import { OpenIdTokenStaff } from 'src/app/data/openidtoken-staff';
import { traceOn } from 'src/app/global';
import { InternalService } from 'src/app/internal-service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { Token } from '../token/token';
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

	/**
	 * Authentification request in a classic approach (user/password).
	 * @param username the given username
	 * @param password the given password
	 * @returns an observable containing a boolean equal to **true** if the connection succeeds, **false** otherwise.
	 */
	public connectClassic$(username: string, password: string): Observable<boolean> {

		if (traceOn()) {
			console.log('Trying a connection with user/pass ' + username + ':' + password
				+ ' on url ' + this.backendSetupService.url() + '/oauth/token');
		}

		let headers: HttpHeaders = new HttpHeaders();
		headers = headers.append('Content-Type', 'application/x-www-form-urlencoded');
		headers = headers.append('Authorization', 'Basic ' + btoa('fitzhi-trusted-client:secret'));

		const params = new HttpParams()
			.set('username', username)
			.set('password', password)
			.set('grant_type', 'password');

		return this.httpClient.post<Token>(
			localStorage.getItem('backendUrl') + '/oauth/token',
				params.toString(),
				{ headers: headers })
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
	 * Authentification request in the OpenId approach
	 * @param credentials the openID credentials loaded from the authentication server
	 * @returns an observable containing the staff linked to the credentials
	 */
	public connectOpenId$(credentials: OpenIdCredentials): Observable<OpenIdTokenStaff> {

		const body = { openIdServer: credentials.serverId, idToken: credentials.jwt};

		return this.httpClient.post<OpenIdTokenStaff>(this.backendSetupService.url() + '/admin/openId/connect', body)
			.pipe(
				take(1),
				tap({
					next: oits => {
						const staff = oits.staff;
						if (traceOn()) {
							console.log('Identity retrieved : %d %s %s', staff.idStaff, staff.firstName, staff.lastName);
						}
						this.setConnect();
					},
					error: error => {
						if (traceOn() && (error)) {
							console.log ('Error', error);
						}
						this.setDisconnect();
					}
				}));
	}

	/**
	 * `Disable` the connection status stored in the authentication service.
	 */
	public setDisconnect() {
		this.connected = false;
	}

	/**
	 * `Enable` the connection status stored in the authentication service.
	 */
	public setConnect() {
		this.connected = true;
	}

	/**
     * @returns `TRUE` if the user is connected, `FALSE` otherwise.
     */
	public isConnected() {

		// For development convenience, we deactivate the security control.
		if (localStorage.getItem('dev') === '1') {
			return true;
		}
		return this.connected;
	}

}


