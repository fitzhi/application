import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import { BAD_REQUEST, INTERNAL_SERVER_ERROR, METHOD_NOT_ALLOWED, NOT_FOUND, NOT_MODIFIED, OK, UNAUTHORIZED } from 'http-status-codes';
import { Observable, throwError } from 'rxjs';
import { catchError, retry, tap } from 'rxjs/operators';
import { Constants } from 'src/app/constants';
import { traceOn } from 'src/app/global';
import { MessageService } from '../../../interaction/message/message.service';

@Injectable({ providedIn: 'root' })
export class HttpErrorInterceptorService implements HttpInterceptor {


	constructor(
		private injector: Injector,
		private router: Router) { }

	intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		return next.handle(request)
			.pipe(
				retry(0),
				tap(response => {
					if (traceOn()) {
						if (response instanceof HttpResponse) {
							// We do not log message if the HTTP request is sucessful
							if (response.status !== OK) {
								console.groupCollapsed ('%s %s', request.method, request.url);
								console.log (response);
								console.groupEnd();
							}
						}
					}
				}),
				catchError((response: HttpErrorResponse) => {
					const messageService = this.injector.get(MessageService);
					let errorMessage: string;

					if (!navigator.onLine) {
						errorMessage = 'No Internet Connection available !';
						setTimeout(() => messageService.warning(errorMessage), 0);
						return throwError(errorMessage);
					}

					// This is not a network error.
					if (response instanceof TypeError) {
						return throwError(response);
					}

					if ((response.error) && response.error.hasOwnProperty('flagApiError')) {
						// Server side error
						const apiError = response.error;
						errorMessage = 'Error: ' + apiError.message;
						setTimeout(() => messageService.error(errorMessage), 0);
						if (traceOn()) {
							console.groupCollapsed ('Error stacktrace');
							console.log(apiError.debugMessage);
							console.groupEnd();
						}
						return throwError(errorMessage);
					}

					if ( (response.error) && (response.error.hasOwnProperty('error')) && (response.error.hasOwnProperty('error_description'))) {
						if (traceOn()) {
							console.groupCollapsed('Catching error');
							console.log ('Error', response.error.error);
							console.log ('Description', response.error.error_description);
							console.groupEnd();
						}
						setTimeout(() => messageService.error(response.error.error_description), 0);
						return throwError(response.error.error_description);
					}

					if (response.error instanceof ErrorEvent) {
						// client-side error
						errorMessage = 'Error: ${error.error.message}';
						setTimeout(() => messageService.error(errorMessage), 0);
						return throwError(errorMessage);
					}

					switch (response.status) {
						case 0:
							messageService.info(Constants.SERVER_DOWN + ' ' + request.url);
							return throwError(Constants.SERVER_DOWN + ' @ ' + request.url);
						case NOT_FOUND:
							if (traceOn()) {
								console.log ('Unreachable URL %s', request.urlWithParams);
							}
							return throwError(response);
						case METHOD_NOT_ALLOWED:
							return throwError(response);
						case NOT_MODIFIED:
							// status code returned by etag filter.
							return throwError(response);
						case BAD_REQUEST:
						case INTERNAL_SERVER_ERROR:
							return throwError(response);
						case UNAUTHORIZED:
							// The code 401 is handled by the 2 other interceptors
							return throwError (response);
						default:
							if (response) {
								if (traceOn()) {
									console.log('Error ' + response.status + ' ' + response.message);
								}
								errorMessage = response.message + ' (' + response.status + ')';
								setTimeout(() => messageService.error(errorMessage), 0);
							}
							return throwError(errorMessage);
					}
				})
			);
		}
	}
