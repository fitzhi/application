import { Component, OnInit, Input, OnDestroy, EventEmitter, Output, AfterViewInit } from '@angular/core';
import { Constants } from 'src/app/constants';
import { CinematicService } from 'src/app/service/cinematic.service';
import { BaseComponent } from 'src/app/base/base.component';
import { thresholdFreedmanDiaconis, color, timeHours } from 'd3';
import { ProjectService } from 'src/app/service/project.service';

@Component({
	selector: 'app-audit-badge',
	templateUrl: './audit-badge.component.html',
	styleUrls: ['./audit-badge.component.css']
})
export class AuditBadgeComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * Index of the badge
	 */
	@Input() index;

	@Input() id;

	@Input() title;

	/**
	 * This messenger emits a signal to show/hide the audit form panel
	 */
	@Output() messengerShowDivAuditTask = new EventEmitter<number>();

	/**
	 * Evaluation retrieved from the project.
	 */
	private evaluation = 100;

	/**
	 * This `boolean` represents the fact that the panel
	 * in charge of create or update a remark for this audit is visible.
	 */
	private auditTaskFormModeIsOn = false;

	constructor(
		private cinematicService: CinematicService,
		private projectService: ProjectService) { super(); }

	ngOnInit(): void {
		this.drawHeaderColor(this.evaluation);
	}

	ngAfterViewInit(): void {
		this.drawHeaderColor(this.evaluation);
	}

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Return the __CSS class__ to used for the thumbail.  
	 * This function is called by the container DIV.
	 * @param id the topic identifier.
	 */
	/* tslint:enable: no-trailing-whitespace */
	private classTopic(id: number) {
		const clazz = (this.cinematicService.idTopicSelected === id) ? 'audit-thumbnail-selected' : 'audit-thumbnail';
		return clazz;
	}

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Return the __CSS class__ to be active for the tasks icon.  
	 * This function is called by the DIV, identified by `'tasks-{{id}}'`, for its preview.
	 * @param id the topic identifier.
	 */
	/* tslint:enable: no-trailing-whitespace */
	private classIconTasks(id: number) {
		const clazz = ((this.cinematicService.idTopicTaskAuditFormSelected === id) && this.auditTaskFormModeIsOn) ? 'tasks-selected' : 'tasks';
		return clazz;
	}

	/**
	 * This function emits a signal broadcasting that the end-user has chosen a new topic.
	 * @param id the new current active topic identifier
	 */
	switchTopic(id: number) {
		if (Constants.DEBUG) {
			console.log ('switching to ' + id + ' ' + this.title);
		}
		this.cinematicService.auditTopicSelected$.next(id);
		this.cinematicService.idTopicSelected = id;
	}

	/**
	 * This function emits asignal broadcasting that audit-task form should be visible, or hidden.
	 */
	private showOrHideAuditTask() {
		this.auditTaskFormModeIsOn = !this.auditTaskFormModeIsOn;
		this.messengerShowDivAuditTask.emit(this.id);
	}

	/**
	 * this function is receiving a signal from `app-audit-graphic-badge` when the end-user has given
	 * a new evaluation of an audit topic.
	 * @param evalution the evaluation.
	 */
	onEvaluationChange(evaluation: number): void {
		this.drawHeaderColor(evaluation);
	}

	/**
	 * Draw the header color for the given evaluation.
	 * @param evaluation the given evaluation.
	 */
	drawHeaderColor(evaluation: number): void {
		const colorEvaluation = this.projectService.getEvaluationColor (evaluation);
		document.getElementById('headerRisk-' + this.id).setAttribute('style', 'background-color: ' + colorEvaluation);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}

