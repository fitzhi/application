import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { take } from 'rxjs/operators';
import { Constants } from 'src/app/constants';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { AuditDetailsHistory } from 'src/app/service/cinematic/audit-details-history';
import { ProjectService } from 'src/app/service/project/project.service';
import { AuditChosenDetail } from '../project-audit-badges/audit-badge/audit-chosen-detail';
import { TopicWeight } from '../project-audit-badges/topic-weight';
import { Topic } from '../table-categories/topic';

/**
 * This service is mainly used by the component project-audit.
 */
@Injectable({
	providedIn: 'root'
})
export class ProjectAuditService {

	/**
	 * Array of topics available in our referential.
	 */
	topics: {[id: number]: string};

	/**
	 * Array of topics involved in the audit.
	 */
	auditTopics = [];

	/**
	 * This subject emits the topics selected by the end-user in the component `tableCategories`.
	 * It is sent to the component `app-project-audit-badges` to generate the corresponding audit thumbnail.
	 */
	auditTopics$ = new BehaviorSubject<any[]>([]);

	/**
	 * Array of `AuditChosenDetail` involved in the audit.
	 */
	auditDetails: AuditChosenDetail[] = [];

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * This subject emits the details called by the end-user from the audit thumbnail.  
	 * There are 2 kinds of details : __Report__ & __Tasks__ .  
	 * It is sent to the component `app-project-audit-badges` to generate the corresponding details panel.
	 */
	/* tslint:enable: no-trailing-whitespace */
	auditDetails$ = new BehaviorSubject<AuditChosenDetail[]>([]);

	constructor(
		private projectService: ProjectService,
		private cinematicService: CinematicService,
		private messageService: MessageService) { }

	/**
	 * Initializing the array of topics to be displayed on the webpage.
	 *
	 * @param topics container of topics available in our referential. These topics are loaded from the referential
	 */
	public initializeAuditTopic(topics: {[id: number]: string}) {

		// We keep the referential.
		this.topics = topics;
		// We initialize the array.
		this.auditTopics = [];
		Object.keys(this.projectService.project.audit).forEach(key => {
			this.auditTopics.push(
				{	idProject: this.projectService.project.id,
					idTopic: Number(key),
					weight: (this.projectService.project.audit[key].weight) ? this.projectService.project.audit[key].weight : 5,
					evaluation: (this.projectService.project.audit[key].evaluation) ? this.projectService.project.audit[key].evaluation : 0,
					title: this.topics[key]} );
		});
		this.auditTopics$.next(this.auditTopics);
	}

