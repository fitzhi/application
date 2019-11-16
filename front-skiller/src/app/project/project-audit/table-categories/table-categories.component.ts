import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import {Category} from './category';
import { Constants } from 'src/app/constants';

@Component({
	selector: 'app-table-categories',
	templateUrl: './table-categories.component.html',
	styleUrls: ['./table-categories.component.css']
})
export class TableCategoriesComponent implements OnInit {

	/**
	 * We inform the parent component that a category has been selectect or deselected.
	 */
	@Output() messengerCategoryUpdated = new EventEmitter<Category>();

	private auditCategories = [
		{ id: 0, title: 'General organization' },
		{ id: 1, title: 'Technical Design' },
		{ id: 2, title: 'Build Process' },
		{ id: 3, title: 'General Documentation' },
		{ id: 4, title: 'Testability' },
	];

	public categoryColumns: string[] = ['select', 'title'];

	public dataSource: Category[] = [];

	private colorRow = 'white';

	constructor() { }

	ngOnInit() {
		this.auditCategories.forEach(element => {
			this.dataSource.push (new Category(false, element.id, element.title, 'white'));
		});
	}

	/**
	 * __Selection__ or __Deselection__ of a topic.
	 * @param topic the given category
	 */
	updateCategory(topic: Category) {
		if (Constants.DEBUG) {
			console.log (topic.title, (topic.select) ? 'is selected' : 'is deselected');
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

}
