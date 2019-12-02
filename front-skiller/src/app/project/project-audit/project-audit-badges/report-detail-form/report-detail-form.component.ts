import { Component, OnInit, Input, OnDestroy, AfterViewInit } from '@angular/core';
import { Topic } from '../../table-categories/topic';
import { BaseComponent } from 'src/app/base/base.component';
import { Subject, BehaviorSubject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ProjectService } from 'src/app/service/project.service';
import { Project } from 'src/app/data/project';
import { AuditBaseComponent } from '../audit-base-component/audit-base-component.component';

@Component({
	selector: 'app-report-detail-form',
	templateUrl: './report-detail-form.component.html',
	styleUrls: ['./report-detail-form.component.css']
})
export class ReportDetailFormComponent extends AuditBaseComponent implements OnInit, OnDestroy, AfterViewInit {

	/**
	 * A `BehaviorSubject` containing the current last uptodate project.
	 */
	@Input() project$: BehaviorSubject<Project>;

	/**
	 * The topic identifier.
	 */
	@Input() idTopic: number;

	/**
	 * Audit topic whose report will be entered in this form.
	 */
	@Input() title: string;

	/**
	 * Current active project.
	 */
	private project: Project;

	profileAuditTask = new FormGroup({
		comment: new FormControl('', [Validators.maxLength(2000)])
	});

	constructor(public projectService: ProjectService) {
		super();
		this.postCreationInit('header-report-',
			this.idTopic,
			this.project$,
			this.projectService);
	}

	ngOnInit(): void {
	}

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
