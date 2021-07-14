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

	constructor() { }

	ngOnInit(): void {
	}

}
