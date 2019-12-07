import { Component, OnInit, ViewChild, OnDestroy, Input } from '@angular/core';
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
import { BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { thresholdSturges } from 'd3';

@Component({
	selector: 'app-project-staff',
	templateUrl: './project-staff.component.html',
	styleUrls: ['./project-staff.component.css']
})
export class ProjectStaffComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Observable emiting the current active project.
	 */
	@Input() project$: BehaviorSubject<Project>;

	/**
	 * Current active project obtained from from `project$`.
	 */
	project; Project;

	public dataSource;

	public displayedColumns: string[] = ['fullname', 'active', 'external', 'firstCommit', 'lastCommit', 'numberOfCommits', 'numberOfFiles'];

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
			this.cinematicService.tabProjectActivated$.subscribe(
				index => {
					if ((this.project) && (index === Constants.PROJECT_IDX_TAB_STAFF)) {
						// Either we reach this component with this url '/project/:id' and the selection of the tab Staff
						// Or we reach this component directly with this url '/project/:id/staff'
						// We notify the cinematicService with the complete url '/project/:id/staff
						// in order to be able to jump back directly to this list
						// when the user will click the list button on the navigation block at the top left corner.
						const urlProjectStaffList = (this.router.url.indexOf('/staff') === -1) ?
							this.router.url + '/staff' : this.router.url;

						this.cinematicService.setForm(Constants.PROJECT_TAB_STAFF, urlProjectStaffList);
						this.subscriptions.add(
							this.project$.subscribe(project => {
								this.project = project;
								//
								// Loading the contributors
								//
								this.loadContributors();
							}));
					}
				}));

	}

	/**
	 * Load the contributors for the current project.
	 */
	loadContributors() {
		this.subscriptions.add(
			this.projectService.contributors((!this.project) ? this.project.idProject : -1).subscribe(
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
							console.log('404 : cannot find contributors for the id ' + this.project.idProject);
						}
						this.messageService.error('Cannot retrieve the contributors for the project identifier ' + this.project.idProject);
					} else {
						console.error(error);
					}
				},
				() => {
					if (Constants.DEBUG) {
						console.log('Loading complete for id ' + this.project.idProject);
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
