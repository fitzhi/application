import { Component, OnInit } from '@angular/core';

@Component({
	selector: 'app-project-audit-badges',
	templateUrl: './project-audit-badges.component.html',
	styleUrls: ['./project-audit-badges.component.css']
})
export class ProjectAuditBadgesComponent implements OnInit {

	private auditCategories = [
		{id: 0, title: 'General organization'},
		{id: 1, title: 'Technical Design'},
		{id: 2, title: 'Build Process'},
		{id: 3, title: 'General Documentation'},
		{id: 4, title: 'Testability'},
	];

	constructor() { }

	ngOnInit() {
	}

}
