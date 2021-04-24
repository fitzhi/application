import { Component, OnInit, OnDestroy, AfterViewInit, ViewChild } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { DataSource } from '@angular/cdk/table';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { AnalysisTypeSlice } from '../analysis-type-slice';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { switchMap } from 'rxjs/operators';
import { Slice } from 'dynamic-pie-chart';
import { of } from 'rxjs';
import { MatPaginator } from '@angular/material/paginator';
import { UserSetting } from 'src/app/base/user-setting';


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
	 public pageSize = new UserSetting('pie-projjects-staff.pageSize', 5);


	constructor(public pieDashboardService: PieDashboardService) {

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
}
