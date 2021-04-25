import { Component, OnInit, OnDestroy, AfterViewInit, ViewChild } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { AnalysisTypeSlice } from '../analysis-type-slice';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { UserSetting } from 'src/app/base/user-setting';
import { traceOn } from 'src/app/global';
import { Router } from '@angular/router';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Constants } from 'src/app/constants';
import { Form } from 'src/app/service/Form';

@Component({
	selector: 'app-pie-projects',
	templateUrl: './pie-projects.component.html',
	styleUrls: ['./pie-projects.component.css']
})
export class PieProjectsComponent extends BaseComponent implements OnDestroy, OnInit, AfterViewInit {

	/**
	 * Datasource of the table.
	 */
	dataSource = new MatTableDataSource<Project>();

	/**
	 * Columns of the table
	 */
	displayedColumns: string[] = ['name'];

	/**
	 * Style color of the header.
	 */
	colorHeader: string;

	/**
	 * Type of slice activated
	 */
	typeSlice = AnalysisTypeSlice;

	/**
	 * The table in the component
	 */
	@ViewChild(MatTable) table: MatTable<any>;

	/**
	 * The paginator of the displayed datasource.
	 */
	 @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

	 /**
	  * The array listed in the Table
	  */
	public projects: Project[] = [];

	/**
	 * Key used to save the page size in the local storage.
	 */
	 public pageSize = new UserSetting('pie-projects-staff.pageSize', 5);


	constructor(
		public pieDashboardService: PieDashboardService,
		private cinematicService: CinematicService,
		private router: Router) {

		super();
		
		// Set the color of the colum header depending on the activated slice.
		this.subscriptions.add(
			this.pieDashboardService.sliceActivated$
				.subscribe({
					next: slice => {
						this.colorHeader = slice.backgroundColor;
						this.dataSource.data = slice.children; 
					}
				})
		);
	}
	
	ngOnInit(): void {
		this.manageDataSource();
	}
	
	/**
	 * Manage the datasource associated to the table
	 */
	 manageDataSource(): void {
		this.dataSource.paginator = this.paginator; 
	}

	ngAfterViewInit(): void {
	}

	/**
	 * Removing useless subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

	/**
	 * Route the application to the corresponding Project form.
	 * 
	 * @param id the selected project identifier
	 */
	 routeProject(id: number): void {
		if (traceOn()){
			console.log ('Project %d is selected', id);
		}
		this.cinematicService.currentActiveForm$.next(new Form(Constants.PROJECT_TAB_FORM, 'Project') );
		this.router.navigate(['/project/' + id], {});
	}

	/**
	 * Highlight the line of an activated project.
	 * 
	 * @param id the highlighted project identifier
	 */
	 enterProject(id: number): void {
		if (traceOn()){
			console.log ('Project %d is activated', id);
		}
		document.getElementById('project-' + id).setAttribute('style', 'background-color: ' + this.colorHeader );
	}

	/**
	 * Inactive the line of an unactivated project.
	 * 
	 * @param id the left project identifier
	 */
	 leaveProject(id: number): void {
		if (traceOn()){
			console.log ('Project %d is left', id);
		}
		document.getElementById('project-' + id).setAttribute('style', 'background-color: '  );
	}

}
