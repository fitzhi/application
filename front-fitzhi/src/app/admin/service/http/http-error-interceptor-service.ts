import { retry, catchError } from 'rxjs/operators';
import { HttpHandler, HttpEvent, HttpErrorResponse, HttpRequest, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Injectable, Injector } from '@angular/core';
import { MessageService } from '../../../interaction/message/message.service';
import { Router } from '@angular/router';
import { traceOn, HttpCodes } from 'src/app/global';
import { BrowserStack } from 'protractor/built/driverProviders';
import { connectableObservableDescriptor } from 'rxjs/internal/observable/ConnectableObservable';
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
				catchError((response: HttpErrorResponse) => {
					const messageService = this.injector.get(MessageService);
					let errorMessage: string;

					if (!navigator.onLine) {
						errorMessage = 'No Internet Connection available !';
						setTimeout(() => messageService.warning(errorMessage), 0);
						return throwError(errorMessage);
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

					if ( (response.error.hasOwnProperty('error')) && (response.error.hasOwnProperty('error_description'))) {
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
							setTimeout(() => messageService.warning(Constants.SERVER_DOWN), 0);
							return throwError(Constants.SERVER_DOWN);
							break;
						case HttpCodes.notFound:
							if (traceOn()) {
								console.log ('Unreachable URL');
							}
							return throwError(response);
							break;
						case HttpCodes.methodNotAllowed:
							return throwError(response);
							break;
						case 400:
						case 500:
							const return_code = response.headers.get('backend.return_code');
							if (return_code) {
								const return_message = response.headers.get('backend.return_message');
								if (traceOn()) {
									console.log('Error ' + response.status
										+ ' with back-end error code/message '
										+ return_code + '/' + return_message);
								}
								errorMessage = return_message;
							} else {
								if (traceOn()) {
									console.log('Error ' + response.status + ' ' + response.message);
								}
								if ( (response.error !== null) &&  (response.error !== undefined) ) {
									errorMessage = response.error.message + ' (' + response.error.code + ')';
								} else {
									errorMessage = response.message + ' (' + response.status + ')';
								}
							}
							setTimeout(() => messageService.warning(errorMessage), 0);
							return throwError(response);
						case 401:
							if (traceOn()) {
								console.log(response.error.error, response.error.error_description);
							}
							if (response.error.error === 'unauthorized') {
								setTimeout(() => messageService.error('User/password invalid !'), 0);
							} else {
								if (response.error.error === 'invalid_token') {
									setTimeout(() => messageService.error('Your session has expired. Please connect again'), 0);
									setTimeout(() => this.router.navigate(['/welcome']), 0);
								} else {
									setTimeout(() => messageService.error(response.error.error_description), 0);
									return throwError(errorMessage);
								}
							}
							break;
						default:
							if (traceOn()) {
								console.log(response);
							}
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
