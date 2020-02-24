
import { throwError as observableThrowError, Observable, BehaviorSubject, EMPTY } from 'rxjs';

import { take, filter, catchError, switchMap, finalize } from 'rxjs/operators';
import { Injectable, Injector } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpSentEvent, HttpHeaderResponse } from '@angular/common/http';
import { HttpProgressEvent, HttpResponse, HttpUserEvent, HttpErrorResponse } from '@angular/common/http';

import { AuthService } from '../auth/auth.service';
import { MessageService } from 'src/app/message/message.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { Constants } from 'src/app/constants';

@Injectable()
export class HttpTokenInterceptorService implements HttpInterceptor {

	isRefreshingToken = false;
	tokenSubject: BehaviorSubject<string> = new BehaviorSubject<string>(null);

	constructor(
		private injector: Injector,
		private messageService: MessageService) { }

	addToken(req: HttpRequest<any>, token: string): HttpRequest<any> {
		if (typeof token !== 'undefined') {
			return req.clone({ setHeaders: { Authorization: 'Bearer ' + token } });
		} else {
			return req;
		}
	}

	intercept(req: HttpRequest<any>, next: HttpHandler):
		Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {

		const authService = this.injector.get(AuthService);

		/**
		 * FOR DEVELOPMENT PURPOSE ONLY we deactivate the security control.
		 */
		if (localStorage.getItem('dev') === '1') {
			return next.handle(req);
		}
		if 	(req.url.includes('/api/referential/')
			|| req.url.includes('/api/skill/all')
			|| req.url.includes('/api/admin/isVeryFirstConnection')
			|| req.url.includes('/api/admin/veryFirstUser')
			|| req.url.includes('/api/admin/register')
			// Sonar URL
			|| req.url.includes('/api/components/search')
			|| req.url.includes('/api/components/tree')
			|| req.url.includes('/api/metrics/search')
			|| req.url.includes('/api/measures/component')
			|| req.url.includes('/api/project_badges/measure')
			|| req.url.includes('/api/server/version')) {
			return next.handle(req);
		}

		if (req.url.includes('/oauth/token')) {
			return next.handle(req).pipe(catchError(error => {
				if (error instanceof HttpErrorResponse) {
					switch ((<HttpErrorResponse>error).status) {
						case 401:
							this.messageService.error('User/password invalid.');
							return EMPTY;
							break;
						}
				}
				console.log ('connection error', error);
				return observableThrowError(error);
			}));
		}

		return next.handle(this.addToken(req, authService.getAccessToken())).pipe(
			catchError(error => {
				if (error instanceof HttpErrorResponse) {
					switch ((<HttpErrorResponse>error).status) {
						case 400:
							return this.handle400Error(error);
						case 401:
							return this.handle401Error(req, next);
						default:
							return observableThrowError(error);
					}
				} else {
					return observableThrowError(error);
				}
			}));
	}

	handle400Error(error) {
		if (error && error.status === 400 && error.error && error.error.error === 'invalid_grant') {
			// If we get a 400 and the error message is 'invalid_grant', the token is no longer valid so logout.
			return this.logoutUser();
		}

		return observableThrowError(error);
	}

	handle401Error(req: HttpRequest<any>, next: HttpHandler) {
		if (!this.isRefreshingToken) {
			this.isRefreshingToken = true;

			// Reset here so that the following requests wait until the token
			// comes back from the refreshToken call.
			this.tokenSubject.next(null);

			const authService = this.injector.get(AuthService);

			return authService.refreshToken().pipe(
				switchMap((newToken: string) => {
					if (newToken) {
						this.tokenSubject.next(newToken);
						return next.handle(this.addToken(req, newToken));
					}

					// If we don't get a new token, we are in trouble so logout.
					return this.logoutUser();
				}),
				catchError(() => {

					// If there is an exception calling 'refreshToken', bad news so logout.
					return this.logoutUser();
				}),
				finalize(() => {
					this.isRefreshingToken = false;
				}));
		} else {
			return this.tokenSubject.pipe(
				filter(token => token != null),
				take(1),
				switchMap(token => {
					return next.handle(this.addToken(req, token));
				}));
		}
	}

	logoutUser() {
		// Route to the login page (implementation up to you)
		return observableThrowError('');
	}
}
