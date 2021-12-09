import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { FitzhiDashboardPopupHelper } from '../../fitzhi-dashboard-popup-helper';
import { selection } from '../../selection';
import { SummaryService } from '../service/summary.service';
import { environment } from '../../../../environments/environment';
import { TurnoverService } from 'src/app/service/turnover/turnover.service';


@Component({
	selector: 'app-summary',
	templateUrl: './summary.component.html',
	styleUrls: ['./summary.component.css']
})
export class SummaryComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
	 * The component has to emit an event if the user clicks on a summary.
	 */
	@Output() messengerSelectedSummary = new EventEmitter<number>();

	public selection = selection;

	public environment = environment;

	public projectsEvaluation = 0;

	public turnoverDatas = [];

	/**
	 * Helper handler the display or not of the poppup.
	 */
	public popupHelper = new FitzhiDashboardPopupHelper();

	constructor(
		public summaryService: SummaryService,
		public dashboardService: DashboardService,
		public staffListService: StaffListService,
		private turnoverService: TurnoverService,
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

		this.subscriptions.add(
			this.staffListService.allStaffLoaded$.subscribe({
				next: doneAndOk => {
					this.turnoverDatas = [];
					const currentYear = new Date(Date.now()).getFullYear();
					this.turnoverDatas.push(this.turnoverService.turnover(currentYear - 2));
					this.turnoverDatas.push(this.turnoverService.turnover(currentYear - 1));
					this.turnoverDatas.push(this.turnoverService.turnover(currentYear));
				}
			})
		);

	}

	/**
	 * @returns **true** if the mouse is moving over the general average panel.
	 */
	public hasGeneralAverage() {
		return (this.popupHelper.isButtonActivated(selection.generalAverage));
	}

	/**
	 * @returns **true** if the mouse is moving over the skills coverage panel.
	 */
	public hasSkillsCoverageScore() {
		return (this.popupHelper.isButtonActivated(selection.skillsCoverageScore));
	}

	public isTurnoverActivated(year: number) {
		return this.popupHelper.isTurnoverActivated(year);
	}

	/**
	 * Switch the current selection to the given identifier.
	 * @param selectionId the identifier of the panel dashboard to display
	 */
	switchTo(selectionId: number) {
		this.messengerSelectedSummary.emit(selectionId);
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
