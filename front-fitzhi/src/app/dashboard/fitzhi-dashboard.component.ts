import { Component, OnDestroy, OnInit } from '@angular/core';
import { ControlledRisingSkylineService } from 'controlled-rising-skyline';
import { switchMap, take } from 'rxjs/operators';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { ProjectService } from 'src/app/service/project.service';
import { FitzhiDashboardPopupHelper } from './fitzhi-dashboard-popup-helper';
import { selection } from './selection';
import { PieDashboardService } from './service/pie-dashboard.service';
import { SkylineService } from './skyline/service/skyline.service';

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

	/**
	 * Height of the control panel below the skyline
	 */
	private heughtControlPanel = 50;

	/**
	 * Dimension of the Skyline
	 */
	skylineDimension = {
		width: 1200,
		height: 370,
	};

	constructor(
		public projectService: ProjectService,
		public skylineService: SkylineService,
		public controlledRisingSkylineService: ControlledRisingSkylineService,
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
			this.skylineService
				.loadSkyline$(this.skylineDimension.width, this.skylineDimension.height)
				.pipe(take(1))
				.subscribe({
					next: skyline => {
						// this.controlledRisingSkylineService.randomSkylineHistory(this.skylineService.skyline$);
						this.skylineService.loadSkyline$(this.skylineDimension.width, this.skylineDimension.height - this.heughtControlPanel)						
						this.skylineService.skylineLoaded$.next(true);
				}
		}));

		this.skylineService.skyline$.subscribe({
			next: skyline => {
				console.table(skyline);
			}
		})
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
