import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { ProjectService } from '../../service/project.service';
import { Constants } from '../../constants';
import { MessageService } from '../../message/message.service';
import { CinematicService } from '../../service/cinematic.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ProjectStaffService } from '../project-staff-service/project-staff.service';
import { BaseComponent } from '../../base/base.component';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';

@Component({
	selector: 'app-project-staff',
	templateUrl: './project-staff.component.html',
	styleUrls: ['./project-staff.component.css']
})
export class ProjectStaffComponent extends BaseComponent implements OnInit, OnDestroy {

	public dataSource;

	public displayedColumns: string[] = ['fullname', 'active', 'external', 'firstCommit', 'lastCommit', 'numberOfCommits', 'numberOfFiles'];

	public idProject: number;

	public sub: any;

	@ViewChild(MatSort, { static: true }) sort: MatSort;

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
			this.sub = this.route.params.subscribe(params => {
				if (Constants.DEBUG) {
					console.log('params[\'id\'] ' + params['id']);
				}
				if (params['id'] == null) {
					this.idProject = null;
				} else {
					this.idProject = + params['id']; // (+) converts string 'id' to a number
					this.loadContributors();
				}
			}));

		this.subscriptions.add(
			this.cinematicService.tabProjectActivated$.subscribe(
				index => {
					if (index === Constants.PROJECT_IDX_TAB_STAFF) {
						// Either we reach this component with this url '/project/:id' and the selection of the tab Staff
						// Or we reach this component directly with this url '/project/:id/staff'
						// We notify the cinematicService with the complete url '/project/:id/staff
						// in order to be able to jump back directly to this list
						// when the user will click the list button on the navigation block at the top left corner.
						const urlProjectStaffList = (this.router.url.indexOf('/staff') === -1) ?
							this.router.url + '/staff' : this.router.url;

						this.cinematicService.setForm(Constants.PROJECT_TAB_STAFF, urlProjectStaffList);
						/**
						 * Loading the contributors
						 */
						this.loadContributors();
					}

				}));

	}

	/**
	 * Load the contributors for the current project.
	 */
	loadContributors() {
		this.subscriptions.add(
			this.projectService.contributors(this.idProject).subscribe(
				contributorsDTO => {
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
				}, error => {
					if (error.status === 404) {
						if (Constants.DEBUG) {
							console.log('404 : cannot find contributors for the id ' + this.idProject);
						}
						this.messageService.error('Cannot retrieve the contributors for the project identifier ' + this.idProject);
					} else {
						console.error(error);
					}
				},
				() => {
					if (Constants.DEBUG) {
						console.log('Loading complete for id ' + this.idProject);
					}
				}));
	}

	/**
	 * Return the CSS class corresponding to the active vs inactive status of a developer.
	 */
	public class_active_inactive(active: boolean) {
		return active ? 'contributor_active' : 'contributor_inactive';
	}

	public routeStaff(idStaff: number) {
		this.tabsStaffListComponent.inMasterDetail = true;
		this.router.navigate(['/user/' + idStaff], {});
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
