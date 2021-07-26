import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

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
import { ProjectService } from 'src/app/service/project/project.service';
import { MatDialogModule } from '@angular/material/dialog';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { RiskLegend } from 'src/app/data/riskLegend';
import { CinematicService } from 'src/app/service/cinematic.service';

describe('ReportDetailFormComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;
	let referentialService: ReferentialService;

	@Component({
		selector: 'app-host-component',
		template: 	'<app-report-detail-form ' +
						'[idTopic]="1" ' +
						'[title]="\'Title for topic 1\'" >' +
					'</app-report-detail-form>'
	})
	class TestHostComponent {
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ ReportDetailFormComponent, TestHostComponent ],
			providers: [ReferentialService, CinematicService],
			imports: [MatFormFieldModule, FormsModule, ReactiveFormsModule,
				HttpClientTestingModule,
				MatInputModule, BrowserAnimationsModule, MatDialogModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		projectService = TestBed.inject(ProjectService);
		projectService.project = new Project(1, 'Testing project');
		projectService.project.audit['1'] = new AuditTopic(1, 1, 100, 100, 'This is a perfect topic', []);
		projectService.projectLoaded$.next(true);

		referentialService = TestBed.inject(ReferentialService);
		referentialService.legends.push(new RiskLegend(0, 'lightBlue', 'blue like the sky today'));
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
