import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { PieDashboardService } from './service/pie-dashboard.service';
import { BaseComponent } from 'src/app/base/base.component';
import { ProjectService } from 'src/app/service/project.service';
import { Constants } from 'src/app/constants';

@Component({
	selector: 'app-fitzhi-dashboard',
	templateUrl: './fitzhi-dashboard.component.html',
	styleUrls: ['./fitzhi-dashboard.component.css']
})
export class PieDashboardComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Available buttons.
	 */
	public selection  = {
		none: 0,
		lastMonthSummary: 1,
		lastYearSummary: 2,
		currentSummary: 3,
		treeMapSummary: 4
	};

	/**
	 * Selected button
	 */
	public selected = this.selection.none;

	public pieIdentifier = {
		lastMonth: 1,
		lastYear: 2,
		current: 3,
		lastMonthMinimized: 4,
		lastYearMinimized: 5,
		currentMinimized: 6
	};


	public viewTreeMap = [600, 400];

	public colors = Constants.COLORS;

	constructor(
		public projectService: ProjectService,
		public pieDashboardService: PieDashboardService) {
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

	rectColor(index: Number) {
		return 
	}
}
