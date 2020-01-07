import { Component, Input, OnInit, AfterViewInit, AfterContentInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Slice } from './slice';
import * as d3 from 'd3';

@Component({
	selector: 'app-pie-dashboard',
	templateUrl: './pie-dashboard.component.html',
	styleUrls: ['./pie-dashboard.component.css']
})
export class PieDashboardComponent implements OnInit {

	@Input() slices$: BehaviorSubject<Slice[]>;

	/**
	 * Radius of the first pie.
	 */
	private radius = 200;

	constructor() {
	}

	ngOnInit() {
	}

}
