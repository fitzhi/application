import { Component, OnInit, ViewChild, OnDestroy, Input } from '@angular/core';
import { ProjectService } from '../../service/project.service';
import { Constants } from '../../constants';
import { MessageService } from '../../message/message.service';
import { CinematicService } from '../../service/cinematic.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource, MatTable } from '@angular/material/table';
import { ProjectStaffService } from '../project-staff-service/project-staff.service';
import { BaseComponent } from '../../base/base.component';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { Project } from 'src/app/data/project';
import { take, switchMap } from 'rxjs/operators';
import { ContributorsDTO } from 'src/app/data/external/contributorsDTO';
import { Contributor } from 'src/app/data/contributor';

@Component({
	selector: 'app-project-staff',
	templateUrl: './project-staff.component.html',
	styleUrls: ['./project-staff.component.css']
})
export class ProjectStaffComponent extends BaseComponent implements OnInit, OnDestroy {

	public dataSource: MatTableDataSource<Contributor>;

	sub: any;

	public displayedColumns: string[] = ['fullname', 'active', 'external', 'firstCommit', 'lastCommit', 'numberOfCommits', 'numberOfFiles'];

	@ViewChild(MatSort, { static: true }) sort: MatSort;

	@ViewChild(MatTable, { static: true }) table: MatTable<any>;

	constructor(
		private projectService: ProjectService,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private cinematicService: CinematicService,
		private router: Router,
		private tabsStaffListComponent: TabsStaffListService,
		private projectStaffService: ProjectStaffService) {
		super();
	}

	ngOnInit() {

		this.subscriptions.add(
			this.cinematicService.tabProjectActivated$.subscribe(
				index => {
					if ((this.projectService.project) && (index === Constants.PROJECT_IDX_TAB_STAFF)) {
						//
						// Either we reach this component with this url '/project/:id' and the selection of the tab Staff
						// Or we reach this component directly with this url '/project/:id/staff'
						//
						// We notify the cinematicService with the complete url '/project/:id/staff
						// in order to be able to jump back directly to this list
						// when the user will click the list button on the navigation block at the top left corner.
						//
						const urlProjectStaffList = (this.router.url.indexOf('/staff') === -1) ?
							this.router.url + '/staff' : this.router.url;
						if (Constants.DEBUG) {
							console.log ('urlProjectStaffList', urlProjectStaffList);
						}
						this.cinematicService.setForm(Constants.PROJECT_TAB_STAFF, urlProjectStaffList);
					}
				}));

		this.subscriptions.add(
			this.cinematicService.tabProjectActivated$.pipe(
				//
				// If the end-user clicked on the project staff tab, we load the contributors involved in the project.
				//
				switchMap( (tabSelected: number) => {
					return (tabSelected === Constants.PROJECT_IDX_TAB_STAFF) ?
						this.projectService.projectLoaded$ : EMPTY;
				})).subscribe({
					next: doneAndOk => {
						if (Constants.DEBUG) {
							this.projectService.dump(this.projectService.project, 'projectStaffComponent.ngOnInit()');
						}
						if (doneAndOk) {
							this.loadContributors();
						}
					}
				}));
	}

	/**
	 * Load the contributors for the current project.
	 */
	loadContributors() {
		this.projectService.contributors((this.projectService.project) ? this.projectService.project.id : -1)
			.pipe(take(1))
			.subscribe({
				next: contributorsDTO => this.manageDataSource(contributorsDTO),
				error: error => {
					if (error.status === 404) {
						if (Constants.DEBUG) {
							console.log('404 : cannot find contributors for the id ' + this.projectService.project.id);
						}
						this.messageService.error('Cannot retrieve the contributors for the project identifier ' + this.projectService.project.id);
					} else {
						console.error(error);
					}
				},
				complete: () => {
					if (Constants.DEBUG) {
						console.log('Loading complete for id ' + this.projectService.project.id);
					}
				}
			});
	}

	/**
	 * Manage the datasource associated to the table
	 * @param contributorsDTO container of contributors retrieved from the back-end.
	 */
	manageDataSource(contributorsDTO: ContributorsDTO) {

		if (!this.dataSource) {
			this.dataSource = new MatTableDataSource(contributorsDTO.contributors);
			this.dataSource.sort = this.sort;
			this.projectStaffService.contributors = this.dataSource.data;
			this.subscriptions.add(
				this.dataSource.connect().subscribe(data => this.projectStaffService.contributors = data));
			this.dataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
				if (typeof data[sortHeaderId] === 'string') {
					return data[sortHeaderId].toLocaleLowerCase();
				}
				return data[sortHeaderId];
			};
		} else {
			this.dataSource.data = contributorsDTO.contributors;
			this.table.renderRows();
		}

	}

	/**
	 * Return the CSS class corresponding to the active vs inactive status of a developer.
	 * @param active this `boolean` is `true` if this staff member is still active in the company, `false` otherwise.
	 */
	public class_active_inactive(active: boolean) {
		return active ? 'contributor_active' : 'contributor_inactive';
	}

	/**
	 * Route the end-user to the staff-form with a staff member selected in the list.
	 * @param idStaff the Staff identifier selected
	 */
	public routeStaff(idStaff: number) {
		this.tabsStaffListComponent.inMasterDetail = true;
		this.router.navigate(['/user/' + idStaff], {});
		this.cinematicService.setForm(Constants.DEVELOPERS_CRUD);
		this.cinematicService.emitActualCollaboratorDisplay.next(idStaff);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
