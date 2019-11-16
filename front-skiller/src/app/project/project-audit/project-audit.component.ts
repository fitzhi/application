import { Component, OnInit, AfterViewInit } from '@angular/core';

@Component({
	selector: 'app-project-audit',
	templateUrl: './project-audit.component.html',
	styleUrls: ['./project-audit.component.css']
})
export class ProjectAuditComponent implements OnInit, AfterViewInit {

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
		console.log ('displayCategories', this.displayCategories);
		this.displayCategories = !this.displayCategories;
	}
}
