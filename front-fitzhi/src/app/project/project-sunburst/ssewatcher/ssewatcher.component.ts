import { Component, Input, NgZone, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { traceOn } from 'src/app/global';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { ProjectService } from 'src/app/service/project.service';
import { ActivityLog } from 'src/app/data/activity-log';
import { MessageService } from 'src/app/interaction/message/message.service';
import { SunburstCinematicService } from '../service/sunburst-cinematic.service';
import { FindValueOperator } from 'rxjs/internal/operators/find';

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
	 * event$.
	 */
	public event$ = new Subject<ActivityLog>();

	/**
	 * The sources of the log events sent by the server.
	 */
	private eventSource: EventSource;

	constructor(
		private backendSetupService: BackendSetupService,
		private sunburstCinematicService: SunburstCinematicService,
		private messageService: MessageService,
		private zone: NgZone,
		private projectService: ProjectService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.sunburstCinematicService.listenEventsFromServer$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						// We do not allow to open simultaneouly more than ONE server events listener.
						if ((this.eventSource) && (this.eventSource.readyState === EventSource.OPEN)) {
							this.closeEventSource();
						}
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
			console.log('starting to listen events @ ' + this.url + this.projectService.project.id);
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
		this.closeEventSource();
	}

	/**
	 * Close the EventSource.
	 */
	closeEventSource() {
		if (this.eventSource) {
			if (traceOn()) {
				console.log('Ending to listen events @ ' + this.url + this.projectService.project.id);
			}
			this.eventSource.close();
			this.eventSource = null;
		}
	}

	/**
	 * Refresh the Sunburst chart.
	 */
	refreshChart() {
		this.sunburstCinematicService.refreshChart$.next(true);
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		this.closeEventSource();
		super.ngOnDestroy();
	}
}
