import { Component, OnInit, Input } from '@angular/core';

@Component({
	selector: 'app-audit-badge',
	templateUrl: './audit-badge.component.html',
	styleUrls: ['./audit-badge.component.css']
})
export class AuditBadgeComponent implements OnInit {

	/**
	 * Index of the badge
	 */
	@Input() index;

	@Input() id;

	@Input() title;

	constructor() { }

	ngOnInit() {
	}

}
