import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectStaffComponent } from './project-staff.component';
import { Component } from '@angular/core';
import { Project } from 'src/app/data/project';
import { BehaviorSubject } from 'rxjs';

describe('ProjectStaffComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: 	'<app-project-staff ' +
						'[project$]="project$" >' +
					'</app-project-staff>'
	})
	class TestHostComponent {
		public project$ = new BehaviorSubject<Project>(new Project());
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TestHostComponent, ProjectStaffComponent ],
			imports: []
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
