import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';

export class AuditBaseComponent extends BaseComponent implements OnDestroy, AfterViewInit {

	public headerText: string;
	public idTopic: number;
	public project$: BehaviorSubject<Project>;
	public projectService: ProjectService;

	constructor() {
		super();
	}

	/**
	 * Post creation initialization
	 * @param headerText the static header defined for this detail
	 * @param idTopic the topic identifier
	 * @param project$ the Project observable
	 * @param projectService the projectService
	 */
	postCreationInit(
			headerText: string,
			idTopic: number,
			project$: BehaviorSubject<Project>,
			projectService: ProjectService): void {
		this.headerText = headerText;
		this.idTopic = idTopic;
		this.project$ = project$;
		this.projectService = projectService;
	}

	ngAfterViewInit(): void {
		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.drawHeaderColor(project.audit[this.idTopic].evaluation);
			})
		);
	}

	/**
	 * Draw the header color for the given evaluation.
	 * @param evaluation the given evaluation.
	 */
	drawHeaderColor(evaluation: number): void {
		const colorEvaluation = this.projectService.getEvaluationColor(evaluation);
		console.log(document.getElementById(this.headerText + this.idTopic));
		document.getElementById(this.headerText + this.idTopic)
			.setAttribute('style', 'background-color: ' + colorEvaluation);
	}

	public ngOnDestroy() {
		this.ngOnDestroy();
	}

}
