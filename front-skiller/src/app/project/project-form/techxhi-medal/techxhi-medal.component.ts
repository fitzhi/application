import { Component, OnInit, Input, AfterViewInit, OnDestroy, EventEmitter, Output } from '@angular/core';
import { ReferentialService } from 'src/app/service/referential.service';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';

@Component({
	selector: 'app-techxhi-medal',
	templateUrl: './techxhi-medal.component.html',
	styleUrls: ['./techxhi-medal.component.css']
})
export class TechxhiMedalComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Observable of Project received from the parent Form project.
	 */
	@Input() project$;

	/**
	 * the color of the STAFF risk circle
	 */
	@Input() colorOfRisk: string;

	/**
	 * This component, hosted in a tab pane, use this emitter to inform its parent to change the active pane.
	 */
	@Output() tabActivationEmitter = new EventEmitter<number>();

	/**
	 * Current active project.
	 */
	project: Project;

	/**
	 * Mean Sonar evaluation.
	 */
	globalSonarEvaluation = 0;

	public PROJECT_IDX_TAB_SONAR = Constants.PROJECT_IDX_TAB_SONAR;
	public PROJECT_IDX_TAB_SUNBURST = Constants.PROJECT_IDX_TAB_SUNBURST;
	public PROJECT_IDX_TAB_AUDIT = Constants.PROJECT_IDX_TAB_AUDIT;

	constructor(private referentialService: ReferentialService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe((project: Project) => {
				if ((project) && (project.sonarProjects)) {
					this.project = project;
					let totalEvalution = 0;
					let totalNumerberLinesOfCode = 0;
					project.sonarProjects.forEach(sonarProject => {
						totalEvalution += sonarProject.sonarEvaluation.evaluation * sonarProject.sonarEvaluation.totalNumberLinesOfCode;
						totalNumerberLinesOfCode += sonarProject.sonarEvaluation.totalNumberLinesOfCode;
					});
					this.globalSonarEvaluation = Math.round(totalEvalution / totalNumerberLinesOfCode);
					if (Constants.DEBUG) {
						console.log ('globalSonarEvaluation %d for project %s', this.globalSonarEvaluation, project.name);
					}
				}
			}));
	}

	/**
	 * Change the current active tab.
	 * @param tabIndex index of tab requested.
	 */
	public jumpToTab(tabIndex: number) {
		console.log ('emit', tabIndex);
		this.tabActivationEmitter.next(tabIndex);
	}


	/**
	 * @returns the color figuring the risk evaluation for this project.
	 */
	styleDot () {
		return { 'fill': this.colorOfRisk };
	}


	/**
	 * This function is handling the `*ngIf` preview condition of the audit summary badge.
	 */
	auditReady() {
		if (!this.project) {
			return false;
		}

		if (!this.project.audit) {
			return false;
		}

		if (Object.keys(this.project.audit).length === 0) {
			return false;
		}

		return true;
	}
	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
