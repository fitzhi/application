import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EMPTY } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TreemapChartDirective } from 'src/app/base/treemap-chart-directive.directive';
import { Constants } from 'src/app/constants';
import { traceOn } from 'src/app/global';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { StatTypes } from 'src/app/service/dashboard/stat-types';
import { Form } from 'src/app/service/Form';
import { ProjectService } from 'src/app/service/project/project.service';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';
import { TreemapSkillsFilter } from '../treemap-skills-service/treemap-skills-filter';
import { TreemapSkillsService } from '../treemap-skills-service/treemap-skills.service';

@Component({
	selector: 'app-treemap-skills-chart',
	templateUrl: './treemap-skills-chart.component.html',
	styleUrls: ['./treemap-skills-chart.component.css']
})
export class TreemapSkillsChartComponent extends TreemapChartDirective implements OnInit, OnDestroy {


	distribution: any[];

	gradient = false;

	animations = false;

	colorScheme = {
		domain: []
	};

	constructor(
		private tabsStaffListService: TabsStaffListService,
		private router: Router,
		private dashboardService: DashboardService,
		public treeMapService: TreemapSkillsService,
		public cinematicService: CinematicService,
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
								console.log ('Reloading after the detection of a change in the filters');
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
			console.groupCollapsed('Skills distribution ' + this.distribution.length);
			this.distribution.forEach(skillData =>
				console.log (skillData.name, skillData.value)
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
			this.cinematicService.currentActiveFormSubject$.next(new Form(Constants.SKILLS_SEARCH, 'Skill') );
			this.router.navigate(['/searchUser/'], {});
			const skillName = this.distribution.filter(element => (element.name === event.name))[0].name;
			if (traceOn()) {
				console.log('Routing to the selected skill', skillName);
			}
			this.tabsStaffListService.addTabResult('skill:' + skillName, true);
		}
	}

	labelFormatting(tile) {
		return `${tile.data.name}`;
	}

	valueFormatting(value) {
		return `${value}%`;
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
