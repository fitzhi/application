import { Component, OnInit, Input, Output, ViewChild, OnDestroy, AfterViewInit, SimpleChanges } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { Constants } from '../../../../constants';
import { Unknown } from '../../../../data/unknown';
import { BaseComponent } from 'src/app/base/base.component';
import { ProjectGhostsDataSource } from 'src/app/project/project-sunburst/project-ghosts/project-ghosts-data-source';
import { MatPaginator } from '@angular/material/paginator';
import { Collaborator } from 'src/app/data/collaborator';
import { StaffService } from 'src/app/service/staff.service';
import { take, throwIfEmpty } from 'rxjs/operators';
import { MessageService } from 'src/app/message/message.service';
import { StaffListService } from 'src/app/staff-list-service/staff-list.service';
import { ProjectService } from 'src/app/service/project.service';

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
	 * The undeclared contributors in the repository.
	 */
	dataSource: ProjectGhostsDataSource;

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
		private projectService: ProjectService,
		private staffListService: StaffListService,
		private messageService: MessageService) {
		super();
	}

	ngOnInit() {
		if (this.dataSourceGhosts$) {
			if (this.dataSourceGhosts$) {
				this.subscriptions.add(
					this.dataSourceGhosts$.subscribe((dataSource: ProjectGhostsDataSource) => {
						if (Constants.DEBUG) {
							console.log('Project ' + dataSource.project.id + ' ' + dataSource.project.name + ' reveived in the table of ghosts component');
						}
						this.dataSource = dataSource;
						this.dataSource.paginator = this.paginator;
				}));
			}
		}

		if (this.subscriptions) {
			if ( (this.staffListService) && (this.staffListService.allStaff$)) {
				this.subscriptions.add(
					this.staffListService.allStaff$.subscribe(staff => {
						this.allStaff = staff;
				}));
			}
		}
	}
	/**
	 * The check Box for the id "technical" has been checked or unchecked.
	 */
	checkTechnical(unknown: Unknown) {
		if (unknown.technical) {
			unknown.login = '';
			unknown.idStaff = -1;
			unknown.firstname = '';
			unknown.lastname = '';
			unknown.active = false;
			unknown.external = false;

		}
		this.projectService.updateGhost (
			this.dataSource.project.id,
			unknown.pseudo,
			-1,
			unknown.technical)
		.pipe(take(1))
		.subscribe(result => {
			if (result) {
				this.messageService.info('The pseudo ' + unknown.pseudo + ' is now '
						+ (unknown.technical ? 'technical' : 'non technical'));
			}
		});
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

		// A staff has been created and saved for this ghost. This ghost is readonly for this session.
		if (ghost.staffRecorded) {
			return true;
		}

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

		// A staff has been created and saved for this ghost. This ghost is readonly for this session.
		if (ghost.staffRecorded) {
			return true;
		}

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

	/**
	 * @param ghost the ghost treated
	 * @returns TRUE if the staff record is complete to be saved.
	 */
	staffComplete(ghost: Unknown): boolean {
		return !( (!ghost.firstname) || (!ghost.lastname));
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
				ghost.staffRecorded = true;
				this.dataSource.removePseudo(ghost.pseudo);
				this.projectService
					.removeGhost(this.dataSource.project.id, ghost.pseudo)
					.pipe(take(1))
					.subscribe(result => {
						if (result) {
							this.messageService.success('Staff member ' + staff.firstName + ' ' + staff.lastName + ' saved');
						}
					});
			});	}

	handleRelatedLogin(ghost: Unknown) {

		const selectedStaff = this.allStaff.filter(s => (s.login.toLowerCase() === ghost.login));
		if (selectedStaff.length === 1) {
			ghost.staffRelated = selectedStaff[0];
			ghost.idStaff = ghost.staffRelated.idStaff;
			this.projectService.updateGhost (
				this.dataSource.project.id,
				ghost.pseudo,
				ghost.idStaff,
				false)
			.pipe(take(1))
			.subscribe(result => {
				if (result) {
					this.messageService.info('The pseudo ' + ghost.pseudo + ' has been associated to '
						+ ghost.staffRelated.firstName + ' ' + ghost.staffRelated.lastName);
				}
			});
			return true;
		} else {
			// If the ghost was already associated, we reset this association
			if (ghost.idStaff  > 0) {
				this.projectService.updateGhost (
					this.dataSource.project.id,
					ghost.pseudo,
					-1,
					false)
				.pipe(take(1))
				.subscribe(result => {
					if (result) {
						this.messageService.info('The pseudo ' + ghost.pseudo + ' is no more associated to an existing staff member');
					}
				});
			}
			ghost.idStaff = -1;
			return false;
		}
	}
	/**
	 * @param ghost the actual ghost line concerned.
	 * @returns TRUE if the related login typed is matching an existing staff member.
	 */
	relatedLoginMatch(ghost: Unknown): boolean {
		return (ghost.idStaff > 0);
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
