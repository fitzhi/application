import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Topic } from '../../table-categories/topic';
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
	 * The topic identifier.
	 */
	@Input() idTopic: number;

	/**
	 * Audit topic whose report will be entered in this form.
	 */
	@Input() title: string;

	profileAuditTask = new FormGroup({
		comment: new FormControl('', [Validators.maxLength(2000)])
	});

	constructor() { super(); }

	ngOnInit() {
	}

	private getTitle(id: number) {
		return 'title ' + this.idTopic;
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
