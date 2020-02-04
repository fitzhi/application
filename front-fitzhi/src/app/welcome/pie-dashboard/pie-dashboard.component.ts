import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { PieDashboardService } from './service/pie-dashboard.service';
import { BaseComponent } from 'src/app/base/base.component';
import { ProjectService } from 'src/app/service/project.service';

@Component({
	selector: 'app-pie-dashboard',
	templateUrl: './pie-dashboard.component.html',
	styleUrls: ['./pie-dashboard.component.css']
})
export class PieDashboardComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Available buttons.
	 */
	private selection  = {
		none: 0,
		lastMonthSummary: 1,
		lastYearSummary: 2,
		currentSummary: 3
	};

	/**
	 * Selected button
	 */
	private selected = this.selection.none;

	private pieIdentifier = {
		lastMonth: 1,
		lastYear: 2,
		current: 3,
		lastMonthMinimized: 4,
		lastYearMinimized: 5,
		currentMinimized: 6
	};

	constructor(
		private projectService: ProjectService,
		private pieDashboardService: PieDashboardService) {
			super();
	}

	/**
	 * Initialization.
	 */
	ngOnInit() {
		this.subscriptions.add(
			this.projectService.allProjectsIsLoaded$.subscribe ({
				next: loadAndOk => {
					if (loadAndOk) {
						this.pieDashboardService.generatePieSlices(this.projectService.allProjects);
					}
				},
			}));
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

	lastYearMinimized(): boolean {
		return true;
	}

	/**
	 * Switch to a summary.
	 * @param selection the new Selected button
	 */
	switchTo(selection: number) {
		this.selected = selection;
	}

	/**
	 * Return `true` if the given selection is selected.
	 * @param selection the new selection
	 */
	isSelected(selection: number): boolean {
		return (selection === this.selected);
	}
}
