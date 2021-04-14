import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Constants } from '../../../../constants';
import { BaseComponent } from '../../../../base/base.component';
import { ContributorsDataSource } from '../contributors-data-source';
import { traceOn } from 'src/app/global';

@Component({
	selector: 'app-list-contributors',
	templateUrl: './list-contributors.component.html',
	styleUrls: ['./list-contributors.component.css']
})
export class ListContributorsComponent extends BaseComponent implements OnInit, OnDestroy {

	public tblColumns: string[] = ['fullname', 'active', 'external', 'lastCommit'];

	@Input() contributors: ContributorsDataSource;

	constructor() { super(); }

	ngOnInit() {
		if (traceOn()) {
			this.subscriptions.add(
				this.contributors.committers$.subscribe(elements => {
					if (elements) {
						console.groupCollapsed('Contributors');
						elements.forEach(element => console.log  (element.fullname));
						console.groupEnd();
					}
			}));
		}
	}

	/**
	 * Return the CSS class corresponding to the active vs inactive status of a developer.
	 */
	public class_active_inactive(active: boolean) {
		return active ? 'contributor_active' : 'contributor_inactive';
	}


	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
