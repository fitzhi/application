import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { SseListenerService } from './../../../service/sse-listener.service';
import { ProjectService } from 'src/app/service/project.service';
import { Subject, EMPTY } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { switchMap, tap } from 'rxjs/operators';
import { Task } from 'src/app/data/task';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { traceOn } from 'src/app/global';

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
	@Input() listen$ = new Subject<boolean>();

	/**
	 * data$.
	 */
	private data$ = new Subject<String>();

	constructor(
		private listener: SseListenerService,
		private backendSetupService: BackendSetupService,
		private projectService: ProjectService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.listen$.subscribe({
				next: doneAndOk => this.listenServer()
			}));
	}

	/**
	 * This methid is listening the server side event from the server.
	 */
	listenServer() {
		if (traceOn()) {
			console.log ('starting to listen events from %s', this.url);
		}
		this.subscriptions.add(
			this.projectService.projectLoaded$.pipe(
				switchMap(loaded => {
					return (loaded) ?
						this.listener.getServerSentEvent$(this.backendSetupService.url() + this.url + this.projectService.project.id) :  EMPTY;
					}
				)).pipe(
					tap((task: Task) => console.log (task.title))
				).subscribe({
					next: (task: Task) => this.data$.next(task.title)
				}));
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		this.listen$.next(false);
		super.ngOnDestroy();
	}

}
