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
				catchError((response: HttpErrorResponse) => {
					const messageService = this.injector.get(MessageService);
					let errorMessage: string;

					if (!navigator.onLine) {
						errorMessage = 'No Internet Connection available !';
						setTimeout(() => messageService.warning(errorMessage), 0);
						return throwError(errorMessage);
					}

					if (response.error instanceof ErrorEvent) {
						// client-side error
						errorMessage = 'Error: ${error.error.message}';
						setTimeout(() => messageService.error(errorMessage), 0);
						return throwError(errorMessage);
					}

					switch (response.status) {
						case 0:
							setTimeout(() => messageService.error('Server is down or unreachable!'), 0);
							return throwError('Server is down or unreachable!');
							break;
						case 404:
							setTimeout(() => messageService.error('Unreachable URL'), 0);
							return throwError(response);
							break;
						case 400:
						case 500:
							const return_code = response.headers.get('backend.return_code');
							if (return_code) {
								const return_message = response.headers.get('backend.return_message');
								if (Constants.DEBUG) {
									console.log('Error ' + response.status
										+ ' with back-end error code/message '
										+ return_code + '/' + return_message);
								}
								errorMessage = return_message;
							} else {
								if (Constants.DEBUG) {
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
							if (Constants.DEBUG) {
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
							if (Constants.DEBUG) {
								console.log(response);
							}
							if (response) {
								if (Constants.DEBUG) {
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
