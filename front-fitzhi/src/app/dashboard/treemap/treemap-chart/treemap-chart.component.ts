import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { StatTypes } from 'src/app/service/dashboard/stat-types';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project.service';
import { BaseComponent } from 'src/app/base/base.component';
import { TreemapService } from '../service/treemap.service';
import { switchMap } from 'rxjs/operators';
import { EMPTY } from 'rxjs';
import { TreemapFilter } from '../service/treemapFilter';

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
			this.projectService.allProjectsIsLoaded$
				.pipe(switchMap(doneAndOk => doneAndOk ? this.treeMapService.filterUpdated$ : EMPTY))
				.subscribe({
					next: updated => {
						if (updated) {
							if (traceOn()) {
								console.log ('Reloading after the detecion of a change in the filters');
							}
							this.loadDistribution(this.treeMapService.treemapFilter);
						}
					}
				}));

	}

	loadDistribution (filter: TreemapFilter) {
		// Within the tag-star component the evaluation extends from 0 to 4.
		// For a staff member, the level in a skill extends from 1 to 5.
		// We add 1 to the filter
		this.distribution = this.dashboardService.processSkillDistribution(filter.external, filter.level + 1, StatTypes.FilesSize);
		this.colorScheme.domain = [];
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
		if (traceOn()) {
			console.log(event);
		}
	}

	labelFormatting(tile) {
		return `${(tile.label)}`;
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
