import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { MessageService } from './message/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Constants } from './constants';
import { Router } from '@angular/router';


@Injectable()
export class CustomErrorHandler implements ErrorHandler {

    private msgService: MessageService = null;

    constructor(
        private injector: Injector) { }

    handleError(error: Error | HttpErrorResponse) {

        const router = this.injector.get(Router);

        if (error instanceof HttpErrorResponse) {
            // Server or connection error happened.
            if (!navigator.onLine) {
                // Handle offline error
                console.error('Navigator is off line');
                this.messageService().error('No Internet Connection');
            } else {
                switch (error.status) {
                    // The 404 error can be thrown from the back-end server for good reason, with its own appropriate message.
                    case 404:
                    case 500:
                        const return_code = error.headers.get('backend.return_code');
                        if (typeof return_code !== 'undefined') {
                            const return_message = error.headers.get('backend.return_message');
                            if (Constants.DEBUG) {
                                console.log('Error ' + error.status
                                + ' with back-end error code/message '
                                + return_code + '/' + return_message);
                            }
                            this.messageService().warning(return_message + '(' + return_code + ')');
                        } else {
                            this.displayError(error);
                        }
                        break;
                    default:
                        this.displayError(error);
                        break;
                }
            }
        } else {
            console.error('Critical error', error.message);
            router.navigate(['/error'], { queryParams: { error: error } });
        }
    }

    /**
     * Display an error message.
     * @param error the http error response received by the application.
     */
    displayError(error: HttpErrorResponse) {
        if (Constants.DEBUG) {
            console.log('error.message', error.message);
        }
        this.messageService().error(error.message + ' (' + error.status + ')');
    }


    messageService() {
        if (this.msgService == null) {
            this.msgService = this.injector.get(MessageService);
        }
        return this.msgService;
    }

}
