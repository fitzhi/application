import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import {single} from './data';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { StatTypes } from 'src/app/service/dashboard/stat-types';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project.service';
import { BaseComponent } from 'src/app/base/base.component';

@Component({
	selector: 'app-treemap',
	templateUrl: './treemap.component.html',
	styleUrls: ['./treemap.component.css']
})
export class TreemapComponent extends BaseComponent implements OnInit, OnDestroy {


	distribution: any[];

	@Input() view: any[];

	@Input() gradient = false;

	@Input() animations = true;

	colorScheme = {
		domain: ['#5AA454', '#E44D25', '#CFC0BB', '#7aa3e5', '#a8385d', '#aae3f5']
	};

	constructor(
		public dashboardService: DashboardService,
		public projectService: ProjectService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.projectService.allProjectsIsLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.distribution = this.dashboardService.processSkillDistribution(true, 1, StatTypes.FilesSize);
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
