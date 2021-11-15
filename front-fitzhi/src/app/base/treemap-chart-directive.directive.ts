import { Directive, Input, OnDestroy } from '@angular/core';
import { BaseDirective } from './base-directive.directive';

@Directive()
export class TreemapChartDirective extends BaseDirective implements OnDestroy {

	/**
	 * Will this treemap be used as a button inside the navbar, or as a chart inside the dashboard container ?
	 *
	 * If this chart is hosted on a button, neither label, nor value has to be writen on the chart.
	 */
	 @Input() buttonOrChart = 'chart';

	/**
	 * The treemap chart is clickable, or not...
	 */
	 @Input() active = true;

	 constructor() { super(); }

	 /**
	 * @returns **true** if the chart is a chart.
	 */
	public isChart() {
		return (this.buttonOrChart === 'chart');
	}

	/**
	 * @returns **true** if the chart is a button.
	 */
	public isButton() {
		return (this.buttonOrChart === 'button');
	}

	/**
	 * No label for the button chart.
	 * @param tile the active tile
	 * @returns an empty string.
	 */
	public noLabel(tile) {
		return '';
	}

	/**
	 * No value for the button chart.
	 * @param tile the active tile
	 * @returnsan empty string.
	 */
	public noValue(value) {
		return '';
	}


	public ngOnDestroy() {
		super.ngOnDestroy();
	}

}
