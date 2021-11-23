import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { AttachmentFile } from 'src/app/data/AttachmentFile';
import { AuditDetail } from 'src/app/data/audit-detail';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { ProjectAuditService } from '../service/project-audit.service';
import { AuditChosenDetail } from './audit-badge/audit-chosen-detail';
import { TopicEvaluation } from './topic-evaluation';
import { TopicWeight } from './topic-weight';

@Component({
	selector: 'app-project-audit-badges',
	templateUrl: './project-audit-badges.component.html',
	styleUrls: ['./project-audit-badges.component.css']
})
export class ProjectAuditBadgesComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
	 * This messenger propagates the signal to show/hide the audit form panel
	 */
	@Output() messengerShowHideAuditDetail = new EventEmitter<AuditChosenDetail>();

	/**
	 * This messenger propagates the signal than a weight has been given to an audit topic.
	 */
	@Output() messengerTopicWeight = new EventEmitter<TopicWeight>();

	/**
	 * The topics legend obtained from the `referentialService.topics$`.
	 */
	public legendTopics: {[id: number]: string};

	private attachmentList$ = new BehaviorSubject<AttachmentFile[]>([]);

	constructor(
		public projectService: ProjectService,
		public projectAuditService: ProjectAuditService,
		private cinematicService: CinematicService,
		private referentialService: ReferentialService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.referentialService.topics$.subscribe(topics => this.legendTopics = topics));
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
		this.projectService.topicEvaluation$.next(topicEvaluation);
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
	 * This function is called inside a `*ngIf()` conditionning the component `app-files-detail-form` 
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
