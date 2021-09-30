import { Component, OnInit, Input, ViewChild, OnDestroy, AfterViewInit } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { Unknown } from '../../../../data/unknown';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { ProjectGhostsDataSource } from '../project-ghosts-data-source';
import { MatPaginator } from '@angular/material/paginator';
import { Collaborator } from 'src/app/data/collaborator';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { take } from 'rxjs/operators';
import { MessageService } from 'src/app/interaction/message/message.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { traceOn } from 'src/app/global';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { SunburstCacheService } from '../../service/sunburst-cache.service';
import { GhostsService } from '../service/ghosts.service';
import { MatTable } from '@angular/material/table';

@Component({
	selector: 'app-table-ghosts',
	templateUrl: './table-ghosts.component.html',
	styleUrls: ['./table-ghosts.component.css']
})
export class TableGhostsComponent extends BaseDirective implements OnInit, OnDestroy, AfterViewInit {

	/**
	 * Datasource observable.
	 * This dataSource will be emit by the project-sunburst component.
	 * after each dashboard generation.
	 */
	@Input() dataSourceGhosts$;

	/**
	 * The undeclared contributors in the repository.
	 */
	public dataSource: ProjectGhostsDataSource;

	/**
	 * The paginator of the ghosts data source.
	 */
	@ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

	/**
	 * Array will be sortable
	 */
	@ViewChild(MatSort, { static: true }) sort: MatSort;

	/**
	 * The table
	 */
	@ViewChild('table', { static: true }) table: MatTable<Unknown>;

	public editableColumns: string[] = ['pseudo', 'lastCommit', 'numberOfCommits', 'numberOfFiles', 'login', 'technical', 'firstname', 'lastname', 'active', 'external', 'creation'];
	public enhancedColumns: string[] = ['pseudo', 'lastCommit', 'numberOfCommits', 'numberOfFiles', 'login', 'fullName', 'technical'];

	/**
	 * List of all developpers existing in the application.
	 */
	allStaff: Collaborator[] = [];

	constructor(
		private staffService: StaffService,
		private projectService: ProjectService,
		private ghostsService: GhostsService,
		private staffListService: StaffListService,
		private sunburstCacheService: SunburstCacheService,
		private messageBoxService: MessageBoxService,
		private messageService: MessageService) {
		super();
	}

	ngOnInit(): void {

		this.subscriptions.add(
			this.staffListService.allStaff$.subscribe({
				next: staff => this.allStaff = staff
			})
		);

		this.subscriptions.add(
			this.dataSourceGhosts$.subscribe({
					next: (dataSource: ProjectGhostsDataSource) => {
						if (traceOn()) {
							console.log(
								'Project %d %s reveived %s in the table of ghosts component',
								this.projectService.project.id,
								this.projectService.project.name,
								dataSource.data.length);
						}
						this.dataSource = new ProjectGhostsDataSource(dataSource.data);
					}
				}
			)
		);
	}

	ngAfterViewInit() {
		this.dataSource.paginator = this.paginator;
	}

	/**
	 * The check Box for the id **"technical"** has been checked or unchecked.
	 *
	 * @param ghost the ghost to be set as technical
	 */
	checkTechnical(ghost: Unknown): void {
		if (ghost.technical) {
			ghost.login = '';
			ghost.idStaff = -1;
			ghost.firstname = '';
			ghost.lastname = '';
			ghost.active = false;
			ghost.external = false;
		}

		this.projectService.updateGhost$ (this.projectService.project.id, ghost.pseudo, -1, ghost.technical)
			.pipe(take(1))
			.subscribe({
				next: result => {
					if (result) {
						this.messageService.info('The pseudo ' + ghost.pseudo + ' is now '
								+ (ghost.technical ? 'technical' : 'non technical'));
					}
				}
			});
}

	/**
	 * The check Box for the id "active" has been checked or unchecked.
	 */
	checkActive(ghost: Unknown) {
	}

	/**
	 * The check Box for the id "active" has been checked or unchecked.
	 */
	checkExternal(ghost: Unknown) {
	}

	public checkValue(technical: boolean) {
		return technical ? 'admin user' : '';
	}

