import { Component, OnInit, Output, EventEmitter, Input, OnDestroy } from '@angular/core';
import { Constants } from 'src/app/constants';
import { ProjectService } from 'src/app/service/project.service';
import { BaseComponent } from 'src/app/base/base.component';
import { Project } from 'src/app/data/project';
import { take } from 'rxjs/operators';
import { MessageService } from 'src/app/message/message.service';
import { Topic } from './topic';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { BehaviorSubject } from 'rxjs';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ReferentialService } from 'src/app/service/referential.service';

@Component({
	selector: 'app-table-categories',
	templateUrl: './table-categories.component.html',
	styleUrls: ['./table-categories.component.css']
})
export class TableCategoriesComponent extends BaseComponent implements OnInit, OnDestroy {

	@Input() project$: BehaviorSubject<Project>;

	/**
	 * We inform the parent component that a category has been selectect or deselected.
	 */
	@Output() messengerCategoryUpdated = new EventEmitter<Topic>();

	/**
	 * Project hosted by yhje observable `prohect$`.
	 */
	private project: Project;

	public categoryColumns: string[] = ['select', 'title'];

	public dataSource: Topic[] = [];

	/**
	 * Possibe subjects of audit loaded the backend cross the `referentialService`.
	 */
	private auditTopics: {[id: string]: string};

	constructor(
		private projectService: ProjectService,
		private referentialRervice: ReferentialService,
		private messageService: MessageService) { super(); }

	ngOnInit() {

		this.subscriptions.add(
			this.referentialRervice.topics$.subscribe(topics =>
			this.auditTopics = topics
		));


		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.project = project;
				Object.keys(this.auditTopics).forEach(key => {
					const b = (project.audit[key]) ? true : false;
					this.dataSource.push (new Topic(b, Number(key), this.auditTopics[key]));
				});
			}));
	}

	/**
	 * __Selection__ or __Deselection__ of a topic.
	 * @param topic the given topic
	 */
	updateTopic(topic: Topic) {
		if (Constants.DEBUG) {
			console.log (topic.title, (topic.select) ? 'is selected' : 'is deselected');
		}
		if (topic.select) {
			this.projectService
				.addAuditTopic(this.project.id, topic.id)
				.pipe(take(1))
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						this.project.audit[topic.id] = new AuditTopic(topic.id, 0, 5);
						this.messageService.info('The topic \'' + topic.title + '\' is added to the audit');
						this.messengerCategoryUpdated.emit(topic);
					}});
		} else {
			this.projectService
				.removeAuditTopic(this.project.id, topic.id)
				.pipe(take(1))
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						delete this.project.audit[topic.id];
						this.messageService.info('The topic \'' + topic.title + '\' is removed from audit');
						this.messengerCategoryUpdated.emit(topic);
					}});
		}
	}

	/**
	 * This function is called when the user click on the topic title to reverse the selection status
	 * of the topic (involved, or not).
	 * After the reversal, the `updateTopic` is invoked to complete the treatment.
	 * @param topic the topic which title has been clicked
	 */
	reverseTopicSelection(topic: Topic) {
		console.log ('reverseTopisSelection');
		topic.select = !topic.select;
		this.updateTopic(topic);
	}

	/**
	 * Return the class for the row preview depending on the involvement, of not, of this toppic inside the audit.
	 * @param select a boolean representing the fact that a topic has been selected
	 */
	class_select_deselect(select: boolean): string {
		return (select) ? 'row-involved' : 'row-ignored';
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
