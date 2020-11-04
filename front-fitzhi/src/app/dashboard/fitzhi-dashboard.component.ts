import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { PieDashboardService } from './service/pie-dashboard.service';
import { BaseComponent } from 'src/app/base/base.component';
import { ProjectService } from 'src/app/service/project.service';
import { Constants } from 'src/app/constants';
import { selection } from './selection';
import { FitzhiDashboardPopupHelper } from './fitzhi-dashboard-popup-helper';
import { SkylineService } from './skyline/service/skyline.service';
import { Building } from 'rising-skyline';

@Component({
	selector: 'app-fitzhi-dashboard',
	templateUrl: './fitzhi-dashboard.component.html',
	styleUrls: ['./fitzhi-dashboard.component.css']
})
export class FitzhiDashboardComponent extends BaseComponent implements OnInit, OnDestroy {

	public selection = selection;

	/**
	 * Selected button. End-user has clicked on it.
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

	/**
	 * Helper handler the display or not of the poppup.
	 */
	public popupHelper = new FitzhiDashboardPopupHelper();

	public viewTreeMap = [600, 400];

	public colors = Constants.COLORS;

	constructor(
		public projectService: ProjectService,
		public skylineService: SkylineService,
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

		this.subscriptions.add(
			this.skylineService.loadSkykine$().subscribe({
			next: skyline => {
				const building: Building[] = [];
				this.skylineService.skylineLoaded$.next(true);
			}
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
	 * @param clickedselection the new Selected button
	 */
	switchTo(clickedselection: number) {
		this.selected = clickedselection;
	}

	/**
	 * Return `true` if the given selection is selected.
	 * @param clickedSelection the new selection
	 */
	isSelected(clickedSelection: number): boolean {
		return (clickedSelection === this.selected);
	}

}
