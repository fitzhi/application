import { Component, AfterViewInit, Input, ViewEncapsulation, OnInit } from '@angular/core';
import * as D3 from 'd3';
import {Slice} from '../slice';
import { BehaviorSubject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { tap } from 'rxjs/operators';
import { TypeSlice } from '../type-slice';

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

	/**
	 * Array of Arc identifiers.
	 */
	private arcs = ['#arcSonar', '#arcStaff', '#arcAudit'];

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
					// this.generateArcsLegend();
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
			.attr('transform', 'translate(200,200)')
			.attr('fill', slice.color)
			.attr('class', 'slice')
			.attr('d', pathData);

		D3.select('#pieSlice' + slice.id)
			.on('click', function() { this.onSliceClick(slice); }.bind(this))
			.on('mouseover', function() { this.onSliceMouseOver(slice); }.bind(this));
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
		this.inactiveArcs();
		switch (slice.type) {
			case TypeSlice.Sonar:
				this.activeArc('#arcSonar');
				break;
			case TypeSlice.Audit:
				this.activeArc('#arcAudit');
				break;
			case TypeSlice.Staff:
				this.activeArc('#arcStaff');
				break;
		}
	}

	/**
	 * **Inactive** all present arcs in the HTML file.
	 */
	private inactiveArcs(): void {
		this.arcs.forEach(arc => this.inactiveArc(arc));
	}

	/**
	 * **Inactive** the given tooltip arc.
	 * @param idArc HTML tooltip arc identifier
	 */
	private inactiveArc(idArc: string): void {
		this.setupTooltipArc(idArc, false);
	}

	/**
	 * **Active** the given tooltip arc.
	 * @param idArc HTML tooltip arc identifier
	 */
	private activeArc(idArc: string): void {
		this.setupTooltipArc(idArc, true);
	}

	/**
	 * Setup the color of a tooltip arc depending on its status of active, or inactive
	 * @param idArc HTML tooltip arc identifier
	 * @param active the active status
	 */
	private setupTooltipArc(idArc: string, active: boolean) {
		D3.select(idArc)
		.attr('stroke', (active ? 'black' : 'lightGrey'))
		.attr('marker-start', 'url(#arrow' + (active ? 'Active' : '') + ')')
		.attr('marker-end', 'url(#arrow' + (active ? 'Active' : '') + ')');
	}
}
