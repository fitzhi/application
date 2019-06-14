
import { throwError as observableThrowError, Observable, BehaviorSubject } from 'rxjs';

import { take, filter, catchError, switchMap, finalize } from 'rxjs/operators';
import { Injectable, Injector } from "@angular/core";
import { HttpInterceptor, HttpRequest, HttpHandler, HttpSentEvent, HttpHeaderResponse, HttpProgressEvent, HttpResponse, HttpUserEvent, HttpErrorResponse } from "@angular/common/http";

import { AuthService } from "../auth/auth.service";

@Injectable()
export class httpTokenInterceptorService implements HttpInterceptor {

    isRefreshingToken: boolean = false;
    tokenSubject: BehaviorSubject<string> = new BehaviorSubject<string>(null);

    constructor(private injector: Injector) { }

    addToken(req: HttpRequest<any>, token: string): HttpRequest<any> {
        if (typeof token !== 'undefined') {
            return req.clone({ setHeaders: { Authorization: 'Bearer ' + token } });
        } else {
            return req;
        }
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {
        const authService = this.injector.get(AuthService);

        if (req.url.includes('/referential/') || req.url.includes('/skill/all') || req.url.includes('/oauth/token')) {
            return next.handle(req);
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
                catchError(error => {
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

        return observableThrowError("");
    }
}
