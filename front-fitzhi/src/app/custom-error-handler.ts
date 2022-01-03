import { HttpErrorResponse } from '@angular/common/http';
import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import { traceOn } from './global';
import { MessageService } from './interaction/message/message.service';


@Injectable()
export class CustomErrorHandler implements ErrorHandler {

	constructor(
		private injector: Injector) {  }

	handleError(error: Error | HttpErrorResponse) {

		const router = this.injector.get(Router);
		const messageService = this.injector.get(MessageService);

		if (error instanceof HttpErrorResponse) {
			// Server or connection error happened.
			if (!navigator.onLine) {
				// Handle offline error
				messageService.error('No Internet Connection');
			} else {
				switch (error.status) {
					// The 404 error can be thrown from the back-end server for good reason, with its own appropriate message.
					case 404:
					case 500:
						const return_code = error.headers.get('backend.return_code');
						if (typeof return_code !== 'undefined') {
							const return_message = error.headers.get('backend.return_message');
							if (traceOn()) {
								console.log('Error ' + error.status
								+ ' with back-end error code/message '
								+ return_code + '/' + return_message);
							}
							setTimeout(() => messageService.error(return_message + '(' + return_code + ')'), 0);
						} else {
							messageService.error(error.message + ' (' + error.status + ')');
						}
						break;
					default:
						messageService.error(error.message + ' (' + error.status + ')');
						break;
				}
			}
		} else {
			console.error('Critical error', error.message);
			router.navigate(['/error'], { queryParams: { error: error } });
		}
		throw error;
	}

}
