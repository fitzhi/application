import { AfterViewInit, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { AuditBaseDirective } from '../audit-base-component/audit-base-directive.directive';

@Component({
	selector: 'app-report-detail-form',
	templateUrl: './report-detail-form.component.html',
	styleUrls: ['./report-detail-form.component.css']
})
export class ReportDetailFormComponent extends AuditBaseDirective implements OnInit, OnDestroy, AfterViewInit {

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
		super('header-report-', projectService);
	}

	ngOnInit(): void {
		this.setIdTopic(this.idTopic);
		this.profileAuditReport.get('comment').setValue(this.projectService.project.audit[this.idTopic].report);
	}

	ngAfterViewInit(): void {
		super.ngAfterViewInit();
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
		this.projectService.saveAuditTopicReport$(this.idTopic, report)
			.subscribe(doneAndOk => {
				if (doneAndOk) {
					this.messageService.success('The summary report for \'' + this.title + '\' successfully saved!');
					this.projectService.project.audit[this.idTopic].report = report;
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
