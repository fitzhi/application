import { Component, OnInit, Input, Output, ViewChild, OnDestroy, AfterViewInit } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { Constants } from '../../../../constants';
import { Unknown } from '../../../../data/unknown';
import { BaseComponent } from 'src/app/base/base.component';
import { ProjectGhostsDataSource } from 'target/classes/app/project/project-sunburst/project-ghosts/project-ghosts-data-source';
import { Project } from 'target/classes/app/data/project';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Collaborator } from 'src/app/data/collaborator';
import { StaffService } from 'target/classes/app/service/staff.service';
import { take } from 'rxjs/operators';
import { MessageService } from 'src/app/message/message.service';
import { StaffListService } from 'target/classes/app/staff-list-service/staff-list.service';
import { runInThisContext } from 'vm';

@Component({
	selector: 'app-table-ghosts',
	templateUrl: './table-ghosts.component.html',
	styleUrls: ['./table-ghosts.component.css']
})
export class TableGhostsComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Datasource observable.
	 * This dataSource will be emit by the project-sunburst component.
	 * after each dashboard generation.
	 */
	@Input() dataSourceGhosts$;

	/**
	 * Table is editable.
	 */
	@Input() editable;

	/**
	 * The undeclared contributors in the repository.
	 */
	dataSource: MatTableDataSource<Unknown>;

	/**
	 * Staff member associated to a row by its login.
	 */
	public relatedStaff: Collaborator;

	/**
	 * The paginator of the ghosts data source.
	 */
	@ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

	public editableColumns: string[] = ['pseudo', 'login', 'technical', 'firstname', 'lastname', 'active', 'external', 'creation'];
	public enhancedColumns: string[] = ['pseudo', 'login', 'fullName', 'technical'];

	/**
	 * Array will be sortable
	 */
	@ViewChild(MatSort, { static: true }) sort: MatSort;

	/**
	 * List of all developpers existing in the application.
	 */
	allStaff: Collaborator[] = [];

	constructor(
		private staffService: StaffService,
		private staffListService: StaffListService,
		private messageService: MessageService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.dataSourceGhosts$.subscribe((dataSource: ProjectGhostsDataSource) => {
				if (Constants.DEBUG) {
					console.log('Project ' + dataSource.project.id + ' ' + dataSource.project.name + ' reveived in the table of ghosts component');
				}
				this.dataSource = dataSource;
				this.dataSource.paginator = this.paginator;
		}));

		this.staffListService.loadStaff();
		this.subscriptions.add(
			this.staffListService.allStaff$.subscribe(staff => {
				this.allStaff = staff;
			}));
	}
	/**
	 * The check Box for the id "technical" has been checked or unchecked.
	 */
	checkTechnical(unknown: Unknown) {
		if (unknown.technical) {
			unknown.fullName = '';
			unknown.login = '';
			unknown.idStaff = -1;
			unknown.firstname = '';
			unknown.lastname = '';
			unknown.active = false;
			unknown.external = false;

		}
	}

	/**
	 * The check Box for the id "active" has been checked or unchecked.
	 */
	checkActive(unknown: Unknown) {
	}

	/**
	 * The check Box for the id "active" has been checked or unchecked.
	 */
	checkExternal(unknown: Unknown) {
	}

	public checkValue(technical: boolean) {
		return technical ? 'admin user' : '';
	}

	/**
	 * This function is called for each line of the ghosts table.
	 * Is the user allow to create a new ghost ?
	 * which means...
	 * It's not an automatic.
	 * It's not a record with an associated login.
	 * @param ghost the ghost displayed on line
	 * @return TRUE if the creation is not allowed, the field has to be readonly, FALSE otherwise.
	 */
	isCreationNotAllowed(ghost: Unknown): boolean {
		if (ghost.technical) {
			return true;
		}
		if (ghost.login.length > 0) {
			return true;
		}
		return false;
	}

	/**
	 * This function is called for each line of the ghosts table.
	 * Is the user allow to create a new ghost ?
	 * which means...
	 * It's not an automatic.
	 * It's not a record with a firstname or a lastname already filled.
	 * @param ghost the ghost displayed on line
	 * @return TRUE if the association is not allowed, the field has to be readonly, FALSE otherwise.
	 */
	isAssociationNotAllowed(ghost: Unknown): boolean {
		if (ghost.technical) {
			return true;
		}
		if (ghost.firstname.length > 0) {
			return true;
		}
		if (ghost.lastname.length > 0) {
			return true;
		}
		return false;
	}

	addStaff(ghost: Unknown) {
		if (Constants.DEBUG) {
			console.groupCollapsed ('Creation of the the staff member');
			console.log ('Firstname', ghost.firstname);
			console.log ('Lastname', ghost.lastname);
			console.log ('Firstname', ghost.login);
			console.log ('active', ghost.active);
			console.log ('external', ghost.external);
			console.groupEnd();
		}
		const collaborator = new Collaborator();
		collaborator.idStaff = -1;
		collaborator.firstName = ghost.firstname;
		collaborator.lastName = ghost.lastname;
		collaborator.login = ghost.pseudo;
		collaborator.active = ghost.active;
		collaborator.external = ghost.external;
		this.staffService.save(collaborator)
			.pipe(take(1))
			.subscribe(staff => {
				this.messageService.info('Staff member ' + staff.firstName + ' ' + staff.lastName + ' saved');
			});
	}

	/**
	 * @param ghost the actual ghost line concerned.
	 * @returns TRUE if the related login typed is matching an existing staff member.
	 */
	relatedLoginMatch(ghost: Unknown): boolean {
		const selectedStaff = this.allStaff.filter(s => (s.login.toLowerCase() === ghost.login));
		if (selectedStaff.length === 1) {
			this.relatedStaff = selectedStaff[0];
			return true;
		} else {
			return false;
		}
	}

	changeLogin($event: string) {
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}


}
