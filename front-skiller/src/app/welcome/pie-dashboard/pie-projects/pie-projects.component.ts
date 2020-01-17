import { Component, OnInit } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { DataSource } from '@angular/cdk/table';
import { Project } from 'src/app/data/project';
import { ActivatedProjectsDatasSource } from './activated-projects-datasource';


@Component({
	selector: 'app-pie-projects',
	templateUrl: './pie-projects.component.html',
	styleUrls: ['./pie-projects.component.css']
})
export class PieProjectsComponent implements OnInit {

	dataSource: DataSource<Project>;

	displayedColumns: string[] = ['name'];

	constructor(public pieDashboardService: PieDashboardService) {
		this.dataSource = new ActivatedProjectsDatasSource(this.pieDashboardService);
	}

	ngOnInit() {
		this.pieDashboardService.projectsActivated$.subscribe(ps => ps.forEach (p => console.log(p.name)));

	}

}
