
import { HttpErrorResponse, HttpHandler, HttpHeaderResponse, HttpInterceptor, HttpProgressEvent, HttpRequest, HttpResponse, HttpSentEvent, HttpUserEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { EMPTY, Observable, of, throwError as observableThrowError, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';

@Injectable()
export class HttpRefreshTokenErrorInterceptorService implements HttpInterceptor {

	private INVALID_TOKEN = 'invalid_token';

	private REFRESH_TOKEN_EXPIRED = 'Invalid refresh token (expired)';

	private LENGTH_REFRESH_TOKEN_EXPIRED = this.REFRESH_TOKEN_EXPIRED.length;

	private ACCESS_TOKEN_EXPIRED = 'Access token expired';

	private LENGTH_ACCESS_TOKEN_EXPIRED = this.ACCESS_TOKEN_EXPIRED.length;

	private UNAUTHORIZED = 'unauthorized';

	// We currently don't test the error description.
	//private FULL_AUTHORIZATION_IS_REQUIRED = 'Full authentication is required to access this resource';

	constructor(private router: Router, private messageService: MessageService) { }

	intercept(req: HttpRequest<any>, next: HttpHandler):
		Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {

			return next.handle(req).pipe(
				catchError( (response: HttpErrorResponse) => {

					if (response.status === 401) { 
						if	((response.error) && (response.error.error) && (response.error.error === this.INVALID_TOKEN)) { 
							// This is the scenario of the EXPIRED refresh token.
							if	((response.error.error_description) && 
								 (response.error.error_description.indexOf(this.LENGTH_REFRESH_TOKEN_EXPIRED) === 0)
							) {
								if (traceOn()) {
									console.log('Error 401 with message %s', response.error.error_description);
								}
								this.router.navigate(['/login']);
								return throwError(response);
							} else {
								// This is the scenario of the EXPIRED access token.
								if	((response.error.error_description) 
									&& (response.error.error_description.substring(0, this.LENGTH_ACCESS_TOKEN_EXPIRED) === this.ACCESS_TOKEN_EXPIRED)) {
									if (traceOn()) {
										console.log('Access token has expired.')
									}		
								} else {
									if (traceOn()) {
										console.log('Unexpected error.')
									}		
								}
							}
							return throwError(response);
						} else {
							// This is an Unauthorized error, most probably with the Sonar servers
							if (traceOn()) {
								console.log('Response in error', response);
								console.log('for request', req.urlWithParams);
							}
							if (	(response.error) 
								&& 	(response.error.error) 
								&& 	(response.error.error === this.UNAUTHORIZED) 
								&& 	req.url.includes(localStorage.getItem('backendUrl'))) {
									this.router.navigate(['/login']);
									return EMPTY;	
							}
							// This is an unexpected error.
							this.messageService.warning('Unauthorized access to ' + req.urlWithParams);
							return EMPTY;
						}

					} else {
						if (traceOn()) {
							console.groupCollapsed ('Catch %s for %s', response.status, req.urlWithParams);
							console.log (response);
							console.groupEnd();
						}
						return throwError(response);
					}
				})
			);
	}

}
