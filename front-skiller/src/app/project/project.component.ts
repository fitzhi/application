import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CinematicService } from '../service/cinematic.service';
import { Constants } from '../constants';
import { Router, ActivatedRoute } from '@angular/router';
import { Subject, BehaviorSubject } from 'rxjs';
import { Project } from '../data/project';
import { ListProjectsService } from '../list-projects-service/list-projects.service';
import { MessageService } from '../message/message.service';
import { BaseComponent } from '../base/base.component';
import { ProjectService } from '../service/project.service';
import { switchMap, take } from 'rxjs/operators';
import { MessageGravity } from '../message/message-gravity';

@Component({
	selector: 'app-project',
	templateUrl: './project.component.html',
	styleUrls: ['./project.component.css']
})
export class ProjectComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * ## IMPORTANT :
	 *
	 * The observable `project$` cannot be a simple `Subject`
	 * because the lifecycle of the tabs under the component project is not homogenous.
	 * Some tab content are eagerly loaded, some other lazy.
	 *
	 * We need to use a persistant observable whenever the tab is already created, or not.
	 *
	 */
	public project$ = new BehaviorSubject<Project>(null);

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

	private TAB_TITLE = ['Project', 'Staff list', 'Staff coverage', 'Sonar', 'Audit'];

	constructor(
		private cinematicService: CinematicService,
		private route: ActivatedRoute,
		private messageService: MessageService,
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

		this.subscriptions.add(this.route.params.subscribe(params => {
			if (Constants.DEBUG) {
				console.log('params[\'id\']', params['id']);
			}
			if (params['id'] == null) {
				this.idProject = null;
			} else {
				this.idProject = + params['id']; // (+) converts string 'id' to a number
			}
		}));
	}

	/**
	 * After init treatment. We load the project.
	 */
	ngAfterViewInit() {
		this.loadProject();
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
		// EITHER we are in creation mode,
		// OR we load the Project from the back-end...
		// Anyway, We create an empty project until the subscription is complete
		if (this.idProject) {
			if (Constants.DEBUG) {
				console.log ('Loading the project');
			}
			this.subscriptions.add(
				this.projectService.allProjectsIsLoaded$.pipe (
					switchMap( (success: boolean) => {
						return this.listProjectsService.getProject(this.idProject).pipe(take(1));
					}))
				.subscribe(
					(project: Project) => {
						this.projectService.dump(project, 'projectComponent');
						this.project$.next(project);
					},
					error => {
						if (error.status === 404) {
							if (Constants.DEBUG) {
								console.log('404 : cannot find a project for the id ' + this.idProject);
							}
							this.messageService.error('There is no project for id ' + this.idProject);
						} else {
							console.error(error.message);
						}
					},
					() => {
						if (Constants.DEBUG) {
							console.log('Loading complete for id ' + this.idProject);
						}
					})
				);
		}
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
