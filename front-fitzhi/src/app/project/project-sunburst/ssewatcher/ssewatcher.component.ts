import { Component, Input, NgZone, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { traceOn } from 'src/app/global';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { ProjectService } from 'src/app/service/project.service';
import { ActivityLog } from 'src/app/data/activity-log';
import { registerLocaleData } from '@angular/common';
import { MessageService } from 'src/app/message/message.service';

/**
* This component will listen the events from a given Server.
*/
@Component({
	selector: 'app-ssewatcher',
	templateUrl: './ssewatcher.component.html',
	styleUrls: ['./ssewatcher.component.css']
})
export class SSEWatcherComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * URL of the server
	 */
	@Input() url: string;

	/**
	 * This `observable` will emit a `TRUE` to start the listening of server side events.
	 */
	@Input() listenEventsFromServer$ = new Subject<boolean>();

	/**
	 * event$.
	 */
	private event$ = new Subject<ActivityLog>();

	/**
	 * The sources of the log events sent by the server.
	 */
	private eventSource: EventSource;

	constructor(
		private backendSetupService: BackendSetupService,
		private messageService: MessageService,
		private zone: NgZone,
		private projectService: ProjectService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.listenEventsFromServer$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.eventSource = this.listenServer();
					}
			}}));
	}

	/**
	 * Return the eventSource listening the tasks stream.
	 *
	 * This method is listening the server side event from the server.
	 */
	listenServer(): EventSource {

		const completeUrl = this.backendSetupService.url() + this.url + this.projectService.project.id;
		if (traceOn()) {
			console.log('starting to listen events from %s', completeUrl);
		}
		const eventSource = new EventSource(completeUrl);

		eventSource.onmessage = (event: MessageEvent) => this.handleEvent(event);

		// from now, we chocke the error emitted by the eventSource
		eventSource.onerror = (error: Event) => this.handleError(error);

		return eventSource;
	}

	/**
	 * Handle a nominal event received.
	 * @param messageEvent the nominal **messageEvent**.
	 */
	handleEvent(messageEvent: MessageEvent) {
		const activityLog: ActivityLog = new ActivityLog(JSON.parse(messageEvent.data));
		// We need to execute the work INSIDE the Angular zone.
		if (traceOn()) {
			console.log('Event message : ', activityLog.message);
		}
		this.zone.run(() => {
			if (activityLog.isKo()) {
				this.messageService.error(activityLog.message);
				if (activityLog.completeOnError) {
					this.closeEventSource();
				}
			}
			if (activityLog.isOk()) {
				if (activityLog.complete) {
					this.messageService.info(activityLog.message);
					this.closeEventSource();
				} else {
					this.event$.next(activityLog);
				}
			}
		});
	}

	/**
	 * Handle an **ERROR** event received
	 * @param error the **event** on ERROR received.
	 */
	handleError(error: Event) {
		if (traceOn()) {
			console.log('Error emitted', error);
		}
		this.closeEventSource();
	}

	/**
	 * Close the EventSource.
	 */
	closeEventSource() {
		if (this.eventSource) {
			this.eventSource.close();
		}
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		this.closeEventSource();
		super.ngOnDestroy();
	}
}
