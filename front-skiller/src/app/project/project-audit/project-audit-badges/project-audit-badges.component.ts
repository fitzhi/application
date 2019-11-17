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

	constructor() { }

	ngOnInit() {
		this.auditTopics$.subscribe(elements => {
			elements.forEach(element => {
				console.log (element.title);
			});
		});
	}

}
