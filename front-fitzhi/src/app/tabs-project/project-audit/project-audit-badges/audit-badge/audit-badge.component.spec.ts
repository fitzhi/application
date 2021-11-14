import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AuditBadgeComponent } from './audit-badge.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { AuditGraphicBadgeComponent } from './audit-graphic-badge/audit-graphic-badge.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { CinematicService } from 'src/app/service/cinematic.service';
import { AuditDetailsHistory } from 'src/app/service/cinematic/audit-details-history';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { RiskLegend } from 'src/app/data/riskLegend';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Component, ViewChild } from '@angular/core';
import { ProjectService } from 'src/app/service/project/project.service';
import { Project } from 'src/app/data/project';
import { TopicEvaluation } from '../topic-evaluation';
import { take } from 'rxjs/operators';

describe('AuditBadgeComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;

	@Component({
		selector: 'app-host-component',
		template: `<div style="width: 400px; height: 400px; margin: 20px">
						<app-audit-badge
							[id]="1"
							[evaluation]="evaluation"
							[weight]="weight"
							[title]="title">
						</app-audit-badge>
					</div>`
	})
	class TestHostComponent {
		@ViewChild(AuditBadgeComponent) auditBadgeComponent: AuditBadgeComponent;
		id = 1;
		evaluation = 50;
		weight = 100;
		title = 'the Title';
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [AuditBadgeComponent, AuditGraphicBadgeComponent, TestHostComponent],
			providers: [ReferentialService, CinematicService, ProjectService],
			imports : [MatGridListModule, MatFormFieldModule, MatSliderModule,
				MatInputModule, FormsModule, HttpClientTestingModule, MatDialogModule,
				BrowserAnimationsModule ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		const cinematicService: CinematicService = TestBed.inject(CinematicService);
		cinematicService.auditHistory[1] = new AuditDetailsHistory();
		const referentialService: ReferentialService = TestBed.inject(ReferentialService);
		const risk = new RiskLegend();
		risk.level = 5;
		risk.color = 'blue';
		referentialService.legends.push (risk);
		referentialService.referentialLoaded$.next(true);

		projectService = TestBed.inject(ProjectService);
		projectService.project = new Project(1789, 'The revolutionary project');
		projectService.projectLoaded$.next(true);

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
	});

	it('should create correctly the audit-badge Component', () => {
		fixture.detectChanges();
		expect(component).toBeTruthy();
	});

	it('should handle correctly the change of a topic evaluation.', done => {

		projectService.topicEvaluation$.pipe(take(1)).subscribe({
			next: (te: TopicEvaluation) => {
				expect(te.value).toBe(20);
				expect(te.idTopic).toBe(1);
				expect(te.typeOfOperation).toBe(2);
				done();
			}
		});

		fixture.detectChanges();
		const spyProjectService = spyOn(projectService, 'getEvaluationColor').and.returnValue('green');

		expect(component).toBeTruthy();
		component.auditBadgeComponent.onEvaluationChange(new TopicEvaluation(1, 20, 1));

		fixture.detectChanges();
		expect(spyProjectService).toHaveBeenCalledWith(20);
	});


});
