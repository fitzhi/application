import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { StatTypes } from 'src/app/service/dashboard/stat-types';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project.service';
import { BaseComponent } from 'src/app/base/base.component';
import { TreemapService } from '../service/treemap.service';
import { switchMap } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

@Component({
	selector: 'app-treemap-chart',
	templateUrl: './treemap-chart.component.html',
	styleUrls: ['./treemap-chart.component.css']
})
export class TreemapChartComponent extends BaseComponent implements OnInit, OnDestroy {

	distribution: any[];

	view: any[];

	gradient = false;

	animations = true;

	colorScheme = {
		domain: []
	};

	constructor(
		public dashboardService: DashboardService,
		public treeMapService: TreemapService,
		public projectService: ProjectService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.projectService.allProjectsIsLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
					}
				}
		}));

		this.subscriptions.add(
			this.projectService.allProjectsIsLoaded$
				.pipe(switchMap(doneAndOk => doneAndOk ? this.treeMapService.filter$ : EMPTY))
				.subscribe({
					next: filter => {
						if (traceOn()) {
							console.log ('Reload after the change of the external filter');
						}
						this.loadDistribution(filter.external, filter.level);
					}
				}));

	}

	loadDistribution (external: boolean, minimumLevel: number) {
		this.distribution = this.dashboardService.processSkillDistribution(external, minimumLevel, StatTypes.FilesSize);
		this.distribution.forEach(data => this.colorScheme.domain.push(data.color));
		if (traceOn()) {
			console.groupCollapsed('Skills distribution');
			this.distribution.forEach(skillData =>
				console.log (skillData.name, skillData.value)
			);
			console.groupEnd();
		}
	}

	onSelect(event) {
		console.log(event);
	}

	labelFormatting(c) {
		return `${(c.label)}`;
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
