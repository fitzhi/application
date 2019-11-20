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

	/**
	 * This messenger emits a signal to show/hide the audit form panel
	 */
	@Output() messengerShowDivAuditTask = new EventEmitter<number>();

	/**
	 * Topic identitier selected.
	 */
	private idSelected = -1;

	constructor(private cinemeticService: CinematicService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.cinemeticService.auditTopicSelected$.subscribe (id => this.idSelected = id));
	}

	/**
	 * Emit the signal that the enduser choosed a new topic.
	 * @param id the topic identifier selected
	 */
	switchTopic(id: number) {
		if (Constants.DEBUG) {
			console.log ('switching to ' + id + ' ' + this.title);
		}
		this.cinemeticService.auditTopicSelected$.next(id);
	}

	/**
	 * Return the __CSS class__ to used for the thumbail.
	 *
	 * This function is called by the container DIV.
	 * @param id the topic identifier.
	 */
	private classTopic(id: number) {
		const clazz = (this.idSelected === id) ? 'audit-thumbnail-selected' : 'audit-thumbnail';
		return clazz;
	}

	/**
	 * Return the __CSS class__ to used for the tasks icon.
	 *
	 * This function is called by the DIV with `'tasks-{{id}}'` for its display.
	 * @param id the topic identifier.
	 */
	private classIconTasks(id: number) {
		const clazz = (this.idSelected === id) ? 'tasks-selected' : 'tasks';
		return clazz;
	}

	/**
	 * Emit the signal that audit-task form should be displayed, or hidden.
	 */
	private addAuditTask() {
		this.messengerShowDivAuditTask.emit(this.id);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}

