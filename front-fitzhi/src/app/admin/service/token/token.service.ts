import { HttpClient, HttpHeaders, HttpParams, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { traceOn } from 'src/app/global';
import { Token } from './token';

@Injectable({
	providedIn: 'root'
})
export class TokenService {

	private token: Token = null;

	constructor(private httpClient: HttpClient) { }

	/**
	 * This method requests the backend server for a new access token.
	 * @returns an `*observable*` emitting the returned token
	 */
	refreshToken$(): Observable<Token> {

		//
        //    The call that goes in here will use the existing refresh token to call
        //    a method on the oAuth server (usually called refreshToken) to get a new
        //    authorization token for the API calls.
        //
		if (traceOn()) {
			if (this.token) {
				console.log('Refresh the active token with token %s', this.token.refresh_token);
			} else {
				console.log('Token is undefined');
			}
		}

		// Loading the token (if undefined) from the localStorage. 
		if (!this.token) {
			this.token = this.loadToken();
		}

		let headers: HttpHeaders = new HttpHeaders();
		headers = headers.append('Content-Type', 'application/x-www-urlencoded');
		headers = headers.append('Authorization', 'Basic ' + btoa('fitzhi-trusted-client' + ':secret'));

		const params = new HttpParams()
			.set('refresh_token', this.token.refresh_token)
			.set('grant_type', 'refresh_token');

		return this.httpClient.post<Token>(this.urlRefreshToken(), '', { headers: headers, params: params });
	}

	/**
	 * @returns the URL to request the server for a new _temporary_ access token. 
	 */
	urlRefreshToken() {
		return localStorage.getItem('backendUrl') + '/oauth/token';
	}

	/**
	 * Add the authentication token _if necessary_.
	 * @param req the given request
	 * @returns the authenticated request
	 */
	addToken(req: HttpRequest<any>): HttpRequest<any> {
		// We do not add an header if the request is an authentication request.
		if (req.params.get('grant_type')) {
			switch (req.params.get('grant_type')) {
				case 'refresh_token': 
				case 'password':
					return req;
			}
		}
		return (this.token) ?
			req.clone({ setHeaders: { Authorization: 'Bearer ' + this.token.access_token } }) : req;
	}

	/**
	 * Load the token from the localStorage
	 */
	loadToken(): Token {
		const r_t = localStorage.getItem('refresh_token');
		const a_t = localStorage.getItem('access_token');
		const token = new Token();
		token.access_token = a_t;
		token.refresh_token = r_t;
		return token;
	}

	/**
	 * Save the token in the localStorage
	 * @param token the given token
	 */
	public saveToken(token: Token) {
		this.token = token;
		// store the new token
		if (token) {
			localStorage.setItem('refresh_token', token.refresh_token);
			localStorage.setItem('access_token', token.access_token);
		} else {
			localStorage.removeItem('refresh_token');
			localStorage.removeItem('access_token');
		}
	}

	public getToken(): Token {
		return this.token;
	}
}

