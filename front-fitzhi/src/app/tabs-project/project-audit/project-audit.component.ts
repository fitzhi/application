import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { EMPTY, Subject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { AuditDetailsHistory } from 'src/app/service/cinematic/audit-details-history';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { AuditChosenDetail } from './project-audit-badges/audit-badge/audit-chosen-detail';
import { TopicEvaluation } from './project-audit-badges/topic-evaluation';
import { ProjectAuditService } from './service/project-audit.service';

@Component({
	selector: 'app-project-audit',
	templateUrl: './project-audit.component.html',
	styleUrls: ['./project-audit.component.css']
})
export class ProjectAuditComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * This `boolean` control the `[hidden]` property of the div `auditTask`.
	 */
	private showDivAuditTask = false;

	topicsHidden = true;

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
	public topic$ = new Subject<AuditTopic>();

	constructor(
		public referentialService: ReferentialService,
		public projectAuditService: ProjectAuditService,
		public projectService: ProjectService,
		public messageService: MessageService,
		public cinematicService: CinematicService) { super(); }

	ngOnInit() {

		this.subscriptions.add(
			this.projectService.projectLoaded$.pipe(switchMap(
				doneAndOk => (doneAndOk) ? this.referentialService.topics$ : EMPTY
			))
			.subscribe({
				next: topics => {
					// Initialize the Panel details history.
					this.initializePanelDetailsHistory();
					this.projectAuditService.initializeAuditTopic(topics);
				}
			})
		);
		this.subscriptions.add(
			this.projectService.topicEvaluation$.subscribe({
				// Update the backend with the new evaluation given to an audit topic.
				next: (te: TopicEvaluation) => this.broadcastEvaluationChange(te)
			})
		);
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
		if (traceOn()) {
			this.dumpAuditHistory();
		}
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
	 * This function is invoked when `app-project-audit-badges` signals that the end-user tries
	 * to show or hide a detail audit form.
	 * @param idTopic the topic identifier
	 */
	onShowHideAuditDetail(auditChosenDetail: AuditChosenDetail) {
		if (traceOn()) {
			console.log ('adding the detail panel %s for topic %d', auditChosenDetail.detail, auditChosenDetail.idTopic );
		}

		if (this.cinematicService.isPanelDetailSelected(auditChosenDetail.idTopic, auditChosenDetail.detail)) {
			this.projectAuditService.auditDetails.push(auditChosenDetail);
			this.projectAuditService.auditDetails$.next(this.projectAuditService.auditDetails);
		} else {
			const indexForDeletion = this.projectAuditService.auditDetails.findIndex(auditDetail => {
				return auditDetail.deepEqual(auditChosenDetail);
			});
			if (indexForDeletion === -1) {
				throw new Error ('WTF : Should not pass here !');
			}
			this.projectAuditService.auditDetails.splice(indexForDeletion, 1);
		}
	}

	/**
	 * The function is informed that an evaluation has changed on a topic in the audit.
	 *
	 * Its main goal is to notify this change to the backend.
	 *
	 * @param topicEvaluation the topic evaluation emitted
	 */
	public broadcastEvaluationChange(topicEvaluation: TopicEvaluation) {

		if ((traceOn()) && (topicEvaluation.typeOfOperation === Constants.CHANGE_BROADCAST)) {
			console.log (this.projectAuditService.topics[topicEvaluation.idTopic], topicEvaluation.value);
		}

		if (topicEvaluation.typeOfOperation === Constants.CHANGE_BROADCAST) {
			this.projectService.saveAuditTopicEvaluation$(
					this.projectService.project.id, topicEvaluation.idTopic, topicEvaluation.value)
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						this.messageService.success(`Evaluation given to ${this.projectAuditService.topics[topicEvaluation.idTopic]} has been saved.`);

						// Affect the new evaluation given for this topic to the associated item in the Project object.
						this.updateEvaluationOnTopicProject(topicEvaluation);

						// Update the underlining GLOBAL project evaluation
						this.projectService.processGlobalAuditEvaluation();
					}
				}
			);
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
			if (traceOn()) {
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
	 * Change the current active tab.
	 * @param tabIndex index of tab requested.
	 */
	public jumpToTab(tabIndex: number) {
		this.cinematicService.projectTabIndex = tabIndex;
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
