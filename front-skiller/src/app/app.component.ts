import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { CinematicService } from './service/cinematic.service';
import { Constants } from './constants';
import { ListProjectsService } from './list-projects-service/list-projects.service';
import { ReferentialService } from './service/referential.service';
import { StaffService } from './service/staff.service';
import { Router } from '@angular/router';
import { BaseComponent } from './base/base.component';
import { TabsStaffListService } from './tabs-staff-list/service/tabs-staff-list.service';
import { SkillService } from './service/skill.service';
import { ListCriteria } from './data/listCriteria';
import { AuthService } from './admin/service/auth/auth.service';
import { ProjectService } from 'src/app/service/project.service';
import { SonarService } from './service/sonar.service';
import { MessageService } from './message/message.service';

declare var $: any;

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * Definitive name of the application, for today at least....
	 */
	public title = 'techxhicolor';

	/**
    * Context identfifier : Entity currently active.
    */
	public activeContext: number;

	/**
    * Searching request typed & displayed in the searching field.
    */
	criteria: string;

	/**
    * Filter on active employees if true (by default), or include all the employees in the database
    */
	activeOnly = true;

	/**
     * NEXT element ID, if any, from the underlying collection supplying the master/detail feature.
     */
	nextId: number;

	/**
     * PREVIOUS element ID, if any, from the underlying collection supplying the master/detail feature.
     */
	previousId: number;

	constructor(
		private cinematicService: CinematicService,
		private sonarService: SonarService,
		private authService: AuthService,
		private tabsStaffListService: TabsStaffListService,
		private skillService: SkillService,
		private listProjectsService: ListProjectsService,
		private referentialService: ReferentialService,
		private staffService: StaffService,
		private projectService: ProjectService,
		private messageService: MessageService,
		private router: Router) {

		super();
	}

	ngOnInit() {

		localStorage.removeItem('access_token');
		localStorage.removeItem('refresh_token');

		/**
         * Loading the referentials.
         */
		this.referentialService.loadAllReferentials();
		this.sonarService.loadSonarVersion();
		this.subscriptions.add(
		this.sonarService.sonarIsAccessible$
			.subscribe(accessible => {
				if (accessible) {
					this.sonarService.loadProjects();
					this.sonarService.loadSonarSupportedMetrics();
				} else {
					this.messageService.warning('Warning : Sonar is offline or unreachable!');
				}
			})
		);
	}

	/**
      * Search button has been clicked.
      */
	search(): void {
		switch (this.activeContext) {
			case Constants.TABS_STAFF_LIST:
			case Constants.DEVELOPERS_SEARCH:
				if (Constants.DEBUG) {
					console.log(
						'Searching ' + (this.activeOnly ? 'only active ' : 'all ')
						+ 'staff members for the search criteria ' + this.criteria + '');
				}
				if ((this.criteria !== null) && (this.criteria.length > 0)) {
					this.tabsStaffListService.addTabResult(this.criteria, this.activeOnly);
				}
				break;
			case Constants.SKILLS_SEARCH: {
				if (Constants.DEBUG) {
					console.log('Reloading skills for search criteria ' + this.criteria);
				}
				this.skillService.filterSkills(new ListCriteria(this.criteria, this.activeOnly));
				this.staffService.countAll_groupBy_experience(this.activeOnly);
				break;
			}
			case Constants.PROJECT_SEARCH: {
				if (Constants.DEBUG) {
					console.log('Reloading project for search criteria ' + this.criteria);
				}
				this.listProjectsService.reloadProjects(this.criteria);
				break;
			}
		}
	}

	/**
     * User has entered into the search INPUT.
     */
	focusSearch() {
		switch (this.activeContext) {
			case Constants.SKILLS_SEARCH:
				this.router.navigate(['/searchSkill'], {});
				break;
			case Constants.DEVELOPERS_SEARCH:
				this.router.navigate(['/searchUser'], {});
				break;
			case Constants.PROJECT_SEARCH:
				this.router.navigate(['/searchProject'], {});
				break;
		}
	}

	public list() {
		switch (this.activeContext) {
			case Constants.BACK_TO_LIST:
				switch (this.cinematicService.getFormerFormIdentifier()) {
					case Constants.TABS_STAFF_LIST:
						this.router.navigate(['/searchUser'], {});
						break;
					case Constants.PROJECT_TAB_STAFF:
						this.router.navigate([this.cinematicService.previousForm.url], {});
						break;
					default:
						break;
				}
				break;
			default:
				console.error('Unattempted context identifier', this.activeContext);
				break;
		}
	}

	/**
     * The end-user has switched the context to another entity (staff/skill/project)
     */
	onChangeForm($event: number) {
		this.activeContext = $event;
		if (Constants.DEBUG) {
			console.log('Changing to mode', Constants.CONTEXT[$event]);
		}
		if (this.activeContext === Constants.BACK_TO_LIST) {
			this.list();
		}
		if (this.isInSearchingMode()) {
			this.focusSearch();
		}
	}

	/**
     * @returns TRUE if the active mode in a searching mode
     */
	isInSearchingMode() {
		switch (this.activeContext) {
			case Constants.TABS_STAFF_LIST:
			case Constants.DEVELOPERS_SEARCH:
			case Constants.SKILLS_SEARCH:
			case Constants.PROJECT_SEARCH:
				return true;
			default:
				return false;
		}
	}

	/**
     * The end-user has switched the activeOnly data state to ON or OFF.
     */
	onChangeActiveOnly($event: boolean) {
		this.activeOnly = $event;
		if (Constants.DEBUG) {
			if (this.activeOnly) {
				console.log('Filter only active records');
			} else {
				console.log('Select all records');
			}
		}
	}

	/**
     * The end-user has requested a query based on the passed criteria.
     */
	onRequestQuery($event: string) {
		if (Constants.DEBUG) {
			console.log('Request of a query on criteria', $event);
		}
		this.criteria = $event;
		this.search();
	}

	/**
     * @returns TRUE if the user is connected.
     */
	isConnected() {
		return this.authService.isConnected();
	}

	/**
     * All subscriptions are closed in the BaseComponent
     */
	public ngOnDestroy() {
		super.ngOnDestroy();
	}

	public ngAfterViewInit () {

		$(document).ready(
			$(function () {
				$('[data-toggle="tooltip"]').tooltip();
			}
		));

		// For development convenience, we deactivate the security control.
		if (localStorage.getItem('dev') === '1') {
			this.projectService.loadProjects();
		}
	}

}
