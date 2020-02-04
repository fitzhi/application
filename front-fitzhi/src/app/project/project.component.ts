import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CinematicService } from '../service/cinematic.service';
import { Constants } from '../constants';
import { Router, ActivatedRoute } from '@angular/router';
import { Subject, BehaviorSubject, EMPTY } from 'rxjs';
import { Project } from '../data/project';
import { ListProjectsService } from '../list-projects-service/list-projects.service';
import { MessageService } from '../message/message.service';
import { BaseComponent } from '../base/base.component';
import { ProjectService } from '../service/project.service';
import { switchMap, take } from 'rxjs/operators';
import { MessageGravity } from '../message/message-gravity';
import { ReferentialService } from '../service/referential.service';
import { SonarService } from '../service/sonar.service';

@Component({
	selector: 'app-project',
	templateUrl: './project.component.html',
	styleUrls: ['./project.component.css']
})
export class ProjectComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * We given the risk -1 into the behaviorSubject an empty.
	 */
	public risk$ = new BehaviorSubject<number>(-1);

	/**
	 * Index of the tab selected.
	 */
	public tabIndex = 0;

	/**
	 * Project identifier.
	 */
	public idProject: number;

	/**
	 * Title of the tabs.
	 */
	private TAB_TITLE = ['Project', 'Staff list', 'Staff coverage', 'Sonar', 'Audit'];

	constructor(
		private cinematicService: CinematicService,
		private referentialService: ReferentialService,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private sonarService: SonarService,
		private listProjectsService: ListProjectsService,
		private projectService: ProjectService,
		private router: Router) {
		super();
	}

	/**
	 * Initialization treatment.
	 */
	ngOnInit() {
		if (Constants.DEBUG) {
			console.log('Current url ' + this.router.url);
		}
		if (this.router.url === '/project') {
			this.cinematicService.setForm(Constants.PROJECT_TAB_FORM, this.router.url);
		}

		if (this.router.url.indexOf('/staff') !== -1) {
			this.tabIndex = 1;
			if (Constants.DEBUG) {
				console.log('Index selected ' + this.tabIndex + ' to display the project staff list');
			}
		}

		this.subscriptions.add(
			this.referentialService.referentialLoaded$.pipe(
				switchMap(doneAndOk => {
					return (doneAndOk) ? this.route.params : EMPTY;
				}))
				.subscribe(params => {
					if (Constants.DEBUG) {
						console.log('Project identifier given', params['id']);
					}
					if (params['id'] == null) {
						this.idProject = null;
					} else {
						this.idProject = + params['id']; // (+) converts string 'id' to a number
						this.loadProject();
					}
				}));
	}

	/**
	 * After init treatment. We load the project.
	 */
	ngAfterViewInit() {
	}

	/**
	 * Reload the the project record.
	 */
	updateRiskLevel(riskLevel: number) {
		if (Constants.DEBUG) {
			console.log('Update the project risk level to ', riskLevel);
		}
		this.risk$.next(riskLevel);
	}

	/**
	 * User has changed the selected tab.
	 */
	public selectedIndexChange(selectedIndex: number): void {
		if (Constants.DEBUG) {
			console.log('Tab "' + this.TAB_TITLE[selectedIndex] + '" selected.');
		}
		if (this.tabIndex !== selectedIndex) {
			this.tabIndex = selectedIndex;
		}
		this.cinematicService.setProjectTab(selectedIndex);
	}

	/**
	 * This method receives the new tab to activate from e.g. the sunburst tab pane child
	 * (but it won't be the only one).
	 * @param tabIndex new tab to activate.
	 */
	public tabActivation (tabIndex: number) {
		this.tabIndex = tabIndex;
		if (Constants.DEBUG) {
			console.log ('Selected index', this.TAB_TITLE[this.tabIndex]);
		}
	}

	/**
	 * Loading the project from the back-end.
	 */
	loadProject() {

		this.subscriptions.add(
			//
			// We load all Fitzhì projects registered in the backend.
			//
			this.projectService.allProjectsIsLoaded$.pipe (
				//
				// We test and load all Sonar servers declared in the back-end.
				//
				switchMap((doneAndOk: boolean) => {
					return this.sonarService.allSonarServersLoaded$;
				}),
				//
				// We retrieve the project with the given ID.
				//
				switchMap( (success: boolean) => {
					return this.listProjectsService.getProject$(this.idProject);
				}),
				//
				// We test if the Sonar server declared for this project (if any) is available
				//
				switchMap(project => {
					this.projectService.project = project;
					this.projectService.dump(project, 'projectComponent');
					return this.sonarService.sonarIsAccessible$(this.projectService.project);
				}))
				//
				// We inform all tabs of the identifier project with the subject projectService.projectLoaded$
				//
				.subscribe ({
					next: sonarIsAccessible => {
						this.projectService.sonarIsAccessible = sonarIsAccessible;
						this.projectService.projectLoaded$.next(true);
					},
					error: error => {
						if (error.status === 404) {
							if (Constants.DEBUG) {
								console.log('404 : cannot find a project for the id ' + this.idProject);
							}
							this.messageService.error('There is no project for id ' + this.idProject);
						} else {
							console.error(error.message);
						}},
					complete: () => {
						if (Constants.DEBUG) {
							console.log('Loading complete for id ' + this.idProject);
						}
						this.messageService.success(this.projectService.project.name + ' is found');
					}
				}));
	}

	/**
	 * Catch the messageGravity.
	 * @param messageGravity a message and its severity
	 */
	catchMessage(messageGravity: MessageGravity) {
		this.messageService.set(messageGravity.severity, messageGravity.message);
	}

	/**
	 * @returns `TRUE` if the project has an avalaible connection to the source repository.
	 */
	canConnectSourceControl() {
		// TODO something has to be tested here.
		return true;
	}
}
