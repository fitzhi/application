import { Constants } from '../../constants';
import { Collaborator } from '../../data/collaborator';
import { StaffDTO } from '../../data/external/staffDTO';
import { MessageService } from '../../message/message.service';
import { ProjectService } from '../../service/project.service';
import { StaffService } from '../../service/staff.service';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { Component, OnInit, OnDestroy, Input, AfterViewInit, ViewChild } from '@angular/core';

import { LocalDataSource } from 'ng2-smart-table';
import { BaseComponent } from '../../base/base.component';
import Tagify from '@yaireo/tagify';
import { Mission } from 'src/app/data/mission';
import { BooleanDTO } from 'src/app/data/external/booleanDTO';
import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { ProjectGhostsComponent } from 'src/app/project/project-sunburst/dialog-project-ghosts/project-ghosts/project-ghosts.component';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';

@Component({
	selector: 'app-staff-projects',
	templateUrl: './staff-projects.component.html',
	styleUrls: ['./staff-projects.component.css']
})
export class StaffProjectsComponent extends BaseComponent implements OnInit, OnDestroy, AfterViewInit {

	/**
 	* Selected TAB.
 	* This observable is fired by the StaffComponent class when the user changes the tab selected.
 	*/
	@Input() selectedTab$;

	/**
	 * Employee retrieve from StaffComponent access.
	 */
	private collaborator: Collaborator;

	/**
	 * JS object handling the projects component.
	 */
	tagify: Tagify;

	/**
	 * Bound addProject to the current active component.
	 * The goal of this bind is to access the member variables of this class, such as projet
	 */
	private boundAddProject: any;

	/**
	 * Bound removeProject to the current active component.
	 * The goal of this bind is to access the member variables of this class, such as projet
	 */
	private boundRemoveProject: any;

	/**
	 * The datasource containing the missions of the staff member.
	 */
	public dataSource;

	/**
	 * The columns to be displayed in the table of missions.
	 */
	public displayedColumns: string[] = ['name', 'firstCommit', 'lastCommit', 'numberOfCommits', 'numberOfFiles'];

	numberOfMissions$ = new BehaviorSubject<number>(0);

	constructor(
		private messageService: MessageService,
		private staffService: StaffService,
		private projectService: ProjectService,
		private staffDataExchangeService: StaffDataExchangeService) {
		super();

		this.boundAddProject = this.addProject.bind(this);
		this.boundRemoveProject = this.removeProject.bind(this);

	}

	ngOnInit() {

		/**
		 * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
		 */
		this.subscriptions.add(
			this.staffDataExchangeService.collaborator$
				.subscribe((collabRetrieved: Collaborator) => {
					this.collaborator = collabRetrieved;
					this.loadMissions(this.collaborator.missions);
				}));
	}

	/**
	 * Load the missions for the current active staff member.
	 */
	loadMissions(missions: Mission[]) {
		this.dataSource = new MatTableDataSource(missions);
		if (Constants.DEBUG) {
			console.log ('Missions loaded', missions.length);
		}
		this.numberOfMissions$.next(missions.length);
	}

	ngAfterViewInit() {
		const input = document.querySelector('textarea[name=projects]');
		this.tagify = new Tagify (input, {
			enforceWhitelist : true,
			whitelist        : []
		});

		// We setup the whitelist of the componenet
		this.projectService.allProjects
			.map(function(project) { return project.name; }).forEach(element => {
					this.tagify.settings.whitelist.push(element);
				});

		// We add the already attached project into the tagify-textarea component.
		this.subscriptions.add(
			this.staffDataExchangeService.collaborator$.subscribe(
				(collab: Collaborator) =>
				this.tagify.addTags(
					this.collaborator.missions
					.map(function(mission) { return mission.name; }))));

		// We register the listener for the tagify-textarea.
		this.tagify
			.on('add', this.boundAddProject)
			.on('remove', this.boundRemoveProject);
	}

