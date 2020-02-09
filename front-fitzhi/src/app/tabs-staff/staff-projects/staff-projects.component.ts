import { Constants } from '../../constants';
import { Collaborator } from '../../data/collaborator';
import { StaffDTO } from '../../data/external/staffDTO';
import { MessageService } from '../../message/message.service';
import { ProjectService } from '../../service/project.service';
import { StaffService } from '../../service/staff.service';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { Component, OnInit, OnDestroy, Input, AfterViewInit, ViewChild } from '@angular/core';

import { BaseComponent } from '../../base/base.component';
import Tagify from '@yaireo/tagify';
import { Mission } from 'src/app/data/mission';
import { BooleanDTO } from 'src/app/data/external/booleanDTO';
import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { TableGhostsComponent } from 'src/app/project/project-sunburst/project-ghosts/table-ghosts/table-ghosts.component';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';

@Component({
	selector: 'app-staff-projects',
	templateUrl: './staff-projects.component.html',
	styleUrls: ['./staff-projects.component.css']
})
export class StaffProjectsComponent extends BaseComponent implements OnInit, OnDestroy, AfterViewInit {

	/* tslint:disable: no-trailing-whitespace */
	/**
 	* Selected TAB.  
 	* This observable is fired by the StaffComponent class when the user changes the tab selected.
 	*/
	/* tslint:enable: no-trailing-whitespace */
	@Input() selectedTab$;

	/**
	 * Employee retrieve from `StaffComponent` access.
	 */
	public collaborator: Collaborator;

	/**
	 * JS object handling the projects component.
	 */
	tagify: Tagify;

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Bound `addProject` to the current active component.  
	 * The goal of this bind is to access the member variables of this class, such as the projet, inside lambda expressions.
	 */
	/* tslint:enable: no-trailing-whitespace */
	private boundAddProject: any;

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Bound `removeProject` to the current active component.  
	 * The goal of this bind is to access the member variables of this class, such as the projet, inside lambda expressions.
	 */
	/* tslint:enable: no-trailing-whitespace */
	private boundRemoveProject: any;

	/**
	 * The datasource containing the missions of the staff member.
	 */
	public dataSource;

	/**
	 * The table of missions is sortable.
	 */
	@ViewChild(MatSort, { static: true }) sort: MatSort;

	/**
	 * The columns to be displayed in the table of missions.
	 */
	public displayedColumns: string[] = ['name', 'firstCommit', 'lastCommit', 'numberOfCommits', 'numberOfFiles'];

	/**
	 * This `BehaviorSubject` is hosting the number of missions for the current active project.
	 * This number is displayed on a badge-pill on top of the panal.
	 */
	public numberOfMissions$ = new BehaviorSubject<number>(0);

	constructor(
		private messageService: MessageService,
		private staffService: StaffService,
		private projectService: ProjectService,
		private staffDataExchangeService: StaffDataExchangeService) {
		super();

		this.boundAddProject = this.addProject.bind(this);
		this.boundRemoveProject = this.removeProject.bind(this);

	}

	ngOnInit(): void  {
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
	 * @param missions array of Missions declared within the project.
	 */
	loadMissions(missions: Mission[]): void {
		this.dataSource = new MatTableDataSource(missions);
		this.dataSource.data = missions;
		this.dataSource.sort = this.sort;
		this.subscriptions.add(
			this.dataSource.connect().subscribe(data => this.collaborator.missions = data));
		this.dataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
			if (typeof data[sortHeaderId] === 'string') {
				return data[sortHeaderId].toLocaleLowerCase();
			}
			return data[sortHeaderId];
		};

		if (Constants.DEBUG) {
			console.log ('Missions loaded', missions.length);
		}
		this.numberOfMissions$.next(missions.length);
	}

	ngAfterViewInit(): void {
		const input = document.querySelector('textarea[name=projects]');
		this.tagify = new Tagify (input, {
			enforceWhitelist : true,
			whitelist        : []
		});

		// We setup the whitelist of the component
		// This test of non nullable and non 'undefined-able' is there for Karma testing purpose.
		if (this.projectService.allProjects) {
			this.projectService.allProjects
				.map(function(project) { return project.name; }).forEach(element => {
						this.tagify.settings.whitelist.push(element);
					});
		}

		// We add the already attached project into the tagify-textarea component.
		this.subscriptions.add(
			this.staffDataExchangeService.collaborator$.subscribe(
				(collab: Collaborator) => {
					this.removeValues();
					// We add this test to avoid an empty-warning inside the component
					if (this.collaborator.missions.length > 0) {
						this.tagify.addTags(
							this.collaborator.missions.
							map(function(mission) { return mission.name; }));
					}
				}));

		// We register the listener for the tagify-textarea.
		this.tagify
			.on('add', this.boundAddProject)
			.on('remove', this.boundRemoveProject);
	}

