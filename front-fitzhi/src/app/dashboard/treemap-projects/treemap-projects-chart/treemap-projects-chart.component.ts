import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { traceOn } from 'src/app/global';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { TreemapProjectsService } from '../treemap-projects-service/treemap-projects.service';

@Component({
	selector: 'app-treemap-projects-chart',
	templateUrl: './treemap-projects-chart.component.html',
	styleUrls: ['./treemap-projects-chart.component.css']
})
export class TreemapProjectsChartComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * The treemap chart is clickable, or not...
	 */
	 @Input() active = true;

	 distribution: any[];

	view: any[];

	gradient = false;

	animations = true;

	colorScheme = {
		domain: []
	};

	constructor(
		public dashboardService: DashboardService,
		public treeMapService: TreemapProjectsService,
		public projectService: ProjectService) {
		super();
	}

	ngOnInit(): void {
		this.loadDistribution();
	}

	loadDistribution() {
		this.distribution = this.dashboardService.processProjectsDistribution();
		this.colorScheme.domain = [];
		this.distribution.forEach(data => this.colorScheme.domain.push(data.color));
		if (traceOn()) {
			console.groupCollapsed('Projects distribution');
			this.distribution.forEach(skillData =>
				console.log(skillData.name, skillData.value)
			);
			console.groupEnd();
		}
	}

	/**
	 * This method is invoked when the end-user clicks inside the chart.
	 * @param event the event emitted by the component.
	 */
	onSelect(event) {
		if (this.active) {
			if (traceOn()) {
				console.log(event);
			}
		}
	}

	labelFormatting(tile) {
		return `${(tile.label)}`;
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
