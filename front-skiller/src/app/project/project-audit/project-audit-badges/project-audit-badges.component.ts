import { Component, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { Observable } from 'rxjs';
import { TopicEvaluation } from './topic-evaluation';
import { TopicWeight } from './topic-weight';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';

@Component({
	selector: 'app-project-audit-badges',
	templateUrl: './project-audit-badges.component.html',
	styleUrls: ['./project-audit-badges.component.css']
})
export class ProjectAuditBadgesComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * This observable emits the current active project.
	 */
	@Input() project$: Observable<Project>;

	/**
	 * The Topics involved for this audit.
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

	/**
	 * This messenger propagates the signal than a weight has been given to an audit topic.
	 */
	@Output() messengerTopicWeight = new EventEmitter<TopicWeight>();

	/**
	 * The project retrieved from the `project$` input observable.
	 */
	private project: Project;

	constructor() { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe(project => this.project = project));
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

	/**
	 * The function catches a signal emited from `app-audit-badge`
	 * that an weight has been given to a topic
	 * @param weightEvaluation the topic weight emitted
	 */
	onWeightChange(topicWeight: TopicWeight) {
		this.messengerTopicWeight.next(topicWeight);
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
