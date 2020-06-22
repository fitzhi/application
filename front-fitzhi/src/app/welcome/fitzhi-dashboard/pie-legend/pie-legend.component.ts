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

	/**
	 * Identifier of the activated slice.
	 */
	public activatedId = -1;

	constructor(public pieDashboardService: PieDashboardService) { }

	ngOnInit(): void {
		this.pieDashboardService.sliceActivated$.subscribe({
				next: slice => this.activatedId = slice.id
		});
	}

}
