import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { ActivityLog } from 'src/app/data/activity-log';
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
export class SSEWatcherComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * URL of the server
	 */
	@Input() url: string;

	constructor(
		private sunburstCinematicService: SunburstCinematicService,
		public ssewatcherService: SsewatcherService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.sunburstCinematicService.listenEventsFromServer$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.ssewatcherService.initEventSource(this.url);
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
