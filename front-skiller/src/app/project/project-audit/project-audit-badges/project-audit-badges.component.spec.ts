import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectAuditBadgesComponent } from './project-audit-badges.component';
import { Component } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AuditBadgeComponent } from './audit-badge/audit-badge.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { AuditGraphicBadgeComponent } from './audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { ReportDetailFormComponent } from './report-detail-form/report-detail-form.component';
import { TasksDetailFormComponent } from './tasks-detail-form/tasks-detail-form.component';
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

describe('ProjectAuditBadgesComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let topics = [];

	@Component({
		selector: 'app-host-component',
		template: 	'<app-project-audit-badges ' +
						'[project$]="project$" ' +
						'[auditTopics$]="auditTopics$"' +
						'[auditDetails$]="auditDetails$">' +
					'</app-project-audit-badges>'
	})
	class TestHostComponent {
		public auditTopics$ = new BehaviorSubject<any>([]);
		public auditDetails$ = new BehaviorSubject<AuditChosenDetail[]>([]);
		public project$ = new BehaviorSubject<Project>(null);
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectAuditBadgesComponent, TestHostComponent, AuditBadgeComponent,
				ReportDetailFormComponent, AuditGraphicBadgeComponent, TasksDetailFormComponent],
			imports: [RootTestModule, MatGridListModule, MatFormFieldModule,
				FormsModule, MatSliderModule, MatInputModule, ReactiveFormsModule ]
		})
		.compileComponents();
	}));


	beforeEach(() => {
		const referentialService: ReferentialService = TestBed.get(ReferentialService);
		const risk2 = new RiskLegend();
		risk2.level = 2;
		risk2.color = 'violet';
		referentialService.legends.push (risk2);
		const risk5 = new RiskLegend();
		risk5.level = 5;
		risk5.color = 'lightBlue';
		referentialService.legends.push (risk5);

		const cinematicService: CinematicService = TestBed.get(CinematicService);
		cinematicService.auditHistory[1] = new AuditDetailsHistory();
		cinematicService.auditHistory[2] = new AuditDetailsHistory();

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		topics = [];
		topics.push({ idTopic: 1, weight: 50, evaluation: 50, title: 'test title One'});
		topics.push({ idTopic: 2, weight: 80, evaluation: 80, title: 'second test title'});
		component.auditTopics$.next(topics);

		const project = new Project();
		project.id = 1889;
		project.name = 'Revolutionnary project';
		project.audit[3] = new AuditTopic(3, 50, 100);
		component.project$.next(project);

		fixture.detectChanges();
	});

	function field(id: string): HTMLInputElement {
		return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
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

	it('should remove the first thumbnail if the corresponding entry in topics is removed ', () => {
		topics.splice(0, 1);
		expect(topics.length).toBe(1);

		component.auditTopics$.next(topics);
		fixture.detectChanges();
		// 1 child expected.
		expect(field('#containerAuditThumbnails').children.length).toBe(1);
		//
		expect (field('#topic-note-2')).toBeDefined();
		expect(field('#topic-input-note-2').getAttribute('ng-reflect-model')).toBe('80');
		expect(field('#topic-title-2').innerHTML).toBe('second test title');

	});

});
