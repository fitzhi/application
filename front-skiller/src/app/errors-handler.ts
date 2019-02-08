import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { MessageService } from './message/message.service';
import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Constants } from './constants';
import { Router } from '@angular/router';


@Injectable()
export class ErrorsHandler implements ErrorHandler {

        constructor(
                private injector: Injector,
        ) { }

        handleError(error: Error | HttpErrorResponse) {

                const messageService = this.injector.get(MessageService);
                const router = this.injector.get(Router);

                if (error instanceof HttpErrorResponse) {
                        // Server or connection error happened.
                        if (!navigator.onLine) {
                                // Handle offline error
                                console.log('off line');
                                messageService.error('No Internet Connection');
                        } else {
                                // The 404 error can be thrown from the back-end server for good reason, with its own appropriate message.
                                if (error.status === 404) {
                                        const return_code = error.headers.get('backend.return_code');
                                        if (typeof return_code !== 'undefined') {
                                                if (Constants.DEBUG) {
                                                        console.log('Error 404 with back-end message ' + return_code);
                                                }
                                                throw error;
                                        } else {
                                                console.log(error.status + ' - ' + error.message);
                                                return messageService.error(error.status + ' - ' + error.message);
                                        }
                                } else {
                                        return messageService.error(error.status + ' - ' + error.message);
                                }
                        }
                        messageService.error(error.status + ' - ' + error.message);
                        if (Constants.DEBUG) {
                                console.log (error.message);
                        }
                        messageService.error(error.status + ' - ' + error.message);
                } else {
                        console.error('ERROR : ', error);
                        router.navigate(['/error'], { queryParams: { error: error } });
                }
                // Log the error anyway
                console.error('It happens: ', error);
        }
}
