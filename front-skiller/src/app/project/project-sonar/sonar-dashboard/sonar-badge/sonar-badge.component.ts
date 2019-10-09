import { Component, OnInit, Input, ViewEncapsulation } from '@angular/core';

@Component({
	selector: 'app-sonar-badge',
	templateUrl: './sonar-badge.component.html',
	styleUrls: ['./sonar-badge.component.css'],
	encapsulation: ViewEncapsulation.ShadowDom
})
export class SonarBadgeComponent implements OnInit {

	/**
	 * The sanitized content of the badge.
	 */
	@Input() safeBadge;

	constructor() { }

	ngOnInit() {
	}

}
