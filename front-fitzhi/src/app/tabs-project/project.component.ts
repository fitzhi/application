import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CinematicService } from '../service/cinematic.service';
import { Constants } from '../constants';
import { Router, ActivatedRoute } from '@angular/router';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { Project } from '../data/project';
import { ListProjectsService } from './list-project/list-projects-service/list-projects.service';
import { MessageService } from '../interaction/message/message.service';
import { BaseDirective } from '../base/base-directive.directive';
import { ProjectService } from '../service/project/project.service';
import { switchMap, take } from 'rxjs/operators';
import { MessageGravity } from '../interaction/message/message-gravity';
import { ReferentialService } from '../service/referential/referential.service';
import { SonarService } from '../service/sonar.service';
import { traceOn } from '../global';

@Component({
	selector: 'app-project',
	templateUrl: './project.component.html',
	styleUrls: ['./project.component.css']
})
export class ProjectComponent extends BaseDirective implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * We given the risk -1 into the behaviorSubject an empty.
	 */
	public risk$ = new BehaviorSubject<number>(-1);

	/**
	 * Project identifier.
	 */
	public idProject: number;

	constructor(
		public cinematicService: CinematicService,
		private referentialService: ReferentialService,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private sonarService: SonarService,
		private listProjectsService: ListProjectsService,
		public projectService: ProjectService,
		private router: Router) {
		super();
	}

	/**
	 * Initialization treatment.
	 */
	ngOnInit() {

		if (this.router.url === '/project') {
			this.cinematicService.setForm(Constants.PROJECT_TAB_FORM, this.router.url);
		}

		if (this.router.url.indexOf('/staff') !== -1) {
			this.cinematicService.projectTabIndex = 1;
			if (traceOn()) {
				console.log('Index selected ' + this.cinematicService.projectTabIndex + ' to display the project staff list');
			}
		}

		this.subscriptions.add(
			this.referentialService.referentialLoaded$.pipe(take(1)).subscribe({
				next: doneAndOk =>  {
					if (doneAndOk) {
						this.idProject = this.projectService.parseUrl(this.router.url);
						if (!this.idProject) {
							this.projectService.createEmptyProject();
						} else {
							this.loadProject();
						}
					}
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
		if (traceOn()) {
			console.log('Update the project risk level to ', riskLevel);
		}
		this.risk$.next(riskLevel);
	}

	/**
	 * User has changed the selected tab.
	 */
	public selectedIndexChange(selectedIndex: number): void {
		if (traceOn()) {
			console.log('Tab "' + Constants.TAB_TITLE[selectedIndex] + '" selected.');
		}
		if (this.cinematicService.projectTabIndex !== selectedIndex) {
			this.cinematicService.projectTabIndex = selectedIndex;
		}
		this.cinematicService.setProjectTab(selectedIndex);
	}

	/**
	 * This method receives the new tab to activate from e.g. the sunburst tab pane child
	 * (but it won't be the only one).
	 * @param tabIndex new tab to activate.
	 */
	public tabActivation (tabIndex: number) {
		this.cinematicService.projectTabIndex = tabIndex;
		if (traceOn()) {
			console.log ('Selected index', Constants.TAB_TITLE[this.cinematicService.projectTabIndex]);
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
					this.projectService.loadMapSkills(this.projectService.project);
					if (traceOn()) {
						this.projectService.dump(project, 'projectComponent');
					}
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
							if (traceOn()) {
								console.log('404 : cannot find a project for the id ' + this.idProject);
							}
							this.messageService.error('There is no project for id ' + this.idProject);
						} else {
							console.error(error.message);
						}},
					complete: () => {
						if (traceOn()) {
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
