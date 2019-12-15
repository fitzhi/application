import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportDetailFormComponent } from './report-detail-form.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { Component } from '@angular/core';

describe('ReportDetailFormComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: 	'<app-report-detail-form ' +
						'[project$]="project$" ' +
						'[idTopic]="1" ' +
						'[title]="\'Title for topic 1\'" >' +
					'</app-report-detail-form>'
	})
	class TestHostComponent {
		public project$ = new BehaviorSubject<Project>(new Project(1, 'Testing project'));
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ReportDetailFormComponent, TestHostComponent ],
			providers: [ReferentialService],
			imports: [MatFormFieldModule, FormsModule, ReactiveFormsModule,
				HttpClientTestingModule,
				MatInputModule, BrowserAnimationsModule]
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
