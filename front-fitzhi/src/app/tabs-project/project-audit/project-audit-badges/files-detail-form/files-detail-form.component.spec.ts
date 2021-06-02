import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FilesDetailFormComponent } from './files-detail-form.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Project } from 'src/app/data/project';
import { Component } from '@angular/core';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { ProjectService } from 'src/app/service/project/project.service';
import { AuditAttachmentComponent } from './audit-attachment-upload/audit-attachment.component';
import { FormsModule } from '@angular/forms';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatDialogModule } from '@angular/material/dialog';
import { RiskLegend } from 'src/app/data/riskLegend';
import { CinematicService } from 'src/app/service/cinematic.service';
import { TopicEvaluation } from '../topic-evaluation';

describe('FilesDetailFormComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;
	let referentialService: ReferentialService;

	@Component({
		selector: 'app-host-component',
		template: 	'<app-files-detail-form ' +
						'[title]="title" ' +
						'[idTopic]="idTopic" >' +
					'</app-files-detail-form>'
	})
	class TestHostComponent {
		public idTopic = 3;
		public title = 'title for topic 3';
		constructor() {}
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TestHostComponent, FilesDetailFormComponent, AuditAttachmentComponent ],
			imports: [FormsModule, HttpClientTestingModule, MatDialogModule],
			providers: [
				ReferentialService, CinematicService
			]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		projectService = TestBed.inject(ProjectService);
		referentialService = TestBed.inject(ReferentialService);
		referentialService.legends.push(new RiskLegend(0, 'green', 'The prefection'));
		referentialService.legends.push(new RiskLegend(5, 'red', 'Numero 5, like Chanel'));
		projectService.project = new Project(1, 'test');
		projectService.project.auditEvaluation = 50;
		projectService.project.audit[3] = new AuditTopic(3, 50, 100);
		projectService.projectLoaded$.next(true);
		fixture.detectChanges();
	});

	it('should be created successfully.', () => {
		expect(component).toBeTruthy();
	});

	it('should have a header color dynamicaly bind to the current evaluation.', done => {

		const spy1 = spyOn(projectService, 'getEvaluationColor').and.callThrough();
		const spy2 = spyOn(projectService, 'getRiskColor').and.callThrough();

		projectService.topicEvaluation$.subscribe({
			next: (te: TopicEvaluation) => {
				expect(spy1).toHaveBeenCalled();
				expect(spy2).toHaveBeenCalled();
				done();
			}
		})
		projectService.topicEvaluation$.next(new TopicEvaluation(3, 50, 2));

	});
});
