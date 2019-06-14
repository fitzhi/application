import { retry, catchError } from 'rxjs/operators';
import { HttpHandler, HttpEvent, HttpErrorResponse, HttpRequest, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Injectable, Injector } from '@angular/core';
import { MessageService } from '../../../message/message.service';
import { Constants } from '../../../constants';

@Injectable({ providedIn: 'root' })
export class HttpErrorInterceptorService implements HttpInterceptor {


    constructor(private injector: Injector) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request)
            .pipe(
                retry(0),
                catchError((error: HttpErrorResponse) => {
                    const messageService = this.injector.get(MessageService);
                    let errorMessage: string;

                    if (!navigator.onLine) {
                        errorMessage = 'No Internet Connection available !';
                        setTimeout(() => messageService.warning (errorMessage), 0);
                        return throwError(errorMessage);
                    }

                    if (error.error instanceof ErrorEvent) {
                            // client-side error
                            errorMessage = 'Error: ${error.error.message}';
                            setTimeout(() => messageService.error (errorMessage), 0);
                            return throwError(errorMessage);
                    }

                    switch (error.status) {
                            // The 404 error can be thrown from the back-end server for good reason,
                            // with its own appropriate message.
                            case 404:
                            case 500:
                                const return_code = error.headers.get('backend.return_code');
                                if (return_code !== undefined) {
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
                                    errorMessage = error.message + ' (' + error.status + ')';
                                }
                                setTimeout(() => messageService.warning(errorMessage), 0);
                                return throwError(errorMessage);
                            case 401:
                                console.log(error);
                                break;
                            default:
                                console.log (error);
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