	/**
	 * Divide the weights between all topics.
	 */
	assignWeights(): void {
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
	impactWeightsInProject(): void {
		this.auditTopics.forEach(auditTopic => {
			if (!this.projectService.project.audit[auditTopic.idTopic]) {
				console.error(`Internal error : {auditTopic.idTopic} is not retrieved in the project.`);
			} else {
				this.projectService.project.audit[auditTopic.idTopic].weight = auditTopic.weight;
			}
		});
	}

	/**
	 * The user has involved, or removed, a topic from his audit.
	 * @param category the given category.
	 */
	onCategoryUpdated(category: Topic) {

		if (traceOn()) {
			console.log (
				((category.select) ? 'Selection' : 'Deselection)' + ' of %s'), category.title);
		}

		// We add or remove an auditTopic in the active array
		if (category.select) {
			this.cinematicService.auditHistory[category.id] = new AuditDetailsHistory();
			this.auditTopics.push({idTopic: category.id, evaluation: 1, weight: 1, title: category.title});
		} else {
			const index = this.auditTopics.findIndex(item => item.idTopic === category.id);
			if (index === -1) {
				throw new Error ('Internal erreur. This index is supposed to be > 0');
			}
			this.auditTopics.splice(index, 1);
			this.cinematicService.auditHistory[category.id] = null;
			this.removeSecondaryDetailsPanel(category.id, this.auditDetails);
		}

		// We take in account the operation in the loaded project and in the backend.
		if (this.auditTopics && this.auditTopics.length > 0) {
			this.assignWeights();
			this.impactWeightsInProject();
			this.projectService
				.saveAuditTopicWeights$(this.projectService.project.id, this.auditTopics)
				.pipe(take(1))
				.subscribe({
					next: doneAndOk => {
						if (doneAndOk) {
							this.messageService.info('Weights are completly saved.');

							// Update the underlying GLOBAL project evaluation
							this.projectService.processGlobalAuditEvaluation();

							// We inform every panel that the Project object has changed.
							this.projectService.projectLoaded$.next(true);
						}
					}
				});
		} else {

			// Update the underlying GLOBAL project evaluation
			this.projectService.processGlobalAuditEvaluation();

			// We inform every panel that the Project object has changed.
			this.projectService.projectLoaded$.next(true);

		}

		this.auditTopics$.next(this.auditTopics);
	}

	/**
	 * Remove a panel.
	 * @param idTopic the Topic identifier
	 * @param auditChosenDetail the audit detail panel to be removed
	 */
	removeSecondaryDetailsPanel(idTopic: number, auditChosenDetail: AuditChosenDetail[]): void {
		const index = auditChosenDetail.findIndex(detail => (detail.idTopic === idTopic));
		if (index === -1) {
			return;
		}
		auditChosenDetail.splice(index, 1);
		// The secondary panels to upload file / edit a summary, has to be removed
		// if they were activite and visible on the screen.
		this.removeSecondaryDetailsPanel(idTopic, auditChosenDetail);
	}


	/**
	 * The function is informed that a weight has been attributed to a topic of the audit.
	 *
	 * @param topicWeight the topic weight emitted by the UI
	 */
	onWeightChange(topicWeight: TopicWeight) {

		if ((traceOn()) && (topicWeight.typeOfOperation === Constants.CHANGE_BROADCAST)) {
			console.log (this.topics[topicWeight.idTopic], topicWeight.value);
		}

		if (topicWeight.typeOfOperation === Constants.CHANGE_BROADCAST) {
			const auditTopic = this.auditTopics.find (element => element.idTopic === topicWeight.idTopic);
			auditTopic.weight = topicWeight.value;

			// We update the project
			this.projectService.project.audit[topicWeight.idTopic].weight = topicWeight.value;
		}

		const sum = this.auditTopics.reduce((prev, curr) => prev + curr.weight, 0);
		if (sum !== 100) {
			this.messageService.warning('Cannot save the weight of ' + topicWeight.value + '%. The sum of weights have to be equal to 100!');
		} else {
			this.projectService
				.saveAuditTopicWeights$(this.projectService.project.id, this.auditTopics)
				.pipe(take(1))
				.subscribe({
					next: doneAndOk => {
						if (doneAndOk) {
							this.messageService.success(
								`Audit topic weights for the project ${this.projectService.project.name} have been saved!`);

							// Update the underlining GLOBAL project evaluation
							this.projectService.processGlobalAuditEvaluation();
						}
					}
				});
		}
	}

	/**
	 * __Selection__ or __Deselection__ of a topic in the audit scope.
	 *
	 * @param topic the given topic
	 */
	updateTopic(topic: Topic) {
		if (traceOn()) {
			console.log (topic.title, (topic.select) ? 'is selected' : 'is deselected');
		}
		if (topic.select) {
			this.projectService
				.addAuditTopic$(topic.id)
				.pipe(take(1))
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						this.projectService.project.audit[topic.id] = new AuditTopic(topic.id, 0, 5);
						this.messageService.info(`The topic "${topic.title}" is added to the scope of audit`);
						// We inform that a category has been selectect or deselected.
						this.onCategoryUpdated(topic);
					}}
				);
		} else {
			this.projectService
				.removeAuditTopic$(topic.id)
				.pipe(take(1))
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						delete this.projectService.project.audit[topic.id];
						this.messageService.info(`The topic "${topic.title} is removed from audit`);
						// We inform that a category has been selectect or deselected.
						this.onCategoryUpdated(topic);
					}}
				);
		}
	}

}
