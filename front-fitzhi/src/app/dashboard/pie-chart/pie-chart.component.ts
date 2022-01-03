import { AfterViewInit, Component, Input, OnDestroy, ViewEncapsulation } from '@angular/core';
import { Slice, TypeSlice } from 'dynamic-pie-chart';
import { BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { traceOn } from 'src/app/global';
import { PieDashboardService } from '../service/pie-dashboard.service';


@Component({
	selector: 'app-pie-chart',
	templateUrl: './pie-chart.component.html',
	encapsulation: ViewEncapsulation.Emulated,
	styleUrls: ['./pie-chart.component.css']
})
export class PieChartComponent extends BaseDirective implements OnDestroy, AfterViewInit {

	/**
	 * Debug mode for the dynamic pie chart dependency.
	 */
	@Input() debug = false;

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
	 * legend : One pie might have a legend around its pie.
	 */
	@Input() legend: boolean;

	/**
	 * filteredId : slice identifier to be filtered. Only this slice will be drawn.
	 */
	@Input() filteredId = -1;

	/**
     * BehaviorSubject emitting an array of types of slice used to display the legend associated with each type of slice.
     */
	public typeSlices$ = new BehaviorSubject<TypeSlice[]>([]);

	/**
     * BehaviorSubject emitting an array of slice identifiers.
     */
	public filteredIds$ = new BehaviorSubject<number[]>([]);

	constructor(public pieDashboardService: PieDashboardService) {
		super();
		const typeSlices = [];
		typeSlices.push(new TypeSlice(0, 'Sonar'));
		typeSlices.push(new TypeSlice(1, 'Staff'));
		typeSlices.push(new TypeSlice(2, 'Audit'));
		typeSlices.push(new TypeSlice(3, 'None'));
		this.typeSlices$.next(typeSlices);
	}

	ngAfterViewInit() {
		this.subscriptions.add(
			this.pieDashboardService.slices$
				.pipe(tap(slices => {
					if (traceOn()) {
						console.groupCollapsed ('slices received for pie %d', this.pie);
						console.table(slices);
						console.groupEnd();
					}
				})).
				subscribe((slices => {
					if (this.filteredId !== -1) {
						const ids = [];
						ids.push(this.filteredId);
						this.filteredIds$.next(ids);
					} else {
						this.filteredIds$.next([]);
					}
				}))
		);
	}

	/**
	 * Thrown when a slice is **selected**.
	 * @param slice the selected slice
	 */
	public sliceSelection(slice: Slice) {
		if (traceOn()) {
			console.log ('sliceSelection(%d)', slice.id);
		}
	}

	/**
	 * Thrown when a slice is **activated**.
	 * @param slice the selected slice
	 */
	public sliceActivation(slice: Slice) {
		if (traceOn()) {
			console.log ('sliceActivation(%d)', slice.id);
		}
		this.pieDashboardService.onSliceMouseOver(slice);
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
