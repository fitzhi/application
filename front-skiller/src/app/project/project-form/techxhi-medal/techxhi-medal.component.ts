import { Component, OnInit, Input, AfterViewInit, OnDestroy, EventEmitter, Output } from '@angular/core';
import { ReferentialService } from 'src/app/service/referential.service';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { SonarProject } from 'src/app/data/SonarProject';

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

	public PROJECT_IDX_TAB_SONAR = Constants.PROJECT_IDX_TAB_SONAR;
	public PROJECT_IDX_TAB_SUNBURST = Constants.PROJECT_IDX_TAB_SUNBURST;
	public PROJECT_IDX_TAB_AUDIT = Constants.PROJECT_IDX_TAB_AUDIT;

	constructor(private referentialService: ReferentialService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe((project: Project) => {
				if (project) {
					this.project = project;
				}
			}));
	}

	/**
	 * Return the global mean __Sonar__ evaluation processed for all Sonar projects
	 * declared in the techzhì project.
	 */
	globalSonarEvaluation() {

		let globalSonarEvaluation = 0;
		if (!this.project) {
			return 0;
		}

		if ((this.project.sonarProjects) && this.allSonarProjectsEvaluated(this.project)) {
			let totalEvalution = 0;
			let totalNumerberLinesOfCode = 0;
			this.project.sonarProjects.forEach(sonarProject => {
				totalEvalution += sonarProject.sonarEvaluation.evaluation * sonarProject.sonarEvaluation.totalNumberLinesOfCode;
				totalNumerberLinesOfCode += sonarProject.sonarEvaluation.totalNumberLinesOfCode;
			});
			globalSonarEvaluation = Math.round(totalEvalution / totalNumerberLinesOfCode);
		}

		return globalSonarEvaluation;
	}


	/**
	 * Return `true` if all sonar projects declared in this projet, have been evaluated, false otherwise.
	 * @param project the current project
	 */
	allSonarProjectsEvaluated(project: Project) {
		let complete = true;
		project.sonarProjects.forEach(sonarP => {
			if (complete && (!sonarP.sonarEvaluation)) {
				complete = false;
			}
		});
		return complete;
	}

	/**
	 * Change the current active tab.
	 * @param tabIndex index of tab requested.
	 */
	public jumpToTab(tabIndex: number) {
		this.tabActivationEmitter.next(tabIndex);
	}


	/**
	 * @returns the color figuring the risk evaluation for this project.
	 */
	styleDot () {
		return { 'fill': this.colorOfRisk };
	}


	/**
	 * This function is handling the `*ngIf` preview condition of the __Audit__ summary badge.
	 */
	auditReady() {
		if (!this.project) {
			return false;
		}

		if (!this.project.auditEvaluation) {
			return false;
		}

		return true;
	}

	/**
	 * This function is handling the `*ngIf` preview condition of the __Sonar__ summary badge.
	 */
	sonarReady() {
		if (!this.project) {
			return false;
		}

		if (!this.project.sonarProjects) {
			return false;
		}

		if (this.project.sonarProjects.length === 0) {
			return false;
		}

		let preview = true;
		this.project.sonarProjects.forEach(sonarProject => {
			if ((!sonarProject.sonarEvaluation) && preview) {
				preview = false;
			}
		});
		return preview;
	}


	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
