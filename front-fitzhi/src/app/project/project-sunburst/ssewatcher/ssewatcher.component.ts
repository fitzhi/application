import { Component, Input, NgZone, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { traceOn } from 'src/app/global';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { ProjectService } from 'src/app/service/project.service';
import { ActivityLog } from 'src/app/data/activity-log';

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

		eventSource.onmessage = (sse: MessageEvent) => {
			const activityLog: ActivityLog = new ActivityLog(JSON.parse(sse.data));
			// We need to execute the work INSIDE the Angular zone.
			if (traceOn()) {
				console.log('Event message : ', activityLog.message);
			}
			this.zone.run(() => this.event$.next(activityLog));
		};

		// from now, we chocke the error emitted by the eventSource
		eventSource.onerror = err => {
			if (traceOn()) {
				console.error('Error emitted', err);
			}
		};

		return eventSource;
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		if (this.eventSource) {
			this.eventSource.close();
		}
		super.ngOnDestroy();
	}

}
