import { Component, AfterViewInit, Input, ViewEncapsulation, OnInit, AfterContentInit, OnDestroy } from '@angular/core';
import * as D3 from 'd3';
import {Slice} from '../slice';
import { BehaviorSubject, of, Observable } from 'rxjs';
import { Constants } from 'src/app/constants';
import { tap, switchMap } from 'rxjs/operators';
import { TypeSlice } from '../type-slice';
import { BaseComponent } from 'src/app/base/base.component';
import { PieDashboardService } from '../service/pie-dashboard.service';

@Component({
	selector: 'app-pie-chart',
	templateUrl: './pie-chart.component.html',
	encapsulation: ViewEncapsulation.Emulated,
	styleUrls: ['./pie-chart.component.css']
})
export class PieChartComponent extends BaseComponent implements OnInit, OnDestroy, AfterViewInit {

	/**
	 * Radius of the Pie.
	 */
	@Input() radius: number;

	/**
	 * Pie number : There might be multiple pies displayed on the dashboard. This number is identifying each one.
	 */
	@Input() pie: number;

	/**
	 * active : One pie might be active for the mouse events.
	 *
	 * __There can only be one active on the same dashboard.__
	 */
	@Input() active: boolean;

	/**
	 * D3 Arc generator.
	 */
	arcGenerator: D3.Arc<any, D3.DefaultArcObject>;

	/**
	 * Array of Arc identifiers.
	 */
	private arcs = ['#arcSonar', '#arcStaff', '#arcAudit'];

	/**
	 * Array of Text identifiers.
	 */
	private texts = ['#textSonar', '#textStaff', '#textAudit'];

	constructor(private pieDashboardService: PieDashboardService) {
		super();
	}

	ngOnInit() {
	}

	ngAfterViewInit() {
		this.arcGenerator = D3.arc().cornerRadius(4).padAngle(.01).padRadius(100);
		this.subscriptions.add(
			this.pieDashboardService.slices$
				.pipe(tap(slices => {
					if (Constants.DEBUG) {
						console.groupCollapsed ('slices received');
						console.log(...slices);
						console.groupEnd();
					}
				})).
				subscribe((slices => {
					setTimeout(() => {
						this.generatePie(...slices);
					}, 0);
				})));
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

		D3.select(this.svgPieSliceID(slice.id))
			.append('path')
			.attr('transform', 'translate(200,200)')
			.attr('fill', slice.color)
			.attr('d', pathData);

			if (this.active) {
				D3.select(this.svgPieSliceID(slice.id))
					.on('click', function() { this.onSliceClick(slice); }.bind(this))
					.on('mouseover', function() { this.onSliceMouseOver(slice); }.bind(this));
			}
	}

	/**
	 * Return the SVG pie slice identifier.
	 */
	private svgPieSliceID(idSlice: number) {
		return '#pieSlice-' + this.pie + '-' + idSlice;
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
		this.inactiveTexts();
		this.pieDashboardService.onSliceMouseOver(slice);
		switch (slice.type) {
			case TypeSlice.Sonar:
				this.activeArc('#arcSonar');
				this.activeText('#textSonar');
				break;
			case TypeSlice.Audit:
				this.activeArc('#arcAudit');
				this.activeText('#textAudit');
				break;
			case TypeSlice.Staff:
				this.activeArc('#arcStaff');
				this.activeText('#textStaff');
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
	 * **Inactive** all present arcs in the HTML file.
	 */
	private inactiveTexts(): void {
		this.texts.forEach(text => this.inactiveText(text));
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
	 * **Inactive** the given tooltip arc.
	 * @param idText HTML tooltip TEXT identifier
	 */
	private inactiveText(idText: string): void {
		this.setupTooltipText(idText, false);
	}

	/**
	 * **Active** the given tooltip arc.
	 * @param idText HTML tooltip TEXT identifier
	 */
	private activeText(idText: string): void {
		this.setupTooltipText(idText, true);
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

	/**
	 * Setup the color of a text depending on its status of active, or inactive
	 * @param idArc HTML tooltip text identifier
	 * @param active the active status
	 */
	private setupTooltipText(idText: string, active: boolean) {
		D3.select(idText)
			.attr('class', (active ? 'text-active' : 'text'));
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