	/**
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
		if ((ghost.firstname) && (ghost.firstname.length > 0)) {
			return true;
		}
		if ((ghost.lastname) && (ghost.lastname.length > 0)) {
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
		if (traceOn()) {
			console.groupCollapsed ('Creation of the the staff member');
			console.log ('Pseudo', ghost.pseudo);
			console.log ('Firstname', ghost.firstname);
			console.log ('Lastname', ghost.lastname);
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

		const similarStaff = this.staffListService.lookupSimilarStaff(collaborator);
		if (similarStaff) {
			this.messageBoxService.exclamation('Information',
				`<p>The application has detected a very similar collaborator, already registered for your pseudo :</p>
				<p><strong>${similarStaff.firstName} ${similarStaff.lastName}</strong>
				has already been declared in the staff list.
				<br/>He/she has been linked with the Github login : <strong>${similarStaff.login}</strong></p>
				<p><em>You should link this login with the pseudo.</em></p>`);
			return;
		}

		this.staffService.save$(collaborator)
			.pipe(take(1))
			.subscribe({
				next: (staff: Collaborator) => {
					collaborator.idStaff = staff.idStaff;
					ghost.staffRecorded = true;
					let pseudos = this.dataSource.data
						.map(g => g.pseudo)
						.filter(p => p !== ghost.pseudo);

					this.dataSource.removePseudo(ghost.pseudo);

					pseudos = this.ghostsService.extractMatchingUnknownContributors(pseudos, staff);
					if (traceOn()) {
						console.groupCollapsed ('Associated %d pseudo(s)', pseudos.length);
						pseudos.forEach(pseudo => console.log ('pseudo', pseudo));
						console.groupEnd();
					}
					pseudos.forEach(pseudo => {
						if (traceOn()) {
							console.log('removePseudo(%s)', ghost.pseudo);
						}
						this.dataSource.removePseudo(pseudo);
					});

					this.table.renderRows();

					this.sunburstCacheService.clearReponse();
					this.messageService.success('Staff member ' + staff.firstName + ' ' + staff.lastName + ' saved.');
					if (traceOn()) {
						console.log ('Onboarding the staff %d into the project %d', staff.idStaff, this.projectService.project.id);
					}
					this.projectService.onBoardStaffInProject(this.projectService.project.id,  staff.idStaff);
				}
			});
	}

	renderRows() {
		this.table.renderRows();
	}

	/**
	 * Handle the relation of this ghost to existing staff members
	 * @param ghost the given ghost
	 */
	handleRelatedLogin(ghost: Unknown) {
		if (!ghost.login) {
			this.detachGhost(ghost);
		} else {
			this.attachGhost(ghost);
		}
	}

	/**
	 * Attach a ghost to an existing staff member
	 * @param ghost The given ghost
	 * @returns **TRUE** if we attached the ghost, **FALSE** if we detached
	 */
	attachGhost(ghost: Unknown) {

		const selectedStaff = this.allStaff.filter(s => (s.login.toLowerCase().indexOf(ghost.login.toLowerCase()) === 0) );
		if (traceOn()) {
			if (selectedStaff.length > 0) {
				console.groupCollapsed('selected staff corresponding to %s', ghost.login);
				selectedStaff.forEach(staff => console.log (staff.firstName + ' ' + staff.lastName + ' & login : ' + staff.login));
				console.groupEnd();
			} else {
				console.log ('No staff is corresponding to the ghost login ' + ghost);
			}
		}

		if (selectedStaff.length === 1) {
			ghost.staffRelated = selectedStaff[0];
			ghost.login = selectedStaff[0].login;
			ghost.idStaff = ghost.staffRelated.idStaff;
			this.projectService
				.updateGhost$ (this.projectService.project.id, ghost.pseudo, ghost.idStaff, false)
				.pipe(take(1))
				.subscribe({
					next: result => {
						if (result) {
							this.table.renderRows();

							// We reset the cache to force a re-generation of the chart
							this.sunburstCacheService.clearReponse();

							this.messageService.info('The pseudo ' + ghost.pseudo + ' has been associated to '
								+ ghost.staffRelated.firstName + ' ' + ghost.staffRelated.lastName);
						}
					}
				});
		}
	}

	/**
	* Detach a ghost from an existing staff member
	* @param ghost The given ghost
	* @returns **TRUE** if we attached the ghost, **FALSE** if we detached
	*/
	detachGhost(ghost: Unknown) {
		// If the ghost was already associated, we reset this association
		if (ghost.idStaff  > 0) {
			this.projectService
				.updateGhost$ (this.projectService.project.id, ghost.pseudo, -1, false)
				.pipe(take(1))
				.subscribe(result => {
					if (result) {
						this.messageService.info('The pseudo ' + ghost.pseudo + ' is no more associated to an existing staff member');
						ghost.idStaff = -1;
						ghost.firstname = '';
						ghost.lastname = '';
						ghost.login = '';
					}
				}
			);
		}

		this.table.renderRows();
		// We reset the cache to force a re-generation of the chart
		this.sunburstCacheService.clearReponse();

		return false;
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
