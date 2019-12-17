import { Component, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';
import { AuditBaseComponent } from '../audit-base-component/audit-base-component.component';
import { AttachmentFile } from 'src/app/data/AttachmentFile';

@Component({
	selector: 'app-files-detail-form',
	templateUrl: './files-detail-form.component.html',
	styleUrls: ['./files-detail-form.component.css']
})
export class FilesDetailFormComponent extends AuditBaseComponent implements OnInit, OnDestroy, AfterViewInit {

	/**
	 * A `BehaviorSubject` containing the current last uptodate project.
	 */
	@Input() project$: BehaviorSubject<Project>;

	/**
	 * The topic identifier.
	 */
	@Input() idTopic: number;

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.project = project;
			}));
	}

	/**
	 * AfterViewInit function will call the superclass.
	 */
	ngAfterViewInit(): void {
		super.ngAfterViewInit();
	}

	constructor(public projectService: ProjectService) {
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
