import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TasksDetailFormComponent } from './tasks-detail-form.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { Project } from 'src/app/data/project';
import { BehaviorSubject, Subject } from 'rxjs';
import { Component } from '@angular/core';
import { AuditTopic } from 'src/app/data/AuditTopic';

describe('TasksDetailFormComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let project: Project;
	let project$: BehaviorSubject<Project>;

	@Component({
		selector: 'app-host-component',
		template: 	'<app-tasks-detail-form ' +
						'[idTopic]="idTopic" ' +
						'[project$]="project$" >' +
					'</app-tasks-detail-form>'
	})
	class TestHostComponent {
		public idTopic = 3;

		constructor() {
			project = new Project(1, 'test');
			project.auditEvaluation = 50;
			project.audit[3] = new AuditTopic(3, 50, 100);
			project$ = new BehaviorSubject<Project>(project);
		}
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TestHostComponent, TasksDetailFormComponent ],
			imports: [RootTestModule, HttpClientTestingModule]

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		console.log (project);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
