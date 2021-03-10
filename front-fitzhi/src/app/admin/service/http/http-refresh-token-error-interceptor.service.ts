
import { HttpErrorResponse, HttpHandler, HttpHeaderResponse, HttpInterceptor, HttpProgressEvent, HttpRequest, HttpResponse, HttpSentEvent, HttpUserEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { EMPTY, Observable, of, throwError as observableThrowError, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';

@Injectable()
export class HttpRefreshTokenErrorInterceptorService implements HttpInterceptor {

	private REFRESH_TOKEN_EXPIRED = 'Invalid refresh token (expired)';

	private LENGTH_REFRESH_TOKEN_EXPIRED = this.REFRESH_TOKEN_EXPIRED.length;

	constructor(private router: Router, private messageService: MessageService) { }

	intercept(req: HttpRequest<any>, next: HttpHandler):
		Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {

			return next.handle(req).pipe(
				catchError( (response: HttpErrorResponse) => {

					if (response.status === 401) { 
						// This is the scenario of the EXPIRED refresh token.
						if	((response.error) &&
							(response.error.error) && (response.error.error === 'invalid_token') && 
							(response.error.error_description) && (response.error.error_description.substring(0, this.LENGTH_REFRESH_TOKEN_EXPIRED) === this.REFRESH_TOKEN_EXPIRED)) {
							if (traceOn()) {
								console.log('Refresh token has expired.')
							}
							this.router.navigate(['/login']);
							return throwError(response);
						} else {
							// This is an Unauthorized error, most probably with the Sonar servers
							if (traceOn()) {
								console.log('Response in error', response);
								console.log('for request', req.urlWithParams);
							}
							// this.messageService.warning('Unauthorized access to ' + req.urlWithParams);
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
