import { Component, OnInit } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { AnalysisTypeSlice } from '../analysis-type-slice';
import { LevelStaffRisk } from '../level-staff-risk';
import { Slice } from 'dynamic-pie-chart';

@Component({
	selector: 'app-pie-legend',
	templateUrl: './pie-legend.component.html',
	styleUrls: ['./pie-legend.component.css']
})
export class PieLegendComponent implements OnInit {

	/**
	 * The type of slice
	 */
	public typeSlice = AnalysisTypeSlice;

	/**
	 * The level of risk
	 */
	public levelStaffRisk = LevelStaffRisk;

	/**
	 * The slide activated
	 */
	public activatedSlice: Slice;

	/**
	 * Label for the level of risk.
	 */
	public labelLevelOfRisk;

	constructor(public pieDashboardService: PieDashboardService) { }

	ngOnInit(): void {
		this.pieDashboardService.sliceActivated$.subscribe({
				next: slice => {
					this.activatedSlice = slice;
					this.labelLevelOfRisk = this.levelOfRisk();
				}
		});
	}

	/**
	 * Return an explicit level of risk.
	 */
	levelOfRisk() {
		/*
		switch (this.activatedSlice.levelStaffRisk) {
			case LevelStaffRisk.undefined:
				return 'undefined';
			case LevelStaffRisk.high:
				return 'high';
			case LevelStaffRisk.medium:
				return 'medium';
			case LevelStaffRisk.low:
				return 'low';
			}
		*/
	}
}
