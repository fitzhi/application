import { Component, OnInit, OnDestroy } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { DataSource } from '@angular/cdk/table';
import { Project } from 'src/app/data/project';
import { ActivatedProjectsDatasSource } from './activated-projects-datasource';
import { BaseComponent } from 'src/app/base/base.component';
import { AnalysisTypeSlice } from '../analysis-type-slice';


@Component({
	selector: 'app-pie-projects',
	templateUrl: './pie-projects.component.html',
	styleUrls: ['./pie-projects.component.css']
})
export class PieProjectsComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Datasource of the table.
	 */
	dataSource: DataSource<Project>;

	/**
	 * Columns of the table
	 */
	displayedColumns: string[] = ['name'];

	/**
	 * Style color of the header.
	 */
	colorHeader: string;

	typeSlice = AnalysisTypeSlice;

	constructor(public pieDashboardService: PieDashboardService) {
		super();

		// Set the color of the colum header depending on the activated slice.
		this.subscriptions.add(
			this.pieDashboardService.sliceActivated$.subscribe(slice =>  {
				this.colorHeader = slice.backgroundColor;
			}));
	}

	ngOnInit() {
		this.dataSource = new ActivatedProjectsDatasSource(this.pieDashboardService);
	}

	/**
	 * Removing useless subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
