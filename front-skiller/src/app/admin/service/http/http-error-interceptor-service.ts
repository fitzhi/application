import { retry, catchError } from 'rxjs/operators';
import { HttpHandler, HttpEvent, HttpErrorResponse, HttpRequest, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Injectable, Injector } from '@angular/core';
import { MessageService } from '../../../message/message.service';
import { Constants } from '../../../constants';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class HttpErrorInterceptorService implements HttpInterceptor {


	constructor(
		private injector: Injector,
		private router: Router) { }

	intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		return next.handle(request)
			.pipe(
				retry(0),
				catchError((error: HttpErrorResponse) => {
					const messageService = this.injector.get(MessageService);
					let errorMessage: string;

					if (!navigator.onLine) {
						errorMessage = 'No Internet Connection available !';
						setTimeout(() => messageService.warning(errorMessage), 0);
						return throwError(errorMessage);
					}

					if (error.error instanceof ErrorEvent) {
						// client-side error
						errorMessage = 'Error: ${error.error.message}';
						setTimeout(() => messageService.error(errorMessage), 0);
						return throwError(errorMessage);
					}

					switch (error.status) {
						case 0:
							setTimeout(() => messageService.error("Server is down or unreachable!"), 0);
							break;
						// The 404 error can be thrown from the back-end server for good reason,
						// with its own appropriate message.
						case 400:
						case 404:
						case 500:
							const return_code = error.headers.get('backend.return_code');
							if ( (return_code !== null) && (return_code !== undefined) ) {
								const return_message = error.headers.get('backend.return_message');
								if (Constants.DEBUG) {
									console.log('Error ' + error.status
										+ ' with back-end error code/message '
										+ return_code + '/' + return_message);
								}
								errorMessage = return_message;
							} else {
								if (Constants.DEBUG) {
									console.log('Error ' + error.status + ' ' + error.message);
								}
								if ( (error.error !== null) &&  (error.error !== undefined) ) {
									errorMessage = error.error.message + ' (' + error.error.code + ')';
								} else {
									errorMessage = error.message + ' (' + error.status + ')';
								}
							}
							setTimeout(() => messageService.warning(errorMessage), 0);
							return throwError(errorMessage);
						case 401:
							if (Constants.DEBUG) {
								console.log(error.error, error.error.error_description);
							}
							setTimeout(() => messageService.error(error.error.error_description), 0);
							if (error.error === 'invalid_token') {
								setTimeout(() => this.router.navigate(['/welcome']), 0);
							}
							break;
						default:
							if (Constants.DEBUG) {
								console.log(error);
							}
							if (error !== null) {
								if (Constants.DEBUG) {
									console.log('Error ' + error.status + ' ' + error.message);
								}
								errorMessage = error.message + ' (' + error.status + ')';
								setTimeout(() => messageService.error(errorMessage), 0);
							}
							return throwError(errorMessage);

					}
				})
			);
	}
}
