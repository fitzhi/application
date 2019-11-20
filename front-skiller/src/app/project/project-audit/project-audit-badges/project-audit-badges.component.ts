import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
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
	 * This messenger propagates the signal to show/hide the audit form panel
	 */
	@Output() messengerShowDivAuditTask = new EventEmitter<number>();

	constructor() { }

	ngOnInit() {
	}

	onShowDivAuditTask(idTopic: number) {
		this.messengerShowDivAuditTask.next(idTopic);
	}

}
