import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MatSliderChange } from '@angular/material/slider';
import { take } from 'rxjs/operators';
import { BaseComponent } from 'src/app/base/base.component';
import { AuditDetail } from 'src/app/data/audit-detail';
import { traceOn } from 'src/app/global';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { TopicEvaluation } from '../topic-evaluation';
import { TopicWeight } from '../topic-weight';
import { AuditChosenDetail } from './audit-chosen-detail';

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
	 * that a weight in the global note has been given to this topic.
	 */
	@Output() messengerWeightChange = new EventEmitter<TopicWeight>();

	constructor(
		private cinematicService: CinematicService,
		private referentialService: ReferentialService,
		public projectService: ProjectService) { super(); }

	ngOnInit(): void {
	}

	ngAfterViewInit(): void {
		// 1) The referential data has to be loaded (because we'll use the risks color retrieved from the back-end during the drawing)
		this.referentialService.referentialLoaded$
			.pipe(take(1))
			.subscribe ({
				next: doneAndOk => {
					if (doneAndOk) {
						this.drawHeaderColor(this.evaluation);
					}
				}
			}
		);

	

	}

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Return the __CSS class__ to used for the thumbail.  
	 * This function is called by the container DIV.
	 * @param id the topic identifier.
	 */
	/* tslint:enable: no-trailing-whitespace */
	public classTopic(id: number) {
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
	public classIconTasks(id: number) {
		const clazz = (this.cinematicService.isPanelDetailSelected(this.id, AuditDetail.Tasks)) ? 'tasks-selected' : 'tasks';
		return clazz;
	}

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Return the __CSS class__ to be active for the tasks icon.  
	 * This function is called by the DIV, identified by `'report-{{id}}'`, for its preview.
	 * @param id the topic identifier.
	 */
	/* tslint:enable: no-trailing-whitespace */
	public classIconReport(id: number) {
		const clazz = (this.cinematicService.isPanelDetailSelected(this.id, AuditDetail.Report)) ? 'report-selected' : 'report';
		return clazz;
	}

	/**
	 * This function emits a signal broadcasting that the end-user has chosen a new topic.
	 * @param id the new current active topic identifier
	 */
	switchTopic(id: number) {
		if (traceOn()) {
			console.log (`switching to ${id} ${this.title}`);
		}
		this.cinematicService.auditTopicSelectedSubject$.next(id);
		this.cinematicService.idTopicSelected = id;
	}

	/**
	 * This function broadcasts a signal that audit-task form should be visible, or hidden.
	 */
	public showHideAuditTasks() {
		this.cinematicService.auditHistory[this.id].tasksVisible = !this.cinematicService.auditHistory[this.id].tasksVisible;
		this.messengerShowHideAuditDetail.emit(new AuditChosenDetail(this.id, AuditDetail.Tasks));
	}

	/**
	 * This function broadcasts a signal that audit-report form should be visible, or hidden.
	 */
	public showHideAuditReport() {
		this.cinematicService.auditHistory[this.id].reportVisible = !this.cinematicService.auditHistory[this.id].reportVisible;
		this.messengerShowHideAuditDetail.emit(new AuditChosenDetail(this.id, AuditDetail.Report));
	}

	/**
	 * This function is receiving a signal from `app-audit-graphic-badge` when the end-user has given a new evaluation of an audit topic.
	 * @param evalution the evaluation.
	 */
	onEvaluationChange(topicEvaluation: TopicEvaluation): void {
		this.drawHeaderColor(topicEvaluation.value);
		topicEvaluation.typeOfOperation = 2;
		this.projectService.topicEvaluation$.next(topicEvaluation);
	}

	/**
	 * Draw the header color for the given evaluation.
	 * @param evaluation the given evaluation.
	 */
	drawHeaderColor(evaluation: number): void {
		const colorEvaluation = this.projectService.getEvaluationColor (evaluation);
		// We colorize the header after the UI event loop to avoid a transparent header, 'for an unknwon reason' (shame on me)
		setTimeout(() => {
			document.getElementById('headerRisk-' + this.id)
				.setAttribute('style', 'background-color: ' + colorEvaluation);
		}, 0);
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

