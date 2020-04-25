import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { StatTypes } from 'src/app/service/dashboard/stat-types';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project.service';
import { BaseComponent } from 'src/app/base/base.component';
import { TreemapService } from '../service/treemap.service';

@Component({
	selector: 'app-treemap',
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
						this.distribution = this.dashboardService.processSkillDistribution(true, 1, StatTypes.FilesSize);
						this.distribution.forEach(data => this.colorScheme.domain.push(data.color));
						if (traceOn()) {
							console.groupCollapsed('Skills distribution');
							this.distribution.forEach(skillData =>
								console.log (skillData.name, skillData.value)
							);
							console.groupEnd();
						}
					}
				}
		}));
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
