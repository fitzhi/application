import { Component, OnDestroy, OnInit } from '@angular/core';
import { take } from 'rxjs/operators';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { ProjectAuditService } from '../service/project-audit.service';
import { Topic } from './topic';

@Component({
	selector: 'app-table-categories',
	templateUrl: './table-categories.component.html',
	styleUrls: ['./table-categories.component.css']
})
export class TableCategoriesComponent extends BaseDirective implements OnInit, OnDestroy {

	public categoryColumns: string[] = ['select', 'title'];

	public dataSource: Topic[] = [];

	/**
	 * Possibe subjects of audit loaded the backend cross the `referentialService`.
	 */
	private auditTopics: {[id: string]: string};

	constructor(
		private projectService: ProjectService,
		public projectAuditService: ProjectAuditService,
		private referentialService: ReferentialService,
		private messageService: MessageService) { super(); }

	ngOnInit() {

		this.referentialService.topics$.pipe(take(1)).subscribe({
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
	 * This function is called when the user click on the topic title to reverse the selection status
	 * of the topic (involved, or not).
	 * After the reversal, the `updateTopic` is invoked to complete the treatment.
	 * @param topic the topic which title has been clicked
	 */
	reverseTopicSelection(topic: Topic) {
		topic.select = !topic.select;
		this.projectAuditService.updateTopic(topic);
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
