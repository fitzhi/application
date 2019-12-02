import { Component, OnInit, Input, OnDestroy, AfterViewInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ProjectService } from 'src/app/service/project.service';
import { Project } from 'src/app/data/project';
import { AuditBaseComponent } from '../audit-base-component/audit-base-component.component';
import { MessageService } from 'src/app/message/message.service';
import { take } from 'rxjs/operators';

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

	profileAuditReport = new FormGroup({
		comment: new FormControl('', [Validators.maxLength(2000)])
	});

	constructor(
		public projectService: ProjectService,
		public messageService: MessageService) {
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
		this.subscriptions.add(
			this.project$.subscribe(project => {
				setTimeout(() => {
					this.profileAuditReport.get('comment').setValue(project.audit[this.idTopic].report);
				}, 0);
		}));
	}

	/**
	 * Save the report saved by the end-user.
	 */
	saveReport(): void {
		const report = this.profileAuditReport.get('comment').value;
		if (report.length > 2000) {
			this.messageService.warning('Summary report cannot exceed 2000 cars.');
			return;
		}
		this.projectService.saveAuditTopicReport$(this.project.id, this.idTopic, report)
			.subscribe(doneAndOk => {
				if (doneAndOk) {
					this.messageService.success('The summary report for \'' + this.title + '\' successfully saved!');
					this.project.audit[this.idTopic].report = report;
				}
			});


	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
