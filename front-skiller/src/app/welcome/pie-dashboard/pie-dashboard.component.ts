import { Component, Input, OnInit, AfterViewInit, AfterContentInit, ViewEncapsulation } from '@angular/core';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { Slice } from './slice';
import * as d3 from 'd3';
import { PieDashboardService } from './service/pie-dashboard.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { BaseComponent } from 'src/app/base/base.component';
import { switchMap } from 'rxjs/operators';
import { ProjectService } from 'src/app/service/project.service';

@Component({
	selector: 'app-pie-dashboard',
	templateUrl: './pie-dashboard.component.html',
	styleUrls: ['./pie-dashboard.component.css']
})
export class PieDashboardComponent extends BaseComponent implements OnInit {

	/**
	 * Observable emetting the configuration of the pie.
	 */
	private slices$ = new BehaviorSubject<Slice[]>([]);

	constructor(
		private projectService: ProjectService,
		private pieDashboardService: PieDashboardService) {
			super();
	}

	ngOnInit() {
		this.projectService.allProjectsIsLoaded$
		.subscribe((doneAndOk: boolean) => {
			if (doneAndOk) {
				this.slices$.next(this.pieDashboardService.generatePieSlices(this.projectService.allProjects));
			}});
	}

}
