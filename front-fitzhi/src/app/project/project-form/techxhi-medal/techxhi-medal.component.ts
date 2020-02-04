import { Component, OnInit, Input, AfterViewInit, OnDestroy, EventEmitter, Output } from '@angular/core';
import { ReferentialService } from 'src/app/service/referential.service';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { SonarProject } from 'src/app/data/SonarProject';
import { CinematicService } from 'src/app/service/cinematic.service';
import { BehaviorSubject } from 'rxjs';
import { ProjectService } from 'src/app/service/project.service';
import { take } from 'rxjs/operators';

@Component({
	selector: 'app-techxhi-medal',
	templateUrl: './techxhi-medal.component.html',
	styleUrls: ['./techxhi-medal.component.css']
})
export class TechxhiMedalComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * the color of the STAFF risk circle
	 */
	@Input() colorOfRisk: string;

	/**
	 * This component, hosted in a tab pane, use this emitter to inform its parent to change the active pane.
	 */
	@Output() tabActivationEmitter = new EventEmitter<number>();

	public PROJECT_IDX_TAB_SONAR = Constants.PROJECT_IDX_TAB_SONAR;
	public PROJECT_IDX_TAB_SUNBURST = Constants.PROJECT_IDX_TAB_SUNBURST;
	public PROJECT_IDX_TAB_AUDIT = Constants.PROJECT_IDX_TAB_AUDIT;

	/**
	 * Selected tab.
	 */
	private selectedTab: number;

	/**
	 * Global evaluation given to this project :
	 *
	 * the global mean __Sonar__ evaluation processed for all Sonar projects
	 * declared in the Fitzhì project.
	 */
	private globalSonarEvaluation = -1;

	/**
	 * This boolean agregates the conditions required for displaying the audit badge.
	 */
	private displayAuditBadge = false;

	constructor(
		private referentialService: ReferentialService,
		private projectService: ProjectService,
		private cinematicService: CinematicService) {
		super();
	}

	ngOnInit() {
		this.projectService.projectLoaded$
			.pipe(take(1))
			.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.globalSonarEvaluation = this.projectService.calculateSonarEvaluation(this.projectService.project);
						this.displayAuditBadge = this.ProcessDisplayAuditBadge();
					}
				}
			});
		this.subscriptions.add(
			this.cinematicService.tabProjectActivated$
				.subscribe(selectedTab => {
					setTimeout(() => {
						this.selectedTab = selectedTab ;
					}, 0);
				}));
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
	 * This function is processing the `*ngIf` preview condition of the __Audit__ summary badge.
	 */
	ProcessDisplayAuditBadge(): boolean {

		if (!this.projectService.project) {
			return false;
		}

		if (!this.projectService.project.auditEvaluation) {
			return false;
		}

		return true;
	}

	/**
	 * This function is handling the `*ngIf` preview condition of the __Sonar__ summary badge.
	 */
	sonarReady() {
		if (!this.projectService.project) {
			return false;
		}

		if (!this.projectService.project.sonarProjects) {
			return false;
		}

		if (this.projectService.project.sonarProjects.length === 0) {
			return false;
		}

		let preview = true;
		this.projectService.project.sonarProjects.forEach(sonarProject => {
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
