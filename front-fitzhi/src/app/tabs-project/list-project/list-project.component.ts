import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import {Constants} from '../../constants';
import {Project} from '../../data/project';
import { ListProjectsService } from './list-projects-service/list-projects.service';
import { Router } from '@angular/router';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { ProjectService } from '../../service/project.service';
import { StaffListService } from '../../service/staff-list-service/staff-list.service';
import { Commit } from '../../data/commit';
import { traceOn } from '../../global';
import { ReferentialService } from '../../service/referential.service';
import { ProjectsDataSource } from './projects-data-source';
import { BaseComponent } from 'src/app/base/base.component';
import { UserSetting } from 'src/app/base/user-setting';
import { CinematicService } from 'src/app/service/cinematic.service';

@Component({
	selector: 'app-list-project',
	templateUrl: './list-project.component.html',
	styleUrls: ['./list-project.component.css']
})
export class ListProjectComponent extends BaseComponent implements OnInit, AfterViewInit {

	/**
	 * The datasource that contains the filtered projects
	 */
	dataSource: ProjectsDataSource;

	/**
	 * The projects list will be a sorted table.
	 */
	@ViewChild(MatSort, { static: true }) sort: MatSort;

	/**
	 * The paginator for the Project list.
	 */
	@ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

	/**
	 * The table
	 */
	@ViewChild('table', { static: true }) table: MatTable<Project>;

	public editableColumns: string[] =
			['staffEvaluation', 'sonarEvaluation', 'auditEvaluation', 'name', 'techno', 'lastCommit', 'lastCommitter'];

	/**
	 * The project identifier associated to the cached commit declared below.
	 */
	idProjectCached = -1;

	/**
	 * Commit saved in order to avoid multiple crawling seach in staff collection.
	 */
	commitCached: Commit;

	/**
	 * Key used to save the page size in the local storage.
	 */
	public pageSize = new UserSetting('project-list.pageSize', 5);

	constructor(
		private staffListService: StaffListService,
		public referentialService: ReferentialService,
		public projectService: ProjectService,
		private listProjectsService: ListProjectsService,
		public cinematicService: CinematicService,
		private router: Router) { super(); }

	ngOnInit() {

		if (localStorage.getItem('dev') === '1') {
			this.staffListService.loadStaff();
		}

		this.subscriptions.add(
			this.listProjectsService.filteredProjects$
				.subscribe({
					next: projects => this.updateData (projects)
				}
			)
		);

	}

	ngAfterViewInit(): void {
		this.dataSource.paginator = this.paginator; 
		this.dataSource.sort = this.sort;
	}

	/**
	 * Update the datasource with the given array of projects
	 * @param projects array of projects
	 */
	updateData (projects: Project[]) {
		this.dataSource = new ProjectsDataSource(projects);
		this.dataSource.sortingDataAccessor = (item: Project, property: string) => {
			switch (property) {
				case 'name':
					return item.name.toLocaleLowerCase();
				case 'staffEvaluation':
					return item.staffEvaluation;
				case 'sonarEvaluation':
					return this.projectService.calculateSonarEvaluation(this.projectService.project);
				case 'auditEvaluation':
					return item.auditEvaluation;
				case 'lastCommitter':
					return this.retrieveLastCommit(item.id).fullname();
				case 'lastCommit':
					return this.retrieveLastCommit(item.id).dateCommit;
			}
		};
		this.dataSource.paginator = this.paginator; 
		this.dataSource.sort = this.sort;
	}

	/**
	 * @param risk the risk evaluated for a project
	 * @returns the color figuring the risk evaluation.
	 */
	styleOfTheDot (risk: number) {
		const color = this.projectService.getRiskColor(risk);
		return { 'fill': color};
	}

	/**
	 * Retrieved the last commit for given project
	 * @param idProject the project identifier
	 */
	retrieveLastCommit(idProject: number): Commit {

		if (idProject === this.idProjectCached) {
			return this.commitCached;
		}

		this.commitCached = this.staffListService.retrieveLastCommit(idProject);
		this.idProjectCached = idProject;
		return this.commitCached;
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
