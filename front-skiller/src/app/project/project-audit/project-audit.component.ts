import { Component, OnInit, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { Category } from './table-categories/category';
import { BehaviorSubject } from 'rxjs';
import { connectableObservableDescriptor } from 'rxjs/internal/observable/ConnectableObservable';

@Component({
	selector: 'app-project-audit',
	templateUrl: './project-audit.component.html',
	styleUrls: ['./project-audit.component.css']
})
export class ProjectAuditComponent implements OnInit, AfterViewInit {

	private auditTopics = [];

	private auditTopics$ = new BehaviorSubject<any>([]);

	displayCategories = true;

	constructor() { }

	ngOnInit() {
	}

	ngAfterViewInit() {
	}

	/**
	 * Setup the categories involved in the manuel audit evaluation.
	 */
	setupCategories() {
		this.displayCategories = !this.displayCategories;
	}

	/**
	 * The user has involved, or removed, a topic from his exam.
	 * @param category the given category.
	 */
	onCategoryUpdated(category: Category) {
		console.log (
			((category.select) ? 'Selection' : 'Deselection)' + ' of %s'),
			category.title);
		if (category.select) {
			this.auditTopics.push({id: category.id, title: category.title});
		} else {
			const index = this.auditTopics.findIndex(item => item.id === category.id);
			if (index === -1) {
				throw new Error ('Internal erreur. This index is supposed to be > 0');
			}
			this.auditTopics.splice(index, 1);
		}
		this.auditTopics$.next(this.auditTopics);
	}
}
