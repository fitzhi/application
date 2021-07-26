import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProjectAuditBadgesComponent } from './project-audit-badges.component';
import { Component, DebugElement } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AuditBadgeComponent } from './audit-badge/audit-badge.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { AuditGraphicBadgeComponent } from './audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { ReportDetailFormComponent } from './report-detail-form/report-detail-form.component';
import { FilesDetailFormComponent } from './files-detail-form/files-detail-form.component';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { Project } from 'src/app/data/project';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { CinematicService } from 'src/app/service/cinematic.service';
import { AuditDetailsHistory } from 'src/app/service/cinematic/audit-details-history';
import { RiskLegend } from 'src/app/data/riskLegend';
import { AuditChosenDetail } from './audit-badge/audit-chosen-detail';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ProjectService } from 'src/app/service/project/project.service';
import { AuditAttachmentComponent } from './files-detail-form/audit-attachment-upload/audit-attachment.component';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { ProjectAuditService } from '../service/project-audit.service';

describe('ProjectAuditBadgesComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;
	let projectAuditService: ProjectAuditService;

	@Component({
		selector: 'app-host-component',
		template: 	'<app-project-audit-badges></app-project-audit-badges>'
	})
	class TestHostComponent {
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectAuditBadgesComponent, TestHostComponent, AuditBadgeComponent,
				ReportDetailFormComponent, AuditGraphicBadgeComponent, FilesDetailFormComponent, AuditAttachmentComponent],
			providers: [ReferentialService, CinematicService, ProjectAuditService],
			imports: [MatGridListModule, MatFormFieldModule,
				HttpClientTestingModule,
				FormsModule, MatSliderModule, MatInputModule, ReactiveFormsModule,
				MatDialogModule, BrowserAnimationsModule ]
		})
		.compileComponents();
	}));


	beforeEach(() => {
		const referentialService: ReferentialService = TestBed.inject(ReferentialService);
		projectService = TestBed.inject(ProjectService);
		const risk2 = new RiskLegend();
		risk2.level = 2;
		risk2.color = 'violet';
		referentialService.legends.push (risk2);
		const risk5 = new RiskLegend();
		risk5.level = 5;
		risk5.color = 'lightBlue';
		referentialService.legends.push (risk5);

		const cinematicService: CinematicService = TestBed.inject(CinematicService);
		cinematicService.auditHistory[1] = new AuditDetailsHistory();
		cinematicService.auditHistory[2] = new AuditDetailsHistory();

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		projectAuditService = TestBed.inject(ProjectAuditService);
		projectAuditService.auditTopics = [];
		projectAuditService.auditTopics.push({ idTopic: 1, weight: 50, evaluation: 50, title: 'test title One'});
		projectAuditService.auditTopics.push({ idTopic: 2, weight: 80, evaluation: 80, title: 'second test title'});
		projectAuditService.auditTopics$.next(projectAuditService.auditTopics);

		const project = new Project();
		project.id = 1889;
		project.name = 'Revolutionary project';
		project.audit[3] = new AuditTopic(3, 50, 100);
		projectService.project = project;
		projectService.projectLoaded$.next(true);

		fixture.detectChanges();
	});

	function field(id: string): HTMLInputElement {
		return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
	}

	function debug(id: string): DebugElement {
		return (fixture.debugElement.query(By.css(id)) as DebugElement);
	}

	it('should be created with 2 thumbnails', () => {
		expect(component).toBeTruthy();
		// 2 children expected.
		expect(field('#containerAuditThumbnails').children.length).toBe(2);
		// 1 app-audit-badge per child div
		expect(field('#containerAuditThumbnails').children[0].children.length).toBe(1);
		//
		expect (field('#topic-note-1')).toBeDefined();
		expect(field('#topic-input-note-1').getAttribute('ng-reflect-model')).toBe('50');
		expect(field('#topic-title-1').innerHTML).toBe('test title One');

	});

	it('should remove the first thumbnail if the corresponding entry in topics is removed.', () => {
		projectAuditService.auditTopics.splice(0, 1);
		expect(projectAuditService.auditTopics.length).toBe(1);
		projectAuditService.auditTopics$.next(projectAuditService.auditTopics);

		fixture.detectChanges();
		// 1 child expected.
		expect(field('#containerAuditThumbnails').children.length).toBe(1);
		//
		expect (field('#topic-note-2')).toBeDefined();
		expect(field('#topic-input-note-2').getAttribute('ng-reflect-model')).toBe('80');
		expect(field('#topic-title-2').innerHTML).toBe('second test title');

	});

});
