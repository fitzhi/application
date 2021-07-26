import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSliderModule } from '@angular/material/slider';
import { MatTableModule } from '@angular/material/table';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BehaviorSubject, of } from 'rxjs';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { Project } from 'src/app/data/project';
import { RiskLegend } from 'src/app/data/riskLegend';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { AuditBadgeComponent } from './project-audit-badges/audit-badge/audit-badge.component';
import { AuditGraphicBadgeComponent } from './project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { AuditAttachmentComponent } from './project-audit-badges/files-detail-form/audit-attachment-upload/audit-attachment.component';
import { FilesDetailFormComponent } from './project-audit-badges/files-detail-form/files-detail-form.component';
import { ProjectAuditBadgesComponent } from './project-audit-badges/project-audit-badges.component';
import { ReportDetailFormComponent } from './project-audit-badges/report-detail-form/report-detail-form.component';
import { TopicEvaluation } from './project-audit-badges/topic-evaluation';
import { ProjectAuditComponent } from './project-audit.component';
import { ProjectAuditService } from './service/project-audit.service';
import { TableCategoriesComponent } from './table-categories/table-categories.component';


describe('ProjectAuditComponent', () => {
	let component: ProjectAuditComponent;
	let fixture: ComponentFixture<ProjectAuditComponent>;
	let projectService: ProjectService;
	let referentialService: ReferentialService;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectAuditComponent, ProjectAuditBadgesComponent,
				TableCategoriesComponent, AuditBadgeComponent, ReportDetailFormComponent,
				AuditGraphicBadgeComponent, FilesDetailFormComponent, AuditAttachmentComponent],
			providers: [ReferentialService, CinematicService, ProjectAuditService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				FormsModule, ReactiveFormsModule,
				MatSliderModule, MatInputModule, MatDialogModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectAuditComponent);
		component = fixture.componentInstance;
		const project = new Project();
		project.id = 1789;
		project.name = 'Revolutionary project';
		project.audit[3] = new AuditTopic(3, 50, 100);
		projectService = TestBed.inject(ProjectService);
		projectService.project = project;
		projectService.projectLoaded$ = new BehaviorSubject(true);

		referentialService = TestBed.inject(ReferentialService);
		referentialService.topics$.next({3: 'Test'});
		referentialService.legends.push(new RiskLegend(0, 'green', 'yes!'));
		referentialService.referentialLoaded$.next(true);

		fixture.detectChanges();
	});

	it('should be created without any error.', () => {
		expect(component).toBeTruthy();
	});

	it('should notify the backend when the user change his evaluation on a topic.', done => {
		const spySaveAuditTopicEvaluation = spyOn(projectService, 'saveAuditTopicEvaluation$').and.returnValue(of(false));
		projectService.topicEvaluation$.subscribe({
			next: te => {
				expect(spySaveAuditTopicEvaluation).toHaveBeenCalled();
				done();
			}
		});
		projectService.topicEvaluation$.next(new TopicEvaluation(3, 50, 2));
		fixture.detectChanges();
	});

	it('should update the global project evaluation which has been affected by a new evaluation.', done => {

		const spySaveAuditTopicEvaluation = spyOn(projectService, 'saveAuditTopicEvaluation$').and.returnValue(of(true));

		// Update the underlining GLOBAL project evaluation
		const spyProcessGlobalAuditEvaluation = spyOn(projectService, 'processGlobalAuditEvaluation').and.returnValue(null);

		projectService.topicEvaluation$.subscribe({
			next: te => {
				expect(spySaveAuditTopicEvaluation).toHaveBeenCalled();
				expect(spyProcessGlobalAuditEvaluation).toHaveBeenCalled();
				done();
			}
		});
		projectService.topicEvaluation$.next(new TopicEvaluation(3, 50, 2));
		fixture.detectChanges();
	});

});
