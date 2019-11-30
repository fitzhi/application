import { Component, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { TopicEvaluation } from './topic-evaluation';
import { TopicWeight } from './topic-weight';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { AuditChosenDetail } from './audit-badge/audit-chosen-detail';
import { AuditDetail } from 'src/app/data/audit-detail';
import { ReferentialService } from 'src/app/service/referential.service';

@Component({
	selector: 'app-project-audit-badges',
	templateUrl: './project-audit-badges.component.html',
	styleUrls: ['./project-audit-badges.component.css']
})
export class ProjectAuditBadgesComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * This observable emits the current active project.
	 */
	@Input() project$: BehaviorSubject<Project>;

	/**
	 * The Topics involved for this audit.
	 *
	 * The HTML `project-audit-badges` file iterates on the topics array emitted by this observable,
	 * and inserts an `app-audit-badge` component for each record.
	 */
	@Input() auditTopics$: BehaviorSubject<any[]>;

	/**
	 * The audit details panel displayed on the Audit container.
	 *
	 * The HTML `project-audit-badges` file iterates on the Audit details array emitted by this observable,
	 * and inserts an `app-report-detail-form` component for each record.
	 */
	@Input() auditDetails$: BehaviorSubject<AuditChosenDetail[]>;

	/**
	 * This messenger propagates the signal to show/hide the audit form panel
	 */
	@Output() messengerShowHideAuditDetail = new EventEmitter<AuditChosenDetail>();

	/**
	 * This messenger propagates the signal than an evaluation has been given to an audit topic.
	 */
	@Output() messengerTopicEvaluation = new EventEmitter<TopicEvaluation>();

	/**
	 * This messenger propagates the signal than a weight has been given to an audit topic.
	 */
	@Output() messengerTopicWeight = new EventEmitter<TopicWeight>();

	/**
	 * The project retrieved from the `project$` input observable.
	 */
	private project: Project;

	/**
	 * The topics legend obtained from tge `referentialService.topics$`.
	 */
	private legendTopics: {[id: number]: string};

	constructor(private referentialService: ReferentialService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.referentialService.topics$.subscribe(topics => this.legendTopics = topics));

		this.subscriptions.add(
			this.project$.subscribe(project => this.project = project));
	}

	/**
	 * The function is catching a signal emitted from `app-audit-badge` and propagates it.
	 * @param auditDetail the audit topic detail panel to be shown or hidden.
	 */
	onShowHideAuditDetail(auditDetail: AuditChosenDetail) {
		this.messengerShowHideAuditDetail.next(auditDetail);
	}

	/**
	 * The function catches a signal emited from `app-audit-badge`
	 * that an evaluation has been given to a topic
	 * @param topicEvaluation the topic evaluation emitted
	 */
	onEvaluationChange(topicEvaluation: TopicEvaluation) {
		this.messengerTopicEvaluation.next(topicEvaluation);
	}

	/**
	 * The function catches a signal emited from `app-audit-badge`
	 * that an weight has been given to a topic
	 * @param weightEvaluation the topic weight emitted
	 */
	onWeightChange(topicWeight: TopicWeight) {
		this.messengerTopicWeight.next(topicWeight);
	}

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Return `true` if this choice is a __report__ detail choice.  
	 * This function is called inside a `*ngIf()` conditionning the component `app-report-detail-form` 
	 * within the HTML file for `ProjectAuditBadges`
	 * @param auditChosenDetail the end-user choice
	 */
	/* tslint:enable: no-trailing-whitespace */
	reportDetailChosen(auditChosenDetail: AuditChosenDetail): boolean {
		return (auditChosenDetail.detail === AuditDetail.Report);
	}

	/* tslint:disable: no-trailing-whitespace */
	/**
	 * Return `true` if this choice is a __taks__ detail choice.  
	 * This function is called inside a `*ngIf()` conditionning the component `app-tasks-detail-form` 
	 * within the HTML file for `ProjectAuditBadges`
	 * @param auditChosenDetail the end-user choice
	 */
	/* tslint:enable: no-trailing-whitespace */
	tasksDetailChosen(auditChosenDetail: AuditChosenDetail): boolean {
		return (auditChosenDetail.detail === AuditDetail.Tasks);
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
