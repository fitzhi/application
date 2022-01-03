import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { ProjectService } from 'src/app/service/project/project.service';
import { SunburstCinematicService } from '../service/sunburst-cinematic.service';
import { SsewatcherService } from './service/ssewatcher.service';

/**
* This component will listen the events from a given Server.
*/
@Component({
	selector: 'app-ssewatcher',
	templateUrl: './ssewatcher.component.html',
	styleUrls: ['./ssewatcher.component.css']
})
export class SSEWatcherComponent extends BaseDirective implements OnInit, OnDestroy {

	constructor(
		private projectService: ProjectService,
		private sunburstCinematicService: SunburstCinematicService,
		public ssewatcherService: SsewatcherService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.sunburstCinematicService.listenEventsFromServer$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.ssewatcherService.initEventSource(`/project/${this.projectService.project.id}/tasks/stream/dashboardGeneration`);
					}
				}
			})
		);

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
		this.ssewatcherService.closeEventSource();
		super.ngOnDestroy();
	}
}
