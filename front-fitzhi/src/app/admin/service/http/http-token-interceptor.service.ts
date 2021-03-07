
import { HttpErrorResponse, HttpHandler, HttpHeaderResponse, HttpInterceptor, HttpProgressEvent, HttpRequest, HttpResponse, HttpSentEvent, HttpUserEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, EMPTY, Observable, throwError as observableThrowError, throwError } from 'rxjs';
import { catchError, filter, finalize, switchMap, take } from 'rxjs/operators';
import { Token } from '../token/token';
import { TokenService } from '../token/token.service';

@Injectable()
export class HttpTokenInterceptorService implements HttpInterceptor {

	authToken$: BehaviorSubject<Token> = new BehaviorSubject<Token>(null);

	isRefreshingToken = false;

	constructor(
		private tokenService: TokenService) { }

	intercept(req: HttpRequest<any>, next: HttpHandler):
		Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {



		//
		// FOR DEVELOPMENT PURPOSE ONLY, we unplugg the security control.
		//
		if (localStorage.getItem('dev') === '1') {
			return next.handle(req);
		}
		
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
			|| req.url.includes('api.github.com')) {

			return next.handle(req);
		}

		return next.handle(this.tokenService.addToken(req)).pipe(
			catchError(response => {
				if (response instanceof HttpErrorResponse) {
					if (response.status === 401) {
						if (!this.isConnectionRequest(req)) {
							return this.retryAfterRefresh$(req, next);
						} else {
							console.log ('Invalid authentification credentials');
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
			
			console.log ('retryAfterRefresh(...)');
			this.authToken$.next(null);

			return this.tokenService.refreshToken$()
				.pipe(
					take(1),
					switchMap(token => {
						// store the new tokens
						this.tokenService.saveToken(token);
						if (token) {
							console.log('access_token %s expires in %s', token.access_token, token.expires_in);
						}

						this.authToken$.next(token);
                        return next.handle(this.tokenService.addToken(req));
					})
					,catchError(response => {
						console.log ('Error', response);
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
