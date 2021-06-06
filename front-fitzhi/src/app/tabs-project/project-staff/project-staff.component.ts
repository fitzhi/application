import { AfterContentInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { EMPTY } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { UserSetting } from 'src/app/base/user-setting';
import { Contributor } from 'src/app/data/contributor';
import { ContributorsDTO } from 'src/app/data/external/contributorsDTO';
import { traceOn } from 'src/app/global';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';
import { BaseComponent } from '../../base/base.component';
import { Constants } from '../../constants';
import { MessageService } from '../../interaction/message/message.service';
import { CinematicService } from '../../service/cinematic.service';
import { ProjectService } from '../../service/project/project.service';
import { ProjectStaffService } from '../project-staff-service/project-staff.service';

@Component({
	selector: 'app-project-staff',
	templateUrl: './project-staff.component.html',
	styleUrls: ['./project-staff.component.css']
})
export class ProjectStaffComponent extends BaseComponent implements OnInit, OnDestroy, AfterContentInit {

	public dataSource: MatTableDataSource<Contributor>;

	public displayedColumns: string[] = ['fullname', 'active', 'external', 'firstCommit', 'lastCommit', 'numberOfCommits', 'numberOfFiles'];

	@ViewChild(MatSort) sort: MatSort;

	/**
	 * The table 
	 */
	@ViewChild(MatTable) table: MatTable<any>;

	/**
	 * The paginator of the staff data source.
	 */
	@ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

	/**
	 * Key used to save the page size in the local storage.
	 */
	public pageSize = new UserSetting('project-staff.pageSize', 5);
	 
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
						if (traceOn()) {
							console.log ('urlProjectStaffList', urlProjectStaffList);
						}
						this.cinematicService.setForm(Constants.PROJECT_TAB_STAFF, urlProjectStaffList);
					}
				}));
	}

	ngAfterContentInit() {
		this.subscriptions.add(
			this.cinematicService.tabProjectActivated$.pipe(
				//
				// If the end-user clicked on the project staff tab, we load the contributors involved in the project.
				//
				switchMap( (tabSelected: number) => {
					return (tabSelected === Constants.PROJECT_IDX_TAB_STAFF) ?
						this.projectService.projectLoaded$ : EMPTY;
				}))
				.subscribe({
					next: doneAndOk => {
						if (traceOn()) {
							this.projectService.dump(this.projectService.project, 'projectStaffComponent.ngOnInit()');
						}
						if (doneAndOk) {
							this.loadContributors();
						}
					}
				}
			)
		);
	}

	/**
	 * Load the contributors for the current project.
	 */
	loadContributors() {
		this.projectService.contributors$((this.projectService.project) ? this.projectService.project.id : -1)
			.pipe(take(1))
			.subscribe({
				next: contributorsDTO => this.manageDataSource(contributorsDTO),
				error: error => {
					if (error.status === 404) {
						if (traceOn()) {
							console.log('404 : cannot find contributors for the id ' + this.projectService.project.id);
						}
						this.messageService.error('Cannot retrieve the contributors for the project identifier ' + this.projectService.project.id);
					} else {
						console.error(error);
					}
				},
				complete: () => {
					if (traceOn()) {
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
			this.dataSource.paginator = this.paginator; 
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
		this.cinematicService.setForm(Constants.DEVELOPERS_CRUD, '/staff/' + idStaff);
		this.cinematicService.currentCollaboratorSubject$.next(idStaff);
	}

	/**
	 * This method is invoked if the user change the page size.
	 * @param $pageEvent event 
	 */
	public page($pageEvent: PageEvent) {
		this.pageSize.saveSetting($pageEvent.pageSize);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