	/**
	 * Check if the staff member available in this form is a brand new, unregistered, staff member or an already registered one.
	 * To add or remove skills, projects, the staff object must have an id.
	 */
	checkStaffMemberExist(event: any): boolean {
		if (this.collaborator.idStaff === null) {
			this.messageService.error('You cannot update a skill, or a project, of an unregistered staff member. '
				+ 'Please saved this new member first !');
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Register a staff member into an existing project
	 * @param event ADD event fired by the tagify component.
	 */
	addProject(event: CustomEvent) {
		if (Constants.DEBUG) {
			console.log ('Adding the project', event.detail.data.value);
		}

		const project = this.projectService.getProject (event.detail.data.value);
		if (project === undefined) {
			console.log ('SEVERE ERROR : Unregistered project', event.detail.data.value);
			return;
		}

		// If this project is already present, we do not continue,
		// and particularly we do not try to insert a second time the same project
		if (this.collaborator.missions.find(mission => mission.idProject === project.id)) {
			return;
		}

		this.collaborator.missions.push(new Mission(project.id, project.name));

		// We have already loaded or saved the collaborator, so we can add each new project as they appear, one by one.
		if (this.collaborator.idStaff) {
			this.updateProject(this.collaborator.idStaff,
			new Mission(project.id, project.name),
			this.staffService.addProject.bind(this.staffService));
		}

	}

	/**
	 * Unregister a staff member from a project.
	 * @param event ADD event fired by the tagify component.
	 */
	removeProject(event: CustomEvent) {
		if (Constants.DEBUG) {
			console.log ('Removing the project', event.detail.data.value);
		}

		// This project HAS TO BE registered inside the project.
		if ( (this.collaborator.missions === undefined) || (this.collaborator.missions.length === 0)) {
			console.error ('SHOULD NOT PASS HERE : ' + this.collaborator.lastName
			+ ' does not contain any mission. So, we should not be able to remove one');
			return;
		}

		const mission = this.collaborator.missions.find (mi => mi.name === event.detail.data.value);
		if (mission === undefined) {
			console.log ('SHOULD NOT PASS HERE : Cannot revoke the project '
			+ event.detail.data.value + ' from collaborator' + this.collaborator.firstName + ' ' + this.collaborator.lastName);
			return;
		}

		const indexOfMission = this.collaborator.missions.indexOf(mission);
		if (Constants.DEBUG) {
			console.log ('Index of the mission ' + mission.name, indexOfMission);
		}

		this.collaborator.missions.splice(indexOfMission, 1);

		// We have already loaded or saved the collaborator, so we can add each new project as they appear, one by one.
		if (this.collaborator.idStaff) {
			this.updateProject(this.collaborator.idStaff, mission, this.staffService.removeProject.bind(this.staffService));
		}

	}

	/**
	 *  Update a project associated to a staff member. This might be an addition or a removal.
	 * @param idStaff the staff member identifier
	 * @param idProject the project identifier
	 * @param callback the callback function, which might be staffService.addProject or staffService.removeProject
	 * @returns TRUE if the operation has succeeded, FALSE otherwise.
	 */
	updateProject(idStaff: number, mission:  Mission, callback: (idStaff: number, idProject:  number) => Observable<BooleanDTO>) {
		callback(idStaff, mission.idProject)
		.subscribe (result => {
			if (!result) {
				this.undoRemoveProject(mission);
				this.messageService.error (result.message);
			}
		},
		response_in_error => {
			if (Constants.DEBUG) {
				console.log('Error ', response_in_error);
			}
			this.undoRemoveProject(mission);
		});

	}

	/**
	 * Re-introduce a missio after a failed remove.
	 * @param mission the mission to be re-introduced.
	 */
	undoRemoveProject(mission: Mission) {
		if (Constants.DEBUG) {
			console.log ('Update failed, we reintroduce the project ' + mission.name);
		}
		this.collaborator.missions.push(mission);
		this.tagify.addTags([mission.name]);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
