
import { HttpErrorResponse, HttpHandler, HttpHeaderResponse, HttpInterceptor, HttpProgressEvent, HttpRequest, HttpResponse, HttpSentEvent, HttpUserEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { EMPTY, Observable, of, throwError as observableThrowError, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { traceOn } from 'src/app/global';

@Injectable()
export class HttpRefreshTokenErrorInterceptorService implements HttpInterceptor {

	private REFRESH_TOKEN_EXPIRED = 'Invalid refresh token (expired)';

	private LENGTH_REFRESH_TOKEN_EXPIRED = this.REFRESH_TOKEN_EXPIRED.length;

	constructor(private router: Router) { }

	intercept(req: HttpRequest<any>, next: HttpHandler):
		Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {

			return next.handle(req).pipe(
				catchError( (response: HttpErrorResponse) => {
					if ((	response.status === 401) && 
							(response.error.error) && (response.error.error === 'invalid_token') && 
							(response.error.error_description) && (response.error.error_description.substring(0, this.LENGTH_REFRESH_TOKEN_EXPIRED) === this.REFRESH_TOKEN_EXPIRED)) {
						if (traceOn()) {
							console.log('Refresh token has expired.')
						}
						this.router.navigate(['/login']);
						return throwError(response);
					} else {
						return throwError(response);
					}
				})
			);
	}

}
