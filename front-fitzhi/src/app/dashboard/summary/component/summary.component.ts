import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Project } from 'src/app/data/project';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { FitzhiDashboardPopupHelper } from '../../fitzhi-dashboard-popup-helper';
import { selection } from '../../selection';
import { SummaryService } from '../service/summary.service';

@Component({
	selector: 'app-summary',
	templateUrl: './summary.component.html',
	styleUrls: ['./summary.component.css']
})
export class SummaryComponent extends BaseDirective implements OnInit, OnDestroy {

	public selection = selection;

	public projectsEvaluation = 0;

	/**
	 * Helper handler the display or not of the poppup.
	 */
	public popupHelper = new FitzhiDashboardPopupHelper();

	constructor(
		public summaryService: SummaryService,
		public dashboardService: DashboardService,
		public staffListService: StaffListService,
		public projectService: ProjectService) {
			super();
		}

	ngOnInit(): void {
		this.subscriptions.add(
			this.projectService.allProjectsIsLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.summaryService.showGeneralAverage();
					}
				}
			})
		);

		this.subscriptions.add(
			this.summaryService.generalAverage$.subscribe({
				next: evaluation => this.projectsEvaluation = Math.floor(evaluation * 10)
			})
		);
	}

	/**
	 * @returns **true** if the mouse is moving over the general average panel.
	 */
	hasGeneralAverage() {
		return (this.popupHelper.isButtonActivated(selection.generalAverage));
	}

	/**
	 * @returns **true** if the mouse is moving over the skills coverage panel.
	 */
	hasSkillsCoverageScore() {
		return (this.popupHelper.isButtonActivated(selection.skillsCoverageScore));
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
