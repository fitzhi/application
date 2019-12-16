import { Component, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';
import { AuditBaseComponent } from '../audit-base-component/audit-base-component.component';

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

	/**
	 * Audit topic whose tasks & todos will be entered in this form.
	 */
	@Input() title: string;

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


	ngOnInit() {
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
