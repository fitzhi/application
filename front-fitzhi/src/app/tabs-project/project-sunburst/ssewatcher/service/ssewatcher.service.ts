import { Injectable, NgZone } from '@angular/core';
import { Subject } from 'rxjs';
import { ActivityLog } from 'src/app/data/activity-log';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { ProjectService } from 'src/app/service/project.service';

@Injectable({
	providedIn: 'root'
})
export class SsewatcherService {

	/**
	 * event$.
	 */
	public event$ = new Subject<ActivityLog>();

	/**
	 * The sources of the log events sent by the server.
	 */
	public eventSource: EventSource;

	constructor(
		private backendSetupService: BackendSetupService,
		private zone: NgZone,
		private messageService: MessageService,
		private projectService: ProjectService) { }

	/**
	 * initialize the eventSource for the given URL
	 * @param url the given URL
	 */
	public initEventSource(url: string) {
		// We do not allow to open simultaneouly more than ONE server events listener.
		if ((this.eventSource) && (this.eventSource.readyState === EventSource.OPEN)) {
			this.closeEventSource();
		}
		this.eventSource = this.listenServer(url);
	}

	/**
	 * Return the eventSource listening the tasks stream.
	 *
	 * This method is listening the server side event from the server.
	 */
	public listenServer(url: string): EventSource {

		const completeUrl = this.backendSetupService.url() + url + this.projectService.project.id;
		if (traceOn()) {
			console.log('starting to listen events @ ' + url + this.projectService.project.id);
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
			console.log('Event message : %s, progression %d', activityLog.message, activityLog.progressionPercentage);
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
					this.event$.next(activityLog);
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
		if (error.target instanceof EventSource) {
			// We close the eventSource given by the error event.
			if (traceOn()) {
				console.log ('Closing the eventSource associated with the error event');
			}
			const source = error.target;
			source.close();
			this.eventSource = null;
		} else {
			this.closeEventSource();
		}
	}

	/**
	 * Close the EventSource.
	 */
	closeEventSource() {
		if (this.eventSource) {
			if (traceOn()) {
				console.log('Stop to listen events for project identifier %d', this.projectService.project.id);
			}
			this.eventSource.close();
			this.eventSource = null;
		}
	}

}
