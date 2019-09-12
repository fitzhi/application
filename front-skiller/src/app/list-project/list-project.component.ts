import { Component, OnInit, ViewChild } from '@angular/core';
import {CinematicService} from '../service/cinematic.service';
import {Constants} from '../constants';
import {Project} from '../data/project';
import { ListProjectsService } from '../list-projects-service/list-projects.service';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { ProjectService } from '../service/project.service';
import { StaffListService } from '../staff-list-service/staff-list.service';

@Component({
	selector: 'app-list-project',
	templateUrl: './list-project.component.html',
	styleUrls: ['./list-project.component.css']
})
export class ListProjectComponent implements OnInit {

	/**
	 * The datasource that contains the filtered projects;
	 */
	dataSource: MatTableDataSource<Project>;

	/**
	 * The projects list will be a sorted table.
	 */
	@ViewChild(MatSort, { static: true }) sort: MatSort;

	/**
	 * The paginator of the ghosts data source.
	 */
	@ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

	public editableColumns: string[] = ['risk', 'name', 'lastCommit', 'lastCommitter'];

	constructor(
		private staffListService: StaffListService,
		private projectService: ProjectService,
		private listProjectsService: ListProjectsService,
		private router: Router) {}

	ngOnInit() {

		if (localStorage.getItem('dev') === '1') {
			this.staffListService.loadStaff();
		}


		this.listProjectsService.filteredProjects$.subscribe(projects => {
			this.dataSource = new MatTableDataSource<Project>(projects);
			this.dataSource.sortingDataAccessor = (item: Project, property: string) => {
				switch (property) {
					case 'name':
						return item.name.toLocaleLowerCase();
					case 'risk':
						return item.risk;
				}
			};
			this.dataSource.sort = this.sort;
		});
	}

	/**
	 * @param risk the risk evaluated for a project
	 * @returns the color figuring the risk evaluation.
	 */
	styleOfTheDot (risk: number) {
		const color = this.projectService.getRiskColor(risk);
		return { 'fill': color};
	}

	public search(source: string): void {
		if (Constants.DEBUG) {
			console.log('Searching a project');
		}
	}
}
