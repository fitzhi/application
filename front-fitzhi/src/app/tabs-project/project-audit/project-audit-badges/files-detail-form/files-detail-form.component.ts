import { Component, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { ProjectService } from 'src/app/service/project/project.service';
import { AuditBaseComponent } from '../audit-base-component/audit-base-component.component';
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
