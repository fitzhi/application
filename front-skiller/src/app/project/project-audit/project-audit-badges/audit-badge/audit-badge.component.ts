import { Component, OnInit, Input, OnDestroy, EventEmitter, Output } from '@angular/core';
import { Constants } from 'src/app/constants';
import { CinematicService } from 'src/app/service/cinematic.service';
import { BaseComponent } from 'src/app/base/base.component';
import { thresholdFreedmanDiaconis } from 'd3';

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
	 * This `boolean` represents the fact that the panel
	 * in charge of create or update a remark for this audit is visible.
	 */
	private auditTaskFormModeIsOn = false;

	constructor(private cinematicService: CinematicService) { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.cinematicService.auditTopicSelected$.subscribe (id => {
				console.log ('showOrHideAuditTask called from ngOnInit()');
			}));
	}

	/**
	 * Return the __CSS class__ to used for the thumbail.
	 *
	 * This function is called by the container DIV.
	 * @param id the topic identifier.
	 */
	private classTopic(id: number) {
		const clazz = (this.cinematicService.idTopicSelected === id) ? 'audit-thumbnail-selected' : 'audit-thumbnail';
		return clazz;
	}

	/**
	 * Return the __CSS class__ to used for the tasks icon.
	 *
	 * This function is called by the DIV with `'tasks-{{id}}'` for its display.
	 * @param id the topic identifier.
	 */
	private classIconTasks(id: number) {
		const clazz = ((this.cinematicService.idTopicTaskAuditFormSelected === id) && this.auditTaskFormModeIsOn) ? 'tasks-selected' : 'tasks';
		return clazz;
	}

	/**
	 * Emit the signal that the end-user choosed a new topic.
	 * @param id the topic identifier selected
	 */
	switchTopic(id: number) {
		if (Constants.DEBUG) {
			console.log ('switching to ' + id + ' ' + this.title);
		}
		this.cinematicService.auditTopicSelected$.next(id);
		this.cinematicService.idTopicSelected = id;
	}

	/**
	 * Emit the signal that audit-task form should be displayed, or hidden.
	 */
	private showOrHideAuditTask() {
		console.log ('showOrHideAuditTask called from HTML');
		this.auditTaskFormModeIsOn = !this.auditTaskFormModeIsOn;
		this.messengerShowDivAuditTask.emit(this.id);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}

