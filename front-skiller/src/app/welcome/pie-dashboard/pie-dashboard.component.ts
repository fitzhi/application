import { Component, Input, OnInit, AfterViewInit, AfterContentInit, ViewEncapsulation } from '@angular/core';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { Slice } from './slice';
import * as d3 from 'd3';
import { PieDashboardService } from './service/pie-dashboard.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { BaseComponent } from 'src/app/base/base.component';
import { switchMap } from 'rxjs/operators';
import { ProjectService } from 'src/app/service/project.service';
import { Project } from 'src/app/data/project';

@Component({
	selector: 'app-pie-dashboard',
	templateUrl: './pie-dashboard.component.html',
	styleUrls: ['./pie-dashboard.component.css']
})
export class PieDashboardComponent extends BaseComponent implements OnInit {

	constructor(
		private projectService: ProjectService,
		private pieDashboardService: PieDashboardService) {
			super();
	}

	ngOnInit() {
		this.projectService.allProjects$
			.subscribe({
				next: projects => this.pieDashboardService.generatePieSlices(projects)
			});
	}

}
