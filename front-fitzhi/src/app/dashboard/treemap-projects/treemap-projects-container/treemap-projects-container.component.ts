import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
	selector: 'app-treemap-projects',
	templateUrl: './treemap-projects-container.component.html',
	styleUrls: ['./treemap-projects-container.component.css']
})
export class TreemapProjectsContainerComponent implements OnInit {

	/**
	 * Width of the chart.
	 */
	@HostBinding('style.--treemap-projects-width')
	@Input() width = '500px';

	/**
	 * Height of the chart.
	 */
	@HostBinding('style.--treemap-projects-height')
	@Input() height = '200px';

	/**
	 * Will this treemap be used as a button inside the navbar, or as a chart inside the dashboard container ?
	 *
	 * This property is concatenated with the class name "treemap-projects-" to specify which classname to be used :
	 * either **treemap-projects-button**, or **treemap-projects-chart**.
	 * **Default is chart.**
	 */
	@Input() buttonOrChart = 'chart';

	/**
	 * The treemap chart is clickable, or not...
	 */
	@Input() active = true;

	constructor() { }

	ngOnInit(): void {
	}

}
