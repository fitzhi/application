import { Component, OnInit, AfterViewInit, Output, EventEmitter, Input, OnDestroy } from '@angular/core';
import { Topic } from './table-categories/topic';
import { BehaviorSubject, Subject, EMPTY } from 'rxjs';
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
import { AuditDetailsHistory } from 'src/app/service/cinematic/audit-details-history';
import { ConnectUserComponent } from 'src/app/admin/connect-user/connect-user.component';
import { TRANSITION_DURATIONS } from 'ngx-bootstrap/modal/modal-options.class';
import { switchMap } from 'rxjs/operators';

@Component({
	selector: 'app-project-audit',
	templateUrl: './project-audit.component.html',
	styleUrls: ['./project-audit.component.css']
})
export class ProjectAuditComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * This component, hosted in a tab pane, use this emitter to inform its parent to change the active pane.
	 * e.g. if the project form is not complete, application will jump to this tab pane.
	 */
	@Output() tabActivationEmitter = new EventEmitter<number>();

	/**
	 * This `boolean` control the `[hidden]` property of the div `auditTask`.
	 */
	private showDivAuditTask = false;

	/**
	 * Array of topics available in our referential.
	 */
	private topics: {[id: number]: string};

	/**
	 * Array of topics involved in the audit.
	 */
	private auditTopics = [];

	/**
	 * This subject emits the topics selected by the end-user in the component `tableCategories`.
	 * It is sent to the component `app-project-audit-badges` to generate the corresponding audit thumbnail.
	 */
	private auditTopics$ = new BehaviorSubject<any[]>([]);

	/**
	 * Array of `AuditChosenDetail` involved in the audit.
	 */
	private auditDetails: AuditChosenDetail[] = [];

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * This subject emits the details called by the end-user from the audit thumbnail.  
	 * There are 2 kinds of details : __Report__ & __Tasks__ .  
	 * It is sent to the component `app-project-audit-badges` to generate the corresponding details panel.
	 */
	/* tslint:enable: no-trailing-whitespace */
	private auditDetails$ = new BehaviorSubject<AuditChosenDetail[]>([]);

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
			this.projectService.projectLoaded$.pipe(switchMap(
				doneAndOk => (doneAndOk) ? this.referentialService.topics$ : EMPTY
			))
			.subscribe({
				next: topics => {
					// Initialize the Panel details history.
					this.initializePanelDetailsHistory();
					this.topics = topics;
					this.initializeAuditTopic();
				}
			}));
	}

	ngAfterViewInit() {
	}

	/**
	 * Initialize the details panels history when loaded a new project.
	 */
	private initializePanelDetailsHistory(): void {
		this.cinematicService.auditHistory = {};
		Object.keys(this.projectService.project.audit).forEach(key => {
			this.cinematicService.auditHistory[key] = new AuditDetailsHistory();
		});
		if (Constants.DEBUG) {
			this.dumpAuditHistory();
		}
	}

	/**
	 * Initializing the array of topics to be displayed on the webpage.
	 */
	initializeAuditTopic() {
		this.auditTopics = [];
		Object.keys(this.projectService.project.audit).forEach(key => {
			this.auditTopics.push(
				{	idTopic: Number(key),
					weight: (this.projectService.project.audit[key].weight) ? this.projectService.project.audit[key].weight : 5,
					evaluation: (this.projectService.project.audit[key].evaluation) ? this.projectService.project.audit[key].evaluation : 0,
					title: this.topics[key]} );
		});
		this.auditTopics$.next(this.auditTopics);
	}

	public dumpAuditHistory() {
		console.groupCollapsed ('Initial details history');
		Object.keys(this.cinematicService.auditHistory).forEach (
			key => { console.log ('Topic ' + key,
				('Tasks panel is ' + ((this.cinematicService.auditHistory[key].tasksVisible) ? 'visible' : 'hidden')) + ', ' +
				('Report panel is ' + ((this.cinematicService.auditHistory[key].reportVisible) ? 'visible' : 'hidden'))
			);
		});
		console.groupEnd();
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
			if (!this.projectService.project.audit[auditTopic.idTopic]) {
				console.error('Internal error : ' + auditTopic.idTopic + ' is not retrieved in the project');
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

		if (Constants.DEBUG) {
			console.log (
				((category.select) ? 'Selection' : 'Deselection)' + ' of %s'), category.title);
		}
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
		if (this.auditTopics && this.auditTopics.length > 0) {
			this.assignWeights();
			this.impactWeightsInProject();
			this.projectService
					.saveAuditTopicWeights$(this.projectService.project.id, this.auditTopics)
					.subscribe(doneAndOk => {
						if (doneAndOk) {
							this.messageService.info('Weights are completly saved');

							// Update the underlying GLOBAL project evaluation
							this.projectService.processGlobalAuditEvaluation();

							// We inform every panel that the Project object has changed.
							this.projectService.projectLoaded$.next(true);
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

	private removeSecondaryDetailsPanel(idTopic: number, auditChosenDetail: AuditChosenDetail[]) {
		const index = auditChosenDetail.findIndex(detail => (detail.idTopic === idTopic));
		if (index === -1) {
			return;
		}
		auditChosenDetail.splice(index, 1);
		this.removeSecondaryDetailsPanel(idTopic, auditChosenDetail);
	}

	/**
	 * This function is invoked when `app-project-audit-badges` signals that the end-user tries
	 * to show or hide a detail audit form.
	 * @param idTopic the topic identifier
	 */
	onShowHideAuditDetail(auditChosenDetail: AuditChosenDetail) {
		if (Constants.DEBUG) {
			console.log ('adding the detail panel %s for topic %d', auditChosenDetail.detail, auditChosenDetail.idTopic );
		}

		if (this.cinematicService.isPanelDetailSelected(auditChosenDetail.idTopic, auditChosenDetail.detail)) {
			this.auditDetails.push(auditChosenDetail);
			this.auditDetails$.next(this.auditDetails);
		} else {
			const indexForDeletion = this.auditDetails.findIndex(auditDetail => {
				return auditDetail.deepEqual(auditChosenDetail);
			});
			if (indexForDeletion === -1) {
				throw new Error ('WTF : Should not pass here !');
			}
			this.auditDetails.splice(indexForDeletion, 1);
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
					this.projectService.project.id, topicEvaluation.idTopic, topicEvaluation.value)
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						this.messageService.success('Evaluation given to ' + this.topics[topicEvaluation.idTopic] + ' has been saved');

						// Affect the new evaluation given for this topic to the associated item in the Project object.
						this.updateEvaluationOnTopicProject(topicEvaluation);

						// Update the underlining GLOBAL project evaluation
						this.projectService.processGlobalAuditEvaluation();

						// We inform every panel that the Project object has changed.
						this.projectService.projectLoaded$.next(true);
					}});
		}
	}

	/**
	 * Save this updated evaluation into the project object container.
	 */
	private updateEvaluationOnTopicProject(topicEvaluation: TopicEvaluation): void {
		if (!this.projectService.project.audit[topicEvaluation.idTopic]) {
			console.error('Internal error : ' + topicEvaluation.idTopic + ' is not retrieved in the project');
			return;
		}
		this.projectService.project.audit[topicEvaluation.idTopic].evaluation = topicEvaluation.value;
	}

	/**
	 * Save the new weigths processed into the project object container.
	 */
	private updateWeightsOnTopicProject(auditTopics: any[]): void {

		console.groupCollapsed('Local update of the weights for %s', this.projectService.project.name);
		auditTopics.forEach (auditTopic => {
			if (Constants.DEBUG) {
				console.log ('changing wight from %d to %d for %d',
					this.projectService.project.audit[auditTopic.idTopic].weight,
					auditTopic.weight,
					auditTopic.idTopic);
			}
			this.projectService.project.audit[auditTopic.idTopic].weight = auditTopic.weight;
		});
		console.groupEnd();

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
			this.projectService.project.audit[topicWeight.idTopic].weight = topicWeight.value;
		}
		const sum = this.auditTopics.reduce((prev, curr) => prev + curr.weight, 0);
		if (sum !== 100) {
			this.messageService.warning('Cannot save the weight of ' + topicWeight.value + '%. The sum of weights have to be equal to 100!');
		} else {
			this.projectService
				.saveAuditTopicWeights$(this.projectService.project.id, this.auditTopics)
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						this.messageService.success('Audit topic weights for the project ' + this.projectService.project.name + ' have been saved!');

						// Update the underlining GLOBAL project evaluation
						this.projectService.processGlobalAuditEvaluation();
					}
				});
		}
	}

	/**
	 * Change the current active tab.
	 * @param tabIndex index of tab requested.
	 */
	public jumpToTab(tabIndex: number) {
		this.tabActivationEmitter.next(tabIndex);
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
