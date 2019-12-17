import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { interpolatePuRd } from 'd3';
import { BaseComponent } from 'src/app/base/base.component';
import { AttachmentFile } from 'src/app/data/AttachmentFile';

@Component({
	selector: 'app-audit-attachment-upload',
	templateUrl: './audit-attachment-upload.component.html',
	styleUrls: ['./audit-attachment-upload.component.css']
})
export class AuditAttachmentUploadComponent extends BaseComponent implements OnInit, OnDestroy {

	@Input() id: number;

	/**
	 * Observable sharing the list of attachment files inside the filesDetailsComponent.
	 */
	@Input() attachmentList$;

	/**
	 * List of attachment files.
	 */
	public attachmentList: AttachmentFile[];

	private label: string;

	constructor() {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.attachmentList$.subscribe(attachmentList => this.attachmentList = attachmentList));
	}

	/**
	 * Display or not display this DIV.
	 * @param id curent file identifier within the topic
	 */
	displayAttachment(id: number) {
		return (this.attachmentList.length >= id);
	}

	/**
	 * Return `true` to disable the download button.
	 * @param id curent file identifier within the topic
	 */
	uploadFile(id: number) {
		if (+id === this.attachmentList.length) {
			console.log ('Upload ' + id);
			this.attachmentList.push(new AttachmentFile(1, 'nope', 2, this.label));
			this.attachmentList$.next(this.attachmentList);
		}
	}

	/**
	 * Return `true` to disable the download button.
	 * @param id curent file identifier within the topic
	 */
	isDisable(id: number): boolean {
		return true;
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
