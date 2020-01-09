import { Component, Input, OnInit, AfterViewInit, AfterContentInit, ViewEncapsulation } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Slice } from './slice';
import * as d3 from 'd3';
import { PieDashboardService } from './service/pie-dashboard.service';

@Component({
	selector: 'app-pie-dashboard',
	templateUrl: './pie-dashboard.component.html',
	styleUrls: ['./pie-dashboard.component.css']
})
export class PieDashboardComponent implements OnInit {

	/**
	 * Observable emetting the configuration of the pie.
	 */
	private slices$ = new BehaviorSubject<Slice[]>([]);

	constructor(private pieDashboardService: PieDashboardService) {
	}

	ngOnInit() {
		this.slices$.next(this.pieDashboardService.generatePieSlices([]));
	}

}
