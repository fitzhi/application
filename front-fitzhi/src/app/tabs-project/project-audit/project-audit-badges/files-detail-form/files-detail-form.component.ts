import { Component, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project/project.service';
import { AuditBaseComponent } from '../audit-base-component/audit-base-component.component';
import { AttachmentFile } from 'src/app/data/AttachmentFile';
import { ReferentialService } from 'src/app/service/referential.service';
import { AuditAttachment } from './audit-attachment-upload/audit-attachment.component';
import { AuditAttachmentService } from './service/audit-attachment.service';

@Component({
	selector: 'app-files-detail-form',
	templateUrl: './files-detail-form.component.html',
	styleUrls: ['./files-detail-form.component.css']
})
export class FilesDetailFormComponent extends AuditBaseComponent implements OnInit, OnDestroy, AfterViewInit {

	/**
	 * The topic identifier.
	 */
	@Input() idTopic: number;

	/**
	 * Title of the badge sent by the parent component.
	 */
	@Input() title: string;


	constructor(
		public projectService: ProjectService,
		public auditAttachmentService: AuditAttachmentService) {
		super('header-tasks-', projectService);
	}

	ngOnInit() {
		this.setIdTopic(this.idTopic);
		if (this.projectService.project.audit[this.idTopic]) {
			this.auditAttachmentService.emitAttachmentFiles(
				this.projectService.project.audit[this.idTopic].attachmentList);
		} else {
			this.auditAttachmentService.emitAttachmentFiles([]);
		}
	}

	/**
	 * AfterViewInit function will call the superclass.
	 */
	ngAfterViewInit(): void {
		super.ngAfterViewInit();
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
