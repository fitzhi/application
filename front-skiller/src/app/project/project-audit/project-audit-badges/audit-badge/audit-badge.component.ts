import { Component, OnInit, Input, OnDestroy, EventEmitter, Output } from '@angular/core';
import { Constants } from 'src/app/constants';
import { CinematicService } from 'src/app/service/cinematic.service';
import { BaseComponent } from 'src/app/base/base.component';

@Component({
	selector: 'app-audit-badge',
	templateUrl: './audit-badge.component.html',
	styleUrls: ['./audit-badge.component.css']
})
export class AuditBadgeComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Index of the badge
	 */
	@Input() index;

	@Input() id;

	@Input() title;

	@Output() messengerShowDivAuditTask = new EventEmitter<number>();

	private idSelected = -1;

	constructor(private cinemeticService: CinematicService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.cinemeticService.auditTopicSelected$.subscribe (id => this.idSelected = id));
	}

	switchTopic(id: number) {
		if (Constants.DEBUG) {
			console.log ('switching to ' + id + ' ' + this.title);
		}
		this.cinemeticService.auditTopicSelected$.next(id);
	}

	private classTopic(id: number) {
		const clazz = (this.idSelected === id) ? 'audit-badge-selected' : 'audit-badge';
		return clazz;
	}

	addAuditTask() {
		this.messengerShowDivAuditTask.emit(this.id);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}

