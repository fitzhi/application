import { Component, OnInit, Input, OnDestroy, EventEmitter, Output, AfterViewInit } from '@angular/core';
import { Constants } from 'src/app/constants';
import { CinematicService } from 'src/app/service/cinematic.service';
import { BaseComponent } from 'src/app/base/base.component';
import { ProjectService } from 'src/app/service/project.service';
import { MatSliderChange } from '@angular/material/slider';
import { TopicEvaluation } from '../topic-evaluation';
import { TopicWeight } from '../topic-weight';
import { AuditChosenDetail } from './audit-chosen-detail';
import { AuditDetail } from 'src/app/data/audit-detail';

@Component({
	selector: 'app-audit-badge',
	templateUrl: './audit-badge.component.html',
	styleUrls: ['./audit-badge.component.css']
})
export class AuditBadgeComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * Topic identifier.
	 */
	@Input() id: number;

	/**
	 * Evaluation given to this subject.
	 */
	@Input() evaluation: number;

	/**
	 * The weight of this topic in the global evaluation.
	 */
	@Input() weight: number;

	/**
	 * Title of the topic retrieved from the referential.
	 */
	@Input() title;

	/**
	 * This messenger emits a signal to show/hide an audit panel detail.
	 */
	@Output() messengerShowHideAuditDetail = new EventEmitter<AuditChosenDetail>();

	/**
	 * This messenger emits a signal to inform the parent component
	 * that an evaluation has been made on this topic.
	 */
	@Output() messengerEvaluationChange = new EventEmitter<TopicEvaluation>();

	/**
	 * This messenger emits a signal to inform the parent component
	 * that a weight in the global note has been given to this topic.
	 */
	@Output() messengerWeightChange = new EventEmitter<TopicWeight>();

	/**
	 * This `boolean` represents the fact that the panel
	 * in charge of adding or removing tasks and todo for this audit is visible.
	 */
	private auditTasksFormModeIsOn = false;

	/**
	 * This `boolean` represents the fact that the panel
	 * in charge of editing the audit-report is visible.
	 */
	private auditReportFormModeIsOn = false;

	constructor(
		private cinematicService: CinematicService,
		private projectService: ProjectService) { super(); }

	ngOnInit(): void {
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
		const clazz = ((this.cinematicService.idTopicTaskAuditFormSelected === id) && this.auditTasksFormModeIsOn) ? 'tasks-selected' : 'tasks';
		return clazz;
	}

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Return the __CSS class__ to be active for the tasks icon.  
	 * This function is called by the DIV, identified by `'report-{{id}}'`, for its preview.
	 * @param id the topic identifier.
	 */
	/* tslint:enable: no-trailing-whitespace */
	private classIconReport(id: number) {
		const clazz = ((this.cinematicService.idTopicTaskAuditFormSelected === id)
			&& this.auditReportFormModeIsOn) ? 'report-selected' : 'report';
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
	private showHideAuditTasks() {
		console.log ('showHideAuditTasks');
		this.auditTasksFormModeIsOn = !this.auditTasksFormModeIsOn;
		this.messengerShowHideAuditDetail.emit(new AuditChosenDetail(this.id, AuditDetail.Tasks));
	}

	/**
	 * This function emits asignal broadcasting that audit-task form should be visible, or hidden.
	 */
	private showHideAuditReport() {
		console.log ('showHideAuditReport');
		this.auditReportFormModeIsOn = !this.auditReportFormModeIsOn;
		this.messengerShowHideAuditDetail.emit(new AuditChosenDetail(this.id, AuditDetail.Report));
	}

	/**
	 * This function is receiving a signal from `app-audit-graphic-badge` when the end-user has given
	 * a new evaluation of an audit topic.
	 * @param evalution the evaluation.
	 */
	onEvaluationChange(topicEvaluation: TopicEvaluation): void {
		this.drawHeaderColor(topicEvaluation.value);
		this.messengerEvaluationChange.emit(topicEvaluation);
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
	* Content of a field has been updated.
	* @param field field identified throwing this event.
	*/
	public onChange(field: string) {
		if (field === 'weight') {
			// 2 for CHANGE Operation
			this.messengerWeightChange.emit(new TopicEvaluation(this.id, this.weight, 2));
		}
	}

	/**
	* Content of a field has been updated.
	* @param field field identified throwing this event.
	*/
	public onInput(field: string) {
		if (field === 'weight') {
			// 1 for INPUT Operation
			this.messengerWeightChange.emit(new TopicEvaluation(this.id, this.weight, 1));
		}
	}

	/**
	 * The method is invoked when the slider is moved.
	 * @param sliderWeight the event emit by the slider.
	 */
	onSliderInputWeight(sliderWeight: MatSliderChange) {
		this.weight = sliderWeight.value;
			// 1 for INPUT Operation
			this.messengerWeightChange.emit(new TopicEvaluation(this.id, this.weight, 1));
	}

	/**
	 * The method is invoked when the value of the slider has changed.
	 * @param sliderWeight the event emit by the slider.
	 */
	onSliderChangeWeight(sliderWeight: MatSliderChange) {
		this.weight = sliderWeight.value;
		// 2 for CHANGE Operation
		this.messengerWeightChange.emit(new TopicEvaluation(this.id, this.weight, 2));
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}

