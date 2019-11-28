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
import { AuditChosenDetail } from './project-audit-badges/audit-badge/audit-chosen-detail';
import { AuditDetail } from 'src/app/data/audit-detail';

@Component({
	selector: 'app-project-audit',
	templateUrl: './project-audit.component.html',
	styleUrls: ['./project-audit.component.css']
})
export class ProjectAuditComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	@Input() project$: BehaviorSubject<Project>;

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
						this.auditTopics = [];
						this.topics = topics;
						Object.keys(this.project.audit).forEach(key => {
							this.auditTopics.push(
								{	idTopic: Number(key),
									weight: (this.project.audit[key].weight) ? this.project.audit[key].weight : 5,
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
	 * Save the new weigths processed, or filled, into the project object container.
	 */
	private impactWeightsInProject(): void {
		this.auditTopics.forEach(auditTopic => {
			if (!this.project.audit[auditTopic.idTopic]) {
				console.error('Internal error : ' + auditTopic.idTopic + ' is not retrieved in the project');
			} else {
				this.project.audit[auditTopic.idTopic].weight = auditTopic.weight;
			}
		});
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
			const index = this.auditTopics.findIndex(item => item.idTopic === category.id);
			if (index === -1) {
				throw new Error ('Internal erreur. This index is supposed to be > 0');
			}
			this.auditTopics.splice(index, 1);
		}
		if (this.auditTopics && this.auditTopics.length > 0) {
			this.assignWeights();
			this.impactWeightsInProject();
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
	 * to show or hide a detail audit form.
	 * @param idTopic the topic identifier
	 */
	onShowHideAuditDetail(auditChosenDetail: AuditChosenDetail) {

		if (auditChosenDetail.detail === AuditDetail.Tasks) {
			const idTopic = auditChosenDetail.idTopic;
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
						this.messageService.success('Evaluation given to ' + this.topics[topicEvaluation.idTopic] + ' has been saved');
						// Affect the new evaluation given for this topic to the associated item in the Project object.
						this.updateEvaluationOnTopicProject(topicEvaluation);

						// Update the underlining GLOBAL project evaluation
						this.projectService.processGlobalAuditEvaluation(this.project);

					}});
		}
	}

	/**
	 * Save the new weigths processed, or filled, into the project object container.
	 */
	private updateEvaluationOnTopicProject(topicEvaluation: TopicEvaluation): void {
		if (!this.project.audit[topicEvaluation.idTopic]) {
			console.error('Internal error : ' + topicEvaluation.idTopic + ' is not retrieved in the project');
			return;
		}
		this.project.audit[topicEvaluation.idTopic].evaluation = topicEvaluation.value;
	}


	/**
	 * The function is informed that a weight has been attributed to a topic of the audit.
	 * @param topicEvaluation the topic evaluation emitted
	 */
	onWeightChange(topicWeight: TopicWeight) {
		if ((Constants.DEBUG) && (topicWeight.typeOfOperation === Constants.CHANGE_BROADCAST)) {
			console.log (this.topics[topicWeight.idTopic], topicWeight.value);
		}
		if (topicWeight.typeOfOperation === Constants.CHANGE_BROADCAST) {
			const auditTopic = this.auditTopics.find (element => element.idTopic === topicWeight.idTopic);
			auditTopic.weight = topicWeight.value;

			// We update the project
			this.project.audit[topicWeight.idTopic].weight = topicWeight.value;
		}
		const sum = this.auditTopics.reduce((prev, curr) => prev + curr.weight, 0);
		if (sum !== 100) {
			this.messageService.warning('Cannot save the weight of ' + topicWeight.value + '%. The sum of weights have to be equal to 100!');
		} else {
			this.projectService
				.saveAuditTopicWeights$(this.project.id, this.auditTopics)
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						this.messageService.success('Audit topic weights for the project ' + this.project.name + ' have been saved!');
				}});
		}
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
