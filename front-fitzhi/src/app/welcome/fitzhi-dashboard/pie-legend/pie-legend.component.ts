import { Component, OnInit } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { TypeSlice } from '../type-slice';

@Component({
	selector: 'app-pie-legend',
	templateUrl: './pie-legend.component.html',
	styleUrls: ['./pie-legend.component.css']
})
export class PieLegendComponent implements OnInit {

	public typeSlice = TypeSlice;

	constructor(public pieDashboardService: PieDashboardService) { }

	ngOnInit(): void {
	}

}
