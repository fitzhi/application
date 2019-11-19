import { Component, OnInit, Input } from '@angular/core';
import { Observable } from 'rxjs';

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
	 * This `boolean` control the `[hidden]` property of the div `auditTask`.
	 */
	private hideDivAuditTask = true;

	constructor() { }

	addAuditTask() {
		this.hideDivAuditTask = !this.hideDivAuditTask;
	}

	onShowDivAuditTask(idTopic: number) {
		this.hideDivAuditTask = !this.hideDivAuditTask;
	}

	ngOnInit() {
		this.auditTopics$.subscribe(elements => {
			elements.forEach(element => {
				console.log (element.title);
			});
		});
	}

}
