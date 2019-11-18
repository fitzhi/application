import { Component, OnInit, Output, EventEmitter, Input, OnDestroy } from '@angular/core';
import { Constants } from 'src/app/constants';
import { ProjectService } from 'src/app/service/project.service';
import { BaseComponent } from 'src/app/base/base.component';
import { Project } from 'src/app/data/project';
import { take } from 'rxjs/operators';
import { MessageService } from 'src/app/message/message.service';
import { Topic } from './topic';

@Component({
	selector: 'app-table-categories',
	templateUrl: './table-categories.component.html',
	styleUrls: ['./table-categories.component.css']
})
export class TableCategoriesComponent extends BaseComponent implements OnInit, OnDestroy {

	@Input() project$;

	/**
	 * We inform the parent component that a category has been selectect or deselected.
	 */
	@Output() messengerCategoryUpdated = new EventEmitter<Topic>();

	private project: Project;

	private auditCategories = [
		{ id: 0, title: 'General organization' },
		{ id: 1, title: 'Technical Design' },
		{ id: 2, title: 'Build Process' },
		{ id: 3, title: 'General Documentation' },
		{ id: 4, title: 'Testability' },
	];

	public categoryColumns: string[] = ['select', 'title'];

	public dataSource: Topic[] = [];

	private colorRow = 'white';

	constructor(
		private projectService: ProjectService,
		private messageService: MessageService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.project = project;
				this.auditCategories.forEach(element => {
					this.dataSource.push (new Topic(
						(project.audit[element.id]), element.id, element.title));
				});
			}));
	}

	/**
	 * __Selection__ or __Deselection__ of a topic.
	 * @param topic the given category
	 */
	updateCategory(topic: Topic) {
		if (Constants.DEBUG) {
			console.log (topic.title, (topic.select) ? 'is selected' : 'is deselected');
		}
		if (topic.select) {
			this.projectService
				.addAuditTopic(this.project.id, topic.id)
				.pipe(take(1))
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						this.messageService.info('Topic ' + topic.title + ' is added to the audit');
				}});
		} else {
			this.projectService
				.removeAuditTopic(this.project.id, topic.id)
				.pipe(take(1))
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						this.messageService.info('Topic ' + topic.title + ' is removed from audit');
				}});
	}
		this.messengerCategoryUpdated.emit(topic);
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
