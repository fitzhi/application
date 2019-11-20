import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Topic } from '../table-categories/topic';
import { BaseComponent } from 'src/app/base/base.component';
import { Subject } from 'rxjs';
import { Constants } from 'src/app/constants';

@Component({
	selector: 'app-audit-task-form',
	templateUrl: './audit-task-form.component.html',
	styleUrls: ['./audit-task-form.component.css']
})
export class AuditTaskFormComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Observable emitting the current active project/topic.
	 */
	@Input() topic$: Subject<Topic>;

	/**
	 * Current active topic received from the observable `topic$`.
	 */
	private topic: Topic;

	constructor() { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.topic$.subscribe(topic => {
				this.topic = topic;
				if (Constants.DEBUG) {
					console.log ('Active topic', topic.id + ':' + this.topic.title);
				}
			})
		);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}


}
