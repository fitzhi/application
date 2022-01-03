
import {
	HttpErrorResponse, HttpHandler, HttpHeaderResponse, HttpInterceptor, HttpProgressEvent,
	HttpRequest, HttpResponse, HttpSentEvent, HttpUserEvent
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, EMPTY, Observable, throwError } from 'rxjs';
import { catchError, filter, finalize, switchMap, take } from 'rxjs/operators';
import { traceOn } from 'src/app/global';
import { Token } from '../token/token';
import { TokenService } from '../token/token.service';

@Injectable()
export class HttpTokenInterceptor implements HttpInterceptor {

	authToken$: BehaviorSubject<Token> = new BehaviorSubject<Token>(null);

	isRefreshingToken = false;

	private NO_SECURITY = 'no-security';

	constructor(
		private tokenService: TokenService) { }


	/**
	 * Extract the host from the given URL.
	 * @param url the given URL
	 * @returns the extracted host from the URL
	 */
	public static extractHost(url: String) {
		const matches = url.match(/^https?\:\/\/([^\/?#]+)(?:[\/?#]|$)/i);
		const domain = matches && matches[1];
		if (!domain) {
			console.error('Cannot extract the hostname from %s', url);
		}
		return domain;
	}

	/**
	 * Intercept the HTTP request.
	 * @param req the current request
	 * @param next the next handler to be in charge of the given request
	 */

	intercept(req: HttpRequest<any>, next: HttpHandler):
		Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {

		//
		// FOR DEVELOPMENT PURPOSE ONLY, we unplugg the security control.
		//
		if (localStorage.getItem(this.NO_SECURITY) === '1') {
			return next.handle(req);
		}

		const host = HttpTokenInterceptor.extractHost(req.url);

		if 	(req.url.includes('/api/referential/')

			|| (req.url.substring(req.url.length - '/api/skill'.length) === '/api/skill')
			|| req.url.includes('/api/admin/isVeryFirstConnection')
			|| req.url.includes('/api/admin/veryFirstUser')
			|| req.url.includes('/api/admin/register')

			// Sonar URL
			|| req.url.includes('/api/components/search')
			|| req.url.includes('/api/components/tree')
			|| req.url.includes('/api/metrics/search')
			|| req.url.includes('/api/measures/component')
			|| req.url.includes('/api/project_badges/measure')
			|| req.url.includes('/api/server/version')

			// GitHub URL
			|| (host && (host  === 'api.github.com'))) {

				return next.handle(req);
		}

		return next.handle(this.tokenService.addToken(req)).pipe(
			catchError(response => {
				if (response instanceof HttpErrorResponse) {
					if (response.status === 401) {
						if (!this.isConnectionRequest(req)) {
							return this.retryAfterRefresh$(req, next);
						} else {
							if (traceOn()) {
								console.log ('Invalid authentification credentials');
							}
							return EMPTY;
						}
					} else {
						return throwError(response);
					}
				} else {
					return throwError(response);
				}
			}));
	}

	isConnectionRequest(req: HttpRequest<any>): boolean {

		if (!req.body) {
			return false;
		}

		if (!req.body.params) {
			return false;
		}

		let isConnection = false;
		req.body.params.updates.forEach(element => {
			if ((element.param === 'grant_type') && (element.value === 'password')) {
				isConnection = true;
			}
		});
		return isConnection;
	}

	retryAfterRefresh$(req: HttpRequest<any>, next: HttpHandler):
		Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {

		if (!this.isRefreshingToken) {
			this.isRefreshingToken = true;

			this.authToken$.next(null);

			return this.tokenService.refreshToken$()
				.pipe(
					take(1),
					switchMap(token => {
						// store the new tokens
						this.tokenService.saveToken(token);
						if (traceOn() && token) {
							console.log('access_token %s expires in %s', token.access_token, token.expires_in);
						}

						this.authToken$.next(token);
						return next.handle(this.tokenService.addToken(req));
					}),
					catchError(response => {
						if (traceOn()) {
							console.log ('Error', response);
						}
						return EMPTY;
					}),
					finalize(() => this.isRefreshingToken = false)
				);
		} else {
			return this.authToken$
			.pipe(
				filter(token => token != null)
				, take(1)
				, switchMap(token => {
					return next.handle(this.tokenService.addToken(req));
				})
			);
		}
	}

}
