import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { traceOn } from 'src/app/global';
import { SsewatcherService } from '../ssewatcher/service/ssewatcher.service';

@Component({
	selector: 'app-chart-in-progress',
	templateUrl: './chart-in-progress.component.html',
	styleUrls: ['./chart-in-progress.component.css']
})
export class ChartInProgressComponent extends BaseDirective implements OnInit, OnDestroy {

	public progressionPercentage = 0;

	constructor(public ssewatcherService: SsewatcherService) { super(); }

	ngOnInit(): void {
		this.subscriptions.add(
			this.ssewatcherService.event$.subscribe({
				next: activityLog => {
						if (traceOn()) {
							console.log ('Update the progress bar value to %d', activityLog.progressionPercentage);
						}
						this.progressionPercentage = activityLog.progressionPercentage;
					},
			}));
	}

	public url() {
		return `/project/{}/tasks/stream/dashboardGeneration/`;
	}
	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
