import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { FileService } from 'src/app/service/file.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { AuditAttachmentService } from '../service/audit-attachment.service';
import { AuditUploadAttachmentComponent } from './audit-upload-attachment/audit-upload-attachment.component';

/**
 * Class of parameters for the upload attachment dialogBox.
 */
export class AuditAttachment  {
	constructor(
		public idProject: number,
		public idTopic: number,
		public filename: string = '',
		public type: number = -1,
		public label: string = '') {}
}


@Component({
	selector: 'app-audit-attachment',
	templateUrl: './audit-attachment.component.html',
	styleUrls: ['./audit-attachment.component.css']
})
export class AuditAttachmentComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
	 * The TOPIC identifier (General conception, Build process, documentation...)
	 */
	@Input() idTopic: number;

	/**
	 * The File identifier. There cannot be more than 4 identifiers.
	 */
	@Input() id: number;

	/**
	 * attachmentFile : the associated attachment file
	 */
	@Input() attachmentFile;

	/**
	 * File label.
	 */
	label = '';

	/**
	 * This `boolean` setup if we are in upload mode (or in delete mode).
	 */
	public modeUpload = true;

	/**
	 * Label associated to the type of applicaton.
	 */
	private relatedApplicationIcon: string;

	constructor(
		private dialog: MatDialog,
		private fileService: FileService,
		private messageService: MessageService,
		private auditAttachmentService: AuditAttachmentService,
		private projectService: ProjectService) {
		super();
	}

	ngOnInit() {

		// A file has already been uploaded
		if ((this.attachmentFile) && (this.attachmentFile.fileName)) {
			this.relatedApplicationIcon = this.fileService.getAssociatedIcon(this.attachmentFile.typeOfFile);
			this.label = this.attachmentFile.label;
			this.modeUpload = false;
		} else {
			this.label = '';
			this.modeUpload = true;
		}
	}

	/**
	 * Return `true` to disable the download button.
	 * @param id curent file identifier within the topic
	 */
	uploadFile(id: number) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;
		dialogConfig.panelClass = 'default-dialog-container-class';
		dialogConfig.data = new AuditAttachment(this.projectService.project.id, this.idTopic, '', -1, this.label);
		const dialogReference = this.dialog.open(AuditUploadAttachmentComponent, dialogConfig);
		this.subscriptions.add(
			dialogReference.afterClosed().subscribe((auditAttachment: AuditAttachment)  => {
				if (auditAttachment) {
					if (traceOn()) {
						console.log(`Adding the file ${auditAttachment.filename} labelled with ${this.label}`);
					}
					this.projectService.loadMapSkills(this.projectService.project);
					if (traceOn()) {
						this.projectService.dump(this.projectService.project, 'uploadFile');
					}
				}
		}));
	}

	/**
	 * Download the audit file from the backend.
	 * @param idFile the file identifier inside this topic
	 */
	downloadFile() {
		this.projectService.downloadAuditAttachment(this.projectService.project.id, this.idTopic, this.attachmentFile);
	}

	/**
	 * Delete the attachment file from the system for a topic inside a project.
	 */
	deleteFile() {
		this.projectService.deleteAuditAttachment(this.projectService.project.id, this.idTopic, this.attachmentFile)
			.subscribe(doneAndOk => {
				if (doneAndOk) {
					this.messageService.success(`The file '${this.attachmentFile.fileName}' has been removed from the system.`);
					this.auditAttachmentService.removeAttachmentFile(this.idTopic, this.id);
					if (traceOn()) {
						this.projectService.dump(this.projectService.project, 'deleteFile');
					}
				}
			});
	}

	/**
	 * The method received the notification when the label has been changed.
	 * @param id current file identifier
	 */
	notifyLabelChange(id: number): void {
		if (+id < this.projectService.project.audit[this.idTopic].attachmentList.length) {
			this.projectService.project.audit[this.idTopic].attachmentList[id].label = this.label;
		}
	}

	/**
	 * Return `true` to disable the download button.
	 * @param id curent file identifier within the topic
	 */
	isDisable(id: number): boolean {
		const auditTopic = this.projectService.project.audit[this.idTopic].attachmentList[this.id];
		if ((!auditTopic) || (!auditTopic.fileName)) {
			return true;
		}
		return false;
	}

	/**
	 * Return the class file
	 */
	classFile(): string {
		return (this.attachmentFile) ?
			this.fileService.getAssociatedAwesomeFont(this.attachmentFile.typeOfFile)
			: '';
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
