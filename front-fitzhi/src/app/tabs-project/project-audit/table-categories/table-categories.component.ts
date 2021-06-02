import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { take } from 'rxjs/operators';
import { BaseComponent } from 'src/app/base/base.component';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { ProjectAuditService } from '../service/project-audit.service';
import { Topic } from './topic';

@Component({
	selector: 'app-table-categories',
	templateUrl: './table-categories.component.html',
	styleUrls: ['./table-categories.component.css']
})
export class TableCategoriesComponent extends BaseComponent implements OnInit, OnDestroy {

	public categoryColumns: string[] = ['select', 'title'];

	public dataSource: Topic[] = [];

	/**
	 * Possibe subjects of audit loaded the backend cross the `referentialService`.
	 */
	private auditTopics: {[id: string]: string};

	constructor(
		private projectService: ProjectService,
		private projectAuditService: ProjectAuditService,
		private referentialRervice: ReferentialService,
		private messageService: MessageService) { super(); }

	ngOnInit() {

		this.referentialRervice.topics$.pipe(take(1)).subscribe({
			next: topics => this.auditTopics = topics
		});

		this.subscriptions.add(
			this.projectService.projectLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						Object.keys(this.auditTopics).forEach(key => {
							const b = (this.projectService.project.audit[key]) ? true : false;
							this.dataSource.push (new Topic(b, Number(key), this.auditTopics[key]));
						});
					}
				}
			}));
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
						this.projectAuditService.onCategoryUpdated(topic);
					}});
		} else {
			this.projectService
				.removeAuditTopic$(topic.id)
				.pipe(take(1))
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						delete this.projectService.project.audit[topic.id];
						this.messageService.info(`The topic "${topic.title} is removed from audit`);
						// We inform that a category has been selectect or deselected.
						this.projectAuditService.onCategoryUpdated(topic);
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
	 * Return the class for the row preview depending on the involvement, of not, of this topic inside the audit.
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
