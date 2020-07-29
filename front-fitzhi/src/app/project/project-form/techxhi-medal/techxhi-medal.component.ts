import { Component, OnInit, Input, AfterViewInit, OnDestroy, EventEmitter, Output } from '@angular/core';
import { ReferentialService } from 'src/app/service/referential.service';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { take } from 'rxjs/operators';
import { ProjectService } from 'src/app/service/project.service';
import { CinematicService } from 'src/app/service/cinematic.service';

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
	public globalSonarEvaluation = -1;

	/**
	 * This boolean agregates the conditions required for displaying the audit badge.
	 */
	public displayAuditBadge = false;

	constructor(
		public referentialService: ReferentialService,
		public projectService: ProjectService,
		public cinematicService: CinematicService) {
			super();
	}

	ngOnInit() {
		console.log('ngOnInit()');
		this.projectService.projectLoaded$
			.pipe(take(1))
			.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.globalSonarEvaluation = this.projectService.calculateSonarEvaluation(this.projectService.project);
						this.displayAuditBadge = this.processDisplayAuditBadge();
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
		this.cinematicService.projectTabIndex = tabIndex;
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
	processDisplayAuditBadge(): boolean {

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
	 * This function is handling the `*ngIf` preview condition of the __codeFactor.io__ summary badge.
	 */
	sonarCodeFactorReady() {
		return (this.projectService.project) && (this.projectService.project.urlCodeFactorIO);
	}

	urlCodeFactorIO() {
		return this.projectService.urlCodeFactorIO();
	}
	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
