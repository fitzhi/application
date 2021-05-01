
import { HttpErrorResponse, HttpHandler, HttpHeaderResponse, HttpInterceptor, HttpProgressEvent, HttpRequest, HttpResponse, HttpSentEvent, HttpUserEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { EMPTY, Observable, of, throwError as observableThrowError, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';

@Injectable()
export class HttpRefreshTokenErrorInterceptor implements HttpInterceptor {

	private INVALID_TOKEN = 'invalid_token';

	private REFRESH_TOKEN_EXPIRED = 'Invalid refresh token (expired)';

	private ACCESS_TOKEN_EXPIRED = 'Access token expired';

	private LENGTH_ACCESS_TOKEN_EXPIRED = this.ACCESS_TOKEN_EXPIRED.length;

	private UNAUTHORIZED = 'unauthorized';

	// https://developer.mozilla.org/fr/docs/Web/HTTP/Headers/WWW-Authenticate
	private WWW_AUTHENTICATE = 'WWW-Authenticate';

	private WWW_AUTHENTICATE_INVALID_REFRESH_TOKEN = 'Bearer error="invalid_token", error_description="Invalid refresh token (expired):';

	private WWW_AUTHENTICATE_INVALID_ACCESS_TOKEN = 'Bearer realm="my_rest_api", error="invalid_token", error_description="Access token expired';

	private WWW_AUTHENTICATE_FULL_AUTHENTICATION = 'Bearer realm="my_rest_api", error="unauthorized", error_description="Full authentication is required to access this resource"';
		
	private WWW_AUTHENTICATE_FULL_INVALID_ACCESS_TOKEN = 'Bearer realm="my_rest_api", error="invalid_token", error_description="Invalid access token:"'

	// We currently don't test the error description.
	//private FULL_AUTHORIZATION_IS_REQUIRED = 'Full authentication is required to access this resource';

	constructor(private router: Router, private messageService: MessageService) { }

	/**
	 * Extract the ***www-authenticate*** header from response.
	 * 
	 * It looks like 2 case-sensitive model exist : WWW-Authenticate and www-authenticate.
	 * @param response the given HttpErrorResponse
	 * @returns the header value, or **null** if non exists.
	 */
	private extractWwwAuthenticate(response: HttpErrorResponse) {
		let wwwAuthenticate = response.headers.get(this.WWW_AUTHENTICATE);
		if (!wwwAuthenticate) {
			wwwAuthenticate = response.headers.get(this.WWW_AUTHENTICATE.toLowerCase());
		}
		if (traceOn()) {
			console.log ('www-authenticate found : %s', wwwAuthenticate);
		}
		return wwwAuthenticate;
	}


	intercept(req: HttpRequest<any>, next: HttpHandler):
		Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {

			return next.handle(req).pipe(
				catchError( (response: HttpErrorResponse) => {

					if (response.status === 401) { 
						const wwwAuthenticate = this.extractWwwAuthenticate(response);
						if (wwwAuthenticate) {
							// This is the scenario of the EXPIRED refresh token.
							if (	wwwAuthenticate.includes(this.WWW_AUTHENTICATE_INVALID_REFRESH_TOKEN)
								|| 	wwwAuthenticate.includes(this.WWW_AUTHENTICATE_FULL_INVALID_ACCESS_TOKEN) 
								|| 	wwwAuthenticate.includes(this.WWW_AUTHENTICATE_FULL_AUTHENTICATION) ){
								this.router.navigate(['/login']);
								return throwError(response);
							}

							// This is the scenario of the ACCESSS refresh token.
							if (wwwAuthenticate.includes(this.WWW_AUTHENTICATE_INVALID_ACCESS_TOKEN)) {
								if (traceOn()) {
									console.log('Access token has expired.')
								}		
								return throwError(response);
							}

							if (traceOn()) {
								console.groupCollapsed('Unexpected error.')
								console.log(response);
								console.groupEnd();
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
