import { Component, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';
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
	 * A `BehaviorSubject` containing the current last uptodate project.
	 */
	@Input() project$;

	/**
	 * The topic identifier.
	 */
	@Input() idTopic: number;

	/**
	 * Title of the badge sent by the parent component.
	 */
	@Input() title: string;

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe((project: Project) => {
				this.project = project;
				if (project.audit[this.idTopic]) {
					this.auditAttachmentService.emitAttachmentFiles(project.audit[this.idTopic].attachmentList);
				} else {
					this.auditAttachmentService.emitAttachmentFiles([]);
				}
		}));
	}

	/**
	 * AfterViewInit function will call the superclass.
	 */
	ngAfterViewInit(): void {
		super.ngAfterViewInit();
	}

	constructor(
		public projectService: ProjectService,
		public auditAttachmentService: AuditAttachmentService) {
		super();
		this.postCreationInit('header-tasks-',
			this.idTopic,
			this.project$,
			this.projectService);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
