import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { interpolatePuRd } from 'd3';
import { BaseComponent } from 'src/app/base/base.component';
import { AttachmentFile } from 'src/app/data/AttachmentFile';
import { Constants } from 'src/app/constants';
import { Project } from 'src/app/data/project';

@Component({
	selector: 'app-audit-attachment-upload',
	templateUrl: './audit-attachment-upload.component.html',
	styleUrls: ['./audit-attachment-upload.component.css']
})
export class AuditAttachmentUploadComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * The TOPIC identifier (General conception, Build process, documentation...)
	 */
	@Input() idTopic: number;

	/**
	 * The File identifier. There cannot be more than 4 identifiers.
	 */
	@Input() id: number;

	/**
	 * Observable emitting the current project.
	 */
	@Input() project$;

	/**
	 * Current project read from the `project$`observable..
	 */
	private project: Project;

	private label: string = 'label';

	private fileName: string = 'fileName';

	constructor() {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.project$
				.subscribe(project => {
					if (Constants.DEBUG) {
						console.log(project);
						console.groupCollapsed('Receiving attachment files');
						project.audit[this.idTopic].attachmentList.forEach((element: AttachmentFile) => {
							console.log(element.fileName, element.label);
						});
						console.groupEnd();
					}
					this.project = project;
				}));
	}

	/**
	 * return `true` if this hosting DIV should be Displayed.
	 * @param id curent file identifier within the topic
	 */
	isAttachmentRecordAvailable(id: number): boolean {
		return (this.project.audit[this.idTopic].attachmentList.length >= id);
	}

	/**
	 * Return `true` to disable the download button.
	 * @param id curent file identifier within the topic
	 */
	uploadFile(id: number) {
		if (+id === this.project.audit[this.idTopic].attachmentList.length) {
			if (Constants.DEBUG) {
				console.log('Adding  the file %s labelled with %s',
					this.fileName,
					this.label);
			}
			this.project.audit[this.idTopic].attachmentList.push(new AttachmentFile(id, this.fileName, 2, this.label));
		}
	}

	notifyLabelChange(id: number): void {
		if (+id < this.project.audit[this.idTopic].attachmentList.length) {
			this.project.audit[this.idTopic].attachmentList[id].label = this.label;
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
