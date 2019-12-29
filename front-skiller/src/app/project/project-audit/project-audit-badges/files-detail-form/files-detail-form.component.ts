import { Component, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';
import { AuditBaseComponent } from '../audit-base-component/audit-base-component.component';
import { AttachmentFile } from 'src/app/data/AttachmentFile';
import { ReferentialService } from 'src/app/service/referential.service';

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
			this.project$.subscribe(project => this.project = project));
	}

	/**
	 * AfterViewInit function will call the superclass.
	 */
	ngAfterViewInit(): void {
		super.ngAfterViewInit();
	}

	constructor(
		public projectService: ProjectService) {
		super();
		this.postCreationInit('header-tasks-',
			this.idTopic,
			this.project$,
			this.projectService);
	}

	/**
	 * return `true` if this hosting DIV should be Displayed.
	 * @param id curent file identifier within the topic
	 */
	isAttachmentRecordAvailable(id: number): boolean {
		return (this.project.audit[this.idTopic].attachmentList.length >= id);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
