import { OnDestroy, AfterViewInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';

export class AuditBaseComponent extends BaseComponent implements OnDestroy, AfterViewInit {

	/**
	 * Topic identifier.
	 */
	public idTopic: number;

	/**
	 * Font awesome.
	 */
	public fontAwesome = '';

	constructor(public headerText: string, public projectService: ProjectService) {
		super();
		if (this.headerText === 'header-tasks-') {
			this.fontAwesome = 'fas fa-tasks';
		}
		if (this.headerText === 'header-report-') {
			this.fontAwesome = 'far fa-file-alt';
		}
	}

	/**
	 * Set the topic identifier
	 * @param idTopic the topic identifier
	 */
	setIdTopic(idTopic: number): void {
		this.idTopic = idTopic;
	}

	ngAfterViewInit(): void {
		this.drawHeaderColor(this.projectService.project.audit[this.idTopic].evaluation);
	}

	/**
	 * Draw the header color for the given evaluation.
	 * @param evaluation the given evaluation.
	 */
	drawHeaderColor(evaluation: number): void {
		const colorEvaluation = this.projectService.getEvaluationColor(evaluation);
		document.getElementById(this.headerText + this.idTopic)
			.setAttribute('style', 'background-color: ' + colorEvaluation);
	}

	public ngOnDestroy() {
		super.ngOnDestroy();
	}

}
