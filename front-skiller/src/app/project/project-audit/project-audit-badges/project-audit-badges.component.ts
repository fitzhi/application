import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs';
import { TopicEvaluation } from './topic-evaluation';

@Component({
	selector: 'app-project-audit-badges',
	templateUrl: './project-audit-badges.component.html',
	styleUrls: ['./project-audit-badges.component.css']
})
export class ProjectAuditBadgesComponent implements OnInit {

	/**
	 * The Topics involed for this audit.
	 */
	@Input() auditTopics$: Observable<any>;

	/**
	 * This messenger propagates the signal to show/hide the audit form panel
	 */
	@Output() messengerShowDivAuditTask = new EventEmitter<number>();

	/**
	 * This messenger propagates the signal than an evaluation has been given to an audit topic.
	 */
	@Output() messengerTopicEvaluation = new EventEmitter<TopicEvaluation>();

	constructor() { }

	ngOnInit() {
	}

	/**
	 * The function catches a signal emited from `app-audit-badge` and propagates it.
	 * @param idTopic the topic for which the audit form is shown or hidden.
	 */
	onShowDivAuditTask(idTopic: number) {
		this.messengerShowDivAuditTask.next(idTopic);
	}

	/**
	 * The function catches a signal emited from `app-audit-badge`
	 * that an evaluation has been given to a topic
	 * @param topicEvaluation the topic evaluation emitted
	 */
	onEvaluationChange(topicEvaluation: TopicEvaluation) {
		this.messengerTopicEvaluation.next(topicEvaluation);
	}
}
