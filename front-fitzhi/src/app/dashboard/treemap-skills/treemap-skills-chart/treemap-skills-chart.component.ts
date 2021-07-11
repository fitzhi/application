import { Component, OnDestroy, OnInit } from '@angular/core';
import { EMPTY } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BaseComponent } from 'src/app/base/base.component';
import { traceOn } from 'src/app/global';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { StatTypes } from 'src/app/service/dashboard/stat-types';
import { ProjectService } from 'src/app/service/project/project.service';
import { TreemapSkillsService } from '../service-skills-service/treemap-skills.service';
import { TreemapSkillsFilter } from '../service-skills-service/treemap-skills-filter';

@Component({
	selector: 'app-treemap-skills-chart',
	templateUrl: './treemap-skills-chart.component.html',
	styleUrls: ['./treemap-skills-chart.component.css']
})
export class TreemapSkillsChartComponent extends BaseComponent implements OnInit, OnDestroy {

	distribution: any[];

	view: any[];

	gradient = false;

	animations = true;

	colorScheme = {
		domain: []
	};

	public viewTreeMap = [1000, 500];

	constructor(
		public dashboardService: DashboardService,
		public treeMapService: TreemapSkillsService,
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

	loadDistribution (filter: TreemapSkillsFilter) {
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
