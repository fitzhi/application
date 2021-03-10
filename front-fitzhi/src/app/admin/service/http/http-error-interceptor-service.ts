import { retry, catchError, tap } from 'rxjs/operators';
import { HttpHandler, HttpEvent, HttpErrorResponse, HttpRequest, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Injectable, Injector } from '@angular/core';
import { MessageService } from '../../../interaction/message/message.service';
import { Router } from '@angular/router';
import { traceOn, HttpCodes } from 'src/app/global';
import { Constants } from 'src/app/constants';

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
						console.groupCollapsed ('%s %s', request.method, request.url);
						console.log (response);
						console.groupEnd();
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
							messageService.info(Constants.SERVER_DOWN + " " + request.url);
							return throwError(Constants.SERVER_DOWN + " @ " + request.url);

						case HttpCodes.notFound:
							if (traceOn()) {
								console.log ('Unreachable URL %s', request.urlWithParams);
							}
							return throwError(response);

						case HttpCodes.methodNotAllowed:
							return throwError(response);

						case 400:
						case 500:
							// The system with backend.return_code & backend.return_message has been unplugged
							// We display the error messsage and code to fix the origin
							if (response.headers.get('backend.return_code')) {
								console.log (response.headers.get('backend.return_code'));
								console.log (response.headers.get('backend.return_message'));
								throw new Error('WTF : Should not pass here !');
							}
							return throwError(response);
						
						case 401:
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
