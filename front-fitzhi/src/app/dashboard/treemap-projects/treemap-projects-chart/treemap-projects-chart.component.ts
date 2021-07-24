import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { traceOn } from 'src/app/global';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { Form } from 'src/app/service/Form';
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
		private dashboardService: DashboardService,
		public treeMapService: TreemapProjectsService,
		public projectService: ProjectService,
		private cinematicService: CinematicService,
		private router: Router) {
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
		// We pass the onSelect as a callback to the treemap component. We receive the callback before this is initialized...
		if ((this) && (this.active)) {
			const idProject = this.distribution.filter(element => (element.name === event.name))[0].id;
			if (traceOn()) {
				console.log('idProject selected', idProject);
			}
			this.cinematicService.currentActiveFormSubject$.next(new Form(Constants.PROJECT_TAB_FORM, 'Project') );
			this.router.navigate(['/project/' + idProject], {});
		}
	}

	labelFormatting(tile) {
		return `<p>${(tile.label)}</p>`;
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
