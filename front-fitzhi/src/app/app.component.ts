import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { CinematicService } from './service/cinematic.service';
import { Constants } from './constants';
import { ListProjectsService } from './tabs-project/list-project/list-projects-service/list-projects.service';
import { ReferentialService } from './service/referential.service';
import { StaffService } from './tabs-staff/service/staff.service';
import { Router } from '@angular/router';
import { BaseComponent } from './base/base.component';
import { TabsStaffListService } from './tabs-staff-list/service/tabs-staff-list.service';
import { SkillService } from './skill/service/skill.service';
import { AuthService } from './admin/service/auth/auth.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { SonarService } from './service/sonar.service';
import { traceOn } from './global';
import { InstallService } from './admin/service/install/install.service';
import { environment } from '../environments/environment';
import { isNumeric } from 'rxjs/internal-compatibility';
import { take } from 'rxjs/operators';
import { ListCriteria } from './data/listCriteria';

declare var $: any;

/**
 * The **Main** application component.
 */
@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * The environment 
	 */
	public environment = environment;

	/**
	* Context identfifier : Entity currently active.
	*/
	public activeContext: number;

	/**
	* Searching request typed & displayed in the searching field.
	*/
	public criteria: string;

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
		public installService: InstallService,
		private router: Router) {

		super();
	}

	ngOnInit() {
		//
		// Loading the referentials.
		//
		// TODO The Sonar servers array should be stored in the referential.
		this.sonarService.loadSonarsVersion();
		this.referentialService.loadAllReferentials();
		this.sonarService.loadSonarMetrics();

		// We display the current version
		console.info('version %s build-time %s', this.environment.version, this.environment.buildTime);
	}

	
	/**
	  * Search button has been clicked.
	  */
	search(): void {

		function isNumber(value: string | number): boolean {
		   return ((value != null) &&
				   (value !== '') &&
				   !isNaN(Number(value.toString())) &&
				   Number.isInteger(Number(value))); 
		}
		
		switch (this.activeContext) {
			case Constants.TABS_STAFF_LIST:
			case Constants.DEVELOPERS_SEARCH:
				if (traceOn()) {
					console.log(
						'Searching %s staff members for the search criteria %s', 
						(this.activeOnly ? 'only active' : 'all'), 
						this.criteria);
				}
				if (this.criteria) {
					if ( isNumber(this.criteria) ) {
						console.log ('Looking a developer with id %s', this.criteria);
						this.router.navigate(['/user/' + this.criteria]);
					} else {
						if (this.criteria.length > 0) {
							this.tabsStaffListService.addTabResult(this.criteria, this.activeOnly);
						}
					}
				}
				break;
			case Constants.SKILLS_SEARCH: {
				if (traceOn()) {
					console.log('Reloading skills for search criteria ' + this.criteria);
				}
				this.skillService.allSkillsLoaded$.pipe(take(1)).subscribe({
					next: doneAndOk => {
						if (doneAndOk) {
							this.skillService.filterSkills(new ListCriteria(this.criteria, this.activeOnly));
							this.staffService.countAll_groupBy_experience(this.activeOnly);
						}
					}
				})
				break;
			}
			case Constants.PROJECT_SEARCH: {
				if (traceOn()) {
					console.log('Reloading %s projects for search criteria %s', (this.activeOnly ? 'active' : ''), this.criteria);
				}
				this.listProjectsService.search(this.criteria, this.activeOnly);
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

	/**
	 * Jump back to the parent list.
	 */
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
	 * @param $event the event emitted
	 */
	public onChangeForm($event: number) {
		this.activeContext = $event;
		if (traceOn()) {
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
	isInSearchingMode(): boolean {
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
	 * @param $event the emitted boolean
	 */
	onChangeActiveOnly($event: boolean) {
		this.activeOnly = $event;
		if (traceOn()) {
			console.log((this.activeOnly) ? 'Filter only active records' : 'Select all records');
		}
		this.search();
	}

	/**
	 * The end-user has requested a query based on the passed criteria.
	 * @param $event the criteria typed by the end-user
	 */
	onRequestQuery($event: string) {
		if (traceOn()) {
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
	 * After view initialization.
	 */
	public ngAfterViewInit() {
		$(document).ready(
			$(function () {
				$('[data-toggle="tooltip"]').tooltip();
			}
			));
	}

	/**
	 * All subscriptions are closed in the BaseComponent
	 */
	public ngOnDestroy() {
		super.ngOnDestroy();
	}
}
