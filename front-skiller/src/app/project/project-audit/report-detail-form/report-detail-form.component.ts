import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Topic } from '../table-categories/topic';
import { BaseComponent } from 'src/app/base/base.component';
import { Subject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
	selector: 'app-report-detail-form',
	templateUrl: './report-detail-form.component.html',
	styleUrls: ['./report-detail-form.component.css']
})
export class ReportDetailFormComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Observable emitting the current active project/topic.
	 */
	@Input() topic$: Subject<Topic>;

	/**
	 * Current active topic received from the observable `topic$`.
	 */
	private topic: Topic;

	profileAuditTask = new FormGroup({
		comment: new FormControl('', [Validators.maxLength(2000)])
	});

	constructor() { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.topic$.subscribe(topic => {
				this.topic = topic;
				if (Constants.DEBUG) {
					console.log ('Active topic', this.topic.id + ':' + this.topic.title);
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
