import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CinematicService } from '../service/cinematic.service';
import { Constants } from '../constants';
import { Router, ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { Project } from '../data/project';
import { ListProjectsService } from '../list-projects-service/list-projects.service';
import { MessageService } from '../message/message.service';
import { BaseComponent } from '../base/base.component';

@Component({
	selector: 'app-project',
	templateUrl: './project.component.html',
	styleUrls: ['./project.component.css']
})
export class ProjectComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	public subjProject = new Subject<Project>();

	/**
     * Index of the tab selected.
     */
	public tabIndex = 0;

	/**
     * Project identifier.
     */
	public idProject: number;

	sub: any;

	constructor(
		private cinematicService: CinematicService,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private listProjectsService: ListProjectsService,
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
				console.log('params[\'id\'] ' + params['id']);
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
		setTimeout(() => {
			this.loadProject();
		});
	}

	/**
     * User has changed the selected tab.
     */
	public selectedIndexChange(selectedIndex: number): void {
		if (Constants.DEBUG) {
			const today = new Date();
			console.log('The index ' + selectedIndex + ' is selected @ '
				+ today.getHours() + ':' + today.getMinutes() + ':' + today.getSeconds());
		}
		this.cinematicService.setProjectTab(selectedIndex);
	}

	/**
     * Loading the project from the back-end.
     */
	loadProject() {
		// EITHER we are in creation mode,
		// OR we load the Project from the back-end...
		// Anyway, We create an empty project until the subscription is complete
		if (this.idProject != null) {
			this.subscriptions.add(
				this.listProjectsService.getProject(this.idProject).subscribe(
					(project: Project) => {
						this.subjProject.next(project);
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
     * @returns TRUE if the project has an avalaible connection to the source repository.
     */
	canConnectSourceControl() {
		return true;
	}
}
