import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project/project.service';
import { SummaryService } from '../service/summary.service';

@Component({
	selector: 'app-summary',
	templateUrl: './summary.component.html',
	styleUrls: ['./summary.component.css']
})
export class SummaryComponent extends BaseDirective implements OnInit, OnDestroy {

	public id = 1;

	public project = new Project(17891789, 'global Project');

	constructor(
		public summaryService: SummaryService,
		public projectService: ProjectService) {
			super();
		}

	ngOnInit(): void {
		this.subscriptions.add(
			this.projectService.allProjectsIsLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.summaryService.showGeneralAverage();
					}
				}
			})
		);

		this.subscriptions.add(
			this.summaryService.generalAverage$.subscribe({
				next: evaluation => this.project.auditEvaluation = Math.floor(evaluation * 10)
			});
		)
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
