import { Component, OnInit, AfterViewInit, Output, EventEmitter, Input, OnDestroy } from '@angular/core';
import { Topic } from './table-categories/topic';
import { BehaviorSubject, Subject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { BaseComponent } from 'src/app/base/base.component';
import { Project } from 'src/app/data/project';
import { ReferentialService } from 'src/app/service/referential.service';
import { TopicProject } from './topic-project';
import { CinematicService } from 'src/app/service/cinematic.service';
import { TopicEvaluation } from './project-audit-badges/topic-evaluation';
import { TopicWeight } from './project-audit-badges/topic-weight';
import { ProjectService } from 'src/app/service/project.service';
import { MessageService } from 'src/app/message/message.service';

@Component({
	selector: 'app-project-audit',
	templateUrl: './project-audit.component.html',
	styleUrls: ['./project-audit.component.css']
})
export class ProjectAuditComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	@Input() project$;

	/**
	 * This `boolean` control the `[hidden]` property of the div `auditTask`.
	 */
	private showDivAuditTask = false;

	/**
	 * The project loaded the input observable.
	 */
	private project: Project;

	/**
	 * Array of topics available in our referential.
	 */
	private topics: {[id: number]: string};

	/**
	 * Array of topics involved in the audit.
	 */
	private auditTopics = [];

	/**
	 * This subject emits the updated values present in the array `auditTopics`
	 * to the component `tableCategories`.
	 */
	private auditTopics$ = new BehaviorSubject<any>([]);

	private topicsHidden = true;

	/**
	 * This `boolean` represents the fact that the panel
	 * in charge of create or update a remark for this audit is visible.
	 *
	 * This `boolean` controls the `[hidden]` property of the div `auditTask`.
	 */
	private auditTaskFormModeIsOn = false;

	/**
	 * This subject is used to notify the `audit-task-form` of the current active topic.
	 */
	public topic$ = new Subject<TopicProject>();

	constructor(
		private referentialService: ReferentialService,
		private projectService: ProjectService,
		private messageService: MessageService,
		private cinematicService: CinematicService) { super(); }

	ngOnInit() {

		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.project = project;
				this.subscriptions.add(
					this.referentialService.topics$.subscribe (topics => {
						this.topics = topics;
						Object.keys(this.project.audit).forEach(key => {
							this.auditTopics.push(
								{	id: Number(key),
									evaluation: (this.project.audit[key].evaluation) ? this.project.audit[key].evaluation : 0,
									title: this.topics[key]} );
						});
						this.auditTopics$.next(this.auditTopics);
					}));
				}));

		this.subscriptions.add(
			this.cinematicService.auditTopicSelected$.subscribe(idTopic => {

				/**
				 * Use case :
				 * Given,
				 * this audit-task form is displayed for a given topic,
				 * and the end-user clicked on another thumbnail body (whithout clicking on the tasks button)
				 * Then we hide the audit-task form.
				 */
				if ( (idTopic !== this.cinematicService.idTopicTaskAuditFormSelected)
					&&  (-1 !== this.cinematicService.idTopicTaskAuditFormSelected)) {
						this.auditTaskFormModeIsOn = false;
						this.cinematicService.idTopicTaskAuditFormSelected = -1;
				}
		}));
	}

	ngAfterViewInit() {
	}

	/**
	 * Setup the categories involved in the manuel audit evaluation.
	 */
	setupCategories() {
		this.topicsHidden = !this.topicsHidden;
	}

	/**
	 * Divide the weights between all topics.
	 */
	private assignWeights(): void {
		// We mitigate the weight on all topics chosen.
		let intermediateSum = 0;
		for (let i = 0; i < this.auditTopics.length - 1; i++) {
			this.auditTopics[i].weight = Math.floor (100 / this.auditTopics.length);
			intermediateSum += this.auditTopics[i].weight;
		}
		this.auditTopics[this.auditTopics.length - 1].weight = 100 - intermediateSum;
	}

	/**
	 * The user has involved, or removed, a topic from his audit.
	 * @param category the given category.
	 */
	onCategoryUpdated(category: Topic) {

		if (Constants.DEBUG) {
			console.log (
				((category.select) ? 'Selection' : 'Deselection)' + ' of %s'), category.title);
		}
		if (category.select) {
			this.auditTopics.push({idTopic: category.id, evaluation: 1, weight: 1, title: category.title});
		} else {
			const index = this.auditTopics.findIndex(item => item.id === category.id);
			if (index === -1) {
				throw new Error ('Internal erreur. This index is supposed to be > 0');
			}
			this.auditTopics.splice(index, 1);
		}
		if (this.auditTopics && this.auditTopics.length > 0) {
			this.assignWeights();
			this.projectService
					.saveAuditTopicWeights$(this.project.id, this.auditTopics)
					.subscribe(doneAndOk => {
						if (doneAndOk) {
							this.messageService.info('Weights are completly saved');
						}
					});
		}

		this.auditTopics$.next(this.auditTopics);
	}

	/**
	 * This function is invoked when `app-project-audit-badges` signals that the end-user tries
	 * to show or hide the tasks audit form.
	 * @param idTopic the topic identifier
	 */
	onShowDivAuditTask(idTopic: number) {

		if ((!this.auditTaskFormModeIsOn) && (idTopic !== this.cinematicService.idTopicTaskAuditFormSelected)) {
			this.topic$.next(new TopicProject(this.project.id, idTopic, this.topics[idTopic]));
			this.auditTaskFormModeIsOn = true;
			this.cinematicService.idTopicTaskAuditFormSelected = idTopic;
		} else {
			if (idTopic === this.cinematicService.idTopicTaskAuditFormSelected) {
				this.auditTaskFormModeIsOn = false;
				this.cinematicService.idTopicTaskAuditFormSelected = -1;
			} else {
				this.topic$.next(new TopicProject(this.project.id, idTopic, this.topics[idTopic]));
				this.cinematicService.idTopicTaskAuditFormSelected = idTopic;
			}
		}
	}

	/**
	 * The function is informed that an evaluation has been given to a topic of the audit.
	 * @param topicEvaluation the topic evaluation emitted
	 */
	onEvaluationChange(topicEvaluation: TopicEvaluation) {
		if ((Constants.DEBUG) && (topicEvaluation.typeOfOperation === Constants.CHANGE_BROADCAST)) {
			console.log (this.topics[topicEvaluation.idTopic], topicEvaluation.value);
		}

		if (topicEvaluation.typeOfOperation === Constants.CHANGE_BROADCAST) {
			this.projectService.saveAuditTopicEvaluation$(
				this.project.id,
				topicEvaluation.idTopic,
				topicEvaluation.value).subscribe(doneAndOk => {
					if (doneAndOk) {
						this.messageService.info('Evaluation given to ' + this.topics[topicEvaluation.idTopic] + ' has been saved');
					}});
		}
	}

	/**
	 * The function is informed that a weight has been given to a topic of the audit.
	 * @param topicEvaluation the topic evaluation emitted
	 */
	onWeightChange(topicWeight: TopicWeight) {
		if ((Constants.DEBUG) && (topicWeight.typeOfOperation === Constants.CHANGE_BROADCAST)) {
			console.log (this.topics[topicWeight.idTopic], topicWeight.value);
		}
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
