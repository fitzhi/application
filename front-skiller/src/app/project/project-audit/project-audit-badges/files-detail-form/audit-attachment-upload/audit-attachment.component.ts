import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { AttachmentFile } from 'src/app/data/AttachmentFile';
import { Constants } from 'src/app/constants';
import { Project } from 'src/app/data/project';
import { MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { AuditUploadAttachmentComponent } from './audit-upload-attachment/audit-upload-attachment.component';
import { ProjectService } from 'src/app/service/project.service';

@Component({
	selector: 'app-audit-attachment',
	templateUrl: './audit-attachment.component.html',
	styleUrls: ['./audit-attachment.component.css']
})
export class AuditAttachmentComponent extends BaseComponent implements OnInit, OnDestroy {

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
	 * Current project read from the `project$` observable..
	 */
	private project: Project;

	private label: string = 'label';

	private fileName: string = 'fileName';

	constructor(
		private dialog: MatDialog,
		private projectService: ProjectService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.project$
				.subscribe(project => {
					this.projectService.dump(project, 'AuditAttachment.ngOntInit');
					this.project = project;
				}));
	}

	/**
	 * Return `true` to disable the download button.
	 * @param id curent file identifier within the topic
	 */
	uploadFile(id: number) {
		if (+id === this.project.audit[this.idTopic].attachmentList.length) {
			if (Constants.DEBUG) {
				console.log('Adding  thefile %s labelled with %s',
					this.fileName,
					this.label);
			}
			const dialogConfig = new MatDialogConfig();
			dialogConfig.disableClose = true;
			dialogConfig.autoFocus = true;
			dialogConfig.panelClass = 'default-dialog-container-class';
			dialogConfig.data = null;
			const dialogReference = this.dialog.open(AuditUploadAttachmentComponent, dialogConfig);
			this.subscriptions.add(
				dialogReference.afterClosed().subscribe(fileName => {
					this.project.audit[this.idTopic].attachmentList.push(new AttachmentFile(id, this.fileName, 2, this.label));
				}));
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
