import { Component, AfterViewInit, Input, ViewEncapsulation, OnInit } from '@angular/core';
import * as D3 from 'd3';
import {Slice} from '../slice';
import { BehaviorSubject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { tap } from 'rxjs/operators';

@Component({
	selector: 'app-pie-chart',
	templateUrl: './pie-chart.component.html',
	encapsulation: ViewEncapsulation.None,
	styleUrls: ['./pie-chart.component.css']
})
export class PieChartComponent implements AfterViewInit {

	/**
	 * Radius of the Pie.
	 */
	@Input() radius: number;

	/**
	 * Observable emitting the slices of the pie.
	 */
	@Input() slices$: BehaviorSubject<Slice[]>;

	/**
	 * D3 Arc generator.
	 */
	arcGenerator: D3.Arc<any, D3.DefaultArcObject>;

	constructor() {}

	ngAfterViewInit() {
		this.arcGenerator = D3.arc().cornerRadius(4).padAngle(.01).padRadius(100);
		if (Constants.DEBUG) {
			this.slices$
				.pipe(tap(slices => {
					if (Constants.DEBUG) {
						console.groupCollapsed ('slices received');
						console.log(...slices);
						console.groupEnd();
					}
				})).subscribe (slices => {
					this.generatePie(...slices);
				});
		}
	}

	/**
	 * This function generates the Techxhì(TM) summary pie (C).
	 * @param slice the given slice with its parameters *(such as angle, color...)*
	 */
	private generatePie(...slices: Slice[]) {
		slices.forEach(slice => {
			this.generatePieSlice (slice);
		});
	}

	/**
	 * This function generates the SVG arc figuring a slice.
	 * @param slice the given slice with its parameters *(such as angle, color...)*
	 */
	private generatePieSlice (slice: Slice): void {

		const pathData = this.arcGenerator({
			startAngle: (slice.offset * 2 * Math.PI) / 360,
			endAngle: ((slice.offset + slice.angle) * 2 * Math.PI) / 360,
			innerRadius: 5,
			outerRadius: this.radius
		});

		D3.select('#pieSlice' + slice.id)
			.append('path')
			.attr('transform', 'translate(' + this.radius + ',' + this.radius + ')')
			.attr('fill', slice.color)
			.attr('class', 'slice')
			.attr('d', pathData);

			D3.select('#pieSlice' + slice.id)
				.on('click', function() { this.onSliceClick(slice); }.bind(this))
				.on('onmouseover', function() { this.onSliceMouseOver(slice.id); }.bind(this));
	}

	/**
	 * This function is invoked when the end-user selects a slice.
	 * @param idSlice Slice identifier
	 */
	onSliceClick(slice: Slice): void {
		if (Constants.DEBUG) {
			console.log ('onSliceClick(%d)', slice.id);
		}
	}

	/**
	 * This function is invoked when the mouse pointer is located up on the given slice.
	 * @param slice the slice highlighted by the end-user mouse.
	 */
	onSliceMouseOver(slice: Slice): void {
	}
}
