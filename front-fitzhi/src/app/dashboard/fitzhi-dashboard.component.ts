import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ControlledRisingSkylineService } from 'controlled-rising-skyline';
import { BehaviorSubject } from 'rxjs';
import { take } from 'rxjs/operators';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Constants } from 'src/app/constants';
import { ProjectService } from 'src/app/service/project/project.service';
import { FitzhiSettings } from '../data/FitzhiSettings';
import { traceOn } from '../global';
import { ReferentialService } from '../service/referential/referential.service';
import { StaffListService } from '../service/staff-list-service/staff-list.service';
import { FilteredProject } from '../tabs-project/table-projects-filter/filtered-project';
import { FitzhiDashboardPopupHelper } from './fitzhi-dashboard-popup-helper';
import { selection } from './selection';
import { PieDashboardService } from './service/pie-dashboard.service';
import { SkylineService } from './skyline/service/skyline.service';
import { StarfieldService } from './starfield/service/starfield.service';

@Component({
	selector: 'app-fitzhi-dashboard',
	templateUrl: './fitzhi-dashboard.component.html',
	styleUrls: ['./fitzhi-dashboard.component.css']
})
export class FitzhiDashboardComponent extends BaseDirective implements OnInit, OnDestroy {

	public selection = selection;

	public settings = new FitzhiSettings();

	/**
	 * Selected button. End-user has clicked on it.
	 */
	public selected = this.selection.summary;

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


	public colors = Constants.COLORS;

	/**
	 * Height of the control panel below the skyline
	 */
	private heightControlPanel = 50;

	/**
	 * Dimension of the Skyline
	 */
	skylineDimension = {
		width: 1195,
		widthWithUnitOfMesure: '1195px',
		height: 500,
		heightWithUnitOfMesure: '500px',
	};

	/**
	 * BehaviorSubject which emits a **TRUE** if the user clicks on the Skyline icon
	 */
	public skylineSelected$ = new BehaviorSubject<boolean>(false);

	treemapProjectsFilter = false;

	counter = Array;

	constructor(
		public httpClient: HttpClient,
		public projectService: ProjectService,
		public staffListService: StaffListService,
		public referentialService: ReferentialService,
		public skylineService: SkylineService,
		public controlledRisingSkylineService: ControlledRisingSkylineService,
		public starfieldService: StarfieldService,
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
						this.skylineService.loadSkyline$(this.skylineDimension.width, this.skylineDimension.height - this.heightControlPanel);
						this.skylineService.skylineLoaded$.next(true);
				}
		}));

	}

	/**
	 * In progress method...
	 * @returns ALWAYS **true**
	 */
	lastYearMinimized(): boolean {
		return true;
	}

	/**
	 * Switch to a dashboard panel.
	 * @param clickedselection the new Selected button
	 */
	switchTo(clickedselection: number) {
		this.selected = clickedselection;
		this.skylineSelected$.next((this.selected === selection.skyline));
		if (clickedselection === selection.starfield) {
			this.starfieldService.generateAndBroadcastConstellations();
		}
	}

	/**
	 * Return `true` if the given selection is selected.
	 * @param clickedSelection the new selection
	 */
	isSelected(clickedSelection: number): boolean {
		return (clickedSelection === this.selected);
	}

	/**
	 * Return **TRUE** if the user has selected a type of dashboard, **FALSE** otherwise.
	 */
	hasSelectedADashboard(): boolean {
		return (this.selected !== this.selection.summary);
	}

	flip() {
		this.treemapProjectsFilter = !this.treemapProjectsFilter;
	}

	onChangeFileredProjects(projects: FilteredProject[]) {
		if (traceOn()) {
			console.log ('onChangeFileredProjects(...) %d projects', projects.length);
		}
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}

