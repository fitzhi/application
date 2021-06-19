import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
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
import { BehaviorSubject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { AuditDetail } from 'src/app/data/audit-detail';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { Project } from 'src/app/data/project';
import { RiskLegend } from 'src/app/data/riskLegend';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { ProjectAuditComponent } from '../project-audit.component';
import { ProjectAuditService } from '../service/project-audit.service';
import { TableCategoriesComponent } from '../table-categories/table-categories.component';
import { AuditBadgeComponent } from './audit-badge/audit-badge.component';
import { AuditChosenDetail } from './audit-badge/audit-chosen-detail';
import { AuditGraphicBadgeComponent } from './audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { AuditAttachmentComponent } from './files-detail-form/audit-attachment-upload/audit-attachment.component';
import { FilesDetailFormComponent } from './files-detail-form/files-detail-form.component';
import { ProjectAuditBadgesComponent } from './project-audit-badges.component';
import { ReportDetailFormComponent } from './report-detail-form/report-detail-form.component';


describe('ProjectAuditBadgesComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;
	let referentialService: ReferentialService;
	let projectAuditService: ProjectAuditService;
	let cinematicService: CinematicService;

	@Component({
		selector: 'app-host-component',
		template: `<div>
						<app-project-audit-badges></app-project-audit-badges>
					</div>`
	})
	class TestHostComponent {
	}

	beforeEach(async(() => {
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

		cinematicService = TestBed.inject(CinematicService);

		referentialService = TestBed.inject(ReferentialService);
		referentialService.topics$.next({3: 'Test'});
		referentialService.legends.push(new RiskLegend(0, 'green', 'yes!'));
		referentialService.referentialLoaded$.next(true);

		fixture.detectChanges();

		projectAuditService = TestBed.inject(ProjectAuditService);
		projectAuditService.displayDetail(new AuditChosenDetail(3, AuditDetail.Report));
		projectAuditService.displayDetail(new AuditChosenDetail(3, AuditDetail.Tasks));
		fixture.detectChanges();
	});

	it('should remove all details panels displayed when clicking on a tab.', done => {
		expect(component).toBeTruthy();
		const spyProjectAuditService = spyOn (projectAuditService, 'cleanupAuditDetails').and.returnValue();
		cinematicService.tabProjectActivatedSubject$.next(Constants.PROJECT_IDX_TAB_FORM);
		fixture.detectChanges();
		setTimeout(() =>  {
			expect(spyProjectAuditService).toHaveBeenCalled();
			done();
		}, 0);
	});

});