	/**
	 * Cleanup the tagify component from all tags.
	 */
	private removeValues(): void {
		this.tagify.off('remove', this.boundRemoveProject);
		this.tagify.removeAllTags();
		this.tagify.on('remove', this.boundRemoveProject);
	}

	/**
	 * Register a staff member into an existing project
	 * @param event ADD event fired by the tagify component.
	 */
	addProject(event: CustomEvent): void {
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
		this.numberOfMissions$.next(this.collaborator.missions.length);
		this.loadMissions(this.collaborator.missions);

		// We have already loaded or saved the collaborator, so we can add each new project as they appear, one by one.
		if (this.collaborator.idStaff) {
			this.updateProject(this.collaborator.idStaff,
			new Mission(project.id, project.name),
			this.staffService.addProject.bind(this.staffService));
		}
	}

	/**
	 * This method operates a rollback and reintroduces a project whose deletion has been rejected.
	 * @param projectName the name to be reintroduced into the tagify componenet
	 */
	rollbackRemove(projectName: string): void {

		if (Constants.DEBUG) {
			console.log ('Rollback the removal of %s', projectName);
		}
		this.tagify.off('add', this.boundAddProject);
		this.tagify.addTags([projectName]);
		this.tagify.on('add', this.boundAddProject);
	}

	/**
	 * Unregister a staff member from a project.
	 * @param event ADD event fired by the tagify component.
	 */
	removeProject(event: CustomEvent): void {

		const projectName = event.detail.data.value;
		if (Constants.DEBUG) {
			console.log ('Removing the project %s', projectName);
		}

		// This project HAS TO BE registered inside the mission of this collaborator.
		if ( (!this.collaborator.missions) || (this.collaborator.missions.length === 0)) {
			console.error ('SHOULD NOT PASS HERE : ' + this.collaborator.lastName
			+ ' does not contain any mission. So, we should not be able to remove one');
			this.rollbackRemove(event.detail.data.value);
			return;
		}

		const mission = this.collaborator.missions.find (mi => mi.name === projectName);
		if (!mission) {
			this.rollbackRemove(projectName);
			console.error ('SHOULD NOT PASS HERE : Cannot revoke the project '
			+ event.detail.data.value + ' from collaborator' + this.collaborator.firstName + ' ' + this.collaborator.lastName);
			return;
		}
		if ((mission.lastCommit) &&  (mission.numberOfCommits > 0)) {
			this.rollbackRemove(projectName);
			this.messageService.warning('Cannot remove the project ' +  mission.name + ' with detected commits');
			return;
		}

		const indexOfMission = this.collaborator.missions.indexOf(mission);
		if (Constants.DEBUG) {
			console.log (
					'%d is the index of the selected mission %s '
				+ 	' to be removed from this collaborator\' missions', indexOfMission, mission.name);
		}
		this.collaborator.missions.splice(indexOfMission, 1);
		this.numberOfMissions$.next(this.collaborator.missions.length);
		this.loadMissions(this.collaborator.missions);

		// We have already loaded or saved the collaborator, so we can add each new project as they appear, one by one.
		if (this.collaborator.idStaff) {
			this.updateProject(this.collaborator.idStaff, mission, this.staffService.removeProject.bind(this.staffService));
		}

	}

	/**
	 * Update a project associated to a staff member. This might be an addition or a removal.
	 * @param idStaff the staff member identifier
	 * @param idProject the project identifier
	 * @param callback the callback function, which might be `staffService.addProject` or `staffService.removeProject`
	 */
	updateProject(idStaff: number, mission:  Mission, callback: (idStaff: number, idProject:  number) => Observable<BooleanDTO>): void {
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

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Re-introduce a mission after a failed remove.  
	 * __This method is only executed when the back-end returns an internal error__
	 * @param mission the mission to be re-introduced.
	 */
	/* tslint:enable: no-trailing-whitespace */
	undoRemoveProject(mission: Mission): void {
		if (Constants.DEBUG) {
			console.log ('Update failed, we re-introduce the project %s', mission.name);
		}
		this.collaborator.missions.push(mission);
		this.tagify.addTags([mission.name]);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy(): void {
		super.ngOnDestroy();
	}

}
