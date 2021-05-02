import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectAuditComponent } from './project-audit.component';
import { ProjectAuditBadgesComponent } from './project-audit-badges/project-audit-badges.component';
import { TableCategoriesComponent } from './table-categories/table-categories.component';
import { AuditBadgeComponent } from './project-audit-badges/audit-badge/audit-badge.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatGridListModule } from '@angular/material/grid-list';
import { AuditGraphicBadgeComponent } from './project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { ReferentialService } from 'src/app/service/referential.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClientModule } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReportDetailFormComponent } from './project-audit-badges/report-detail-form/report-detail-form.component';
import { FilesDetailFormComponent } from './project-audit-badges/files-detail-form/files-detail-form.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project/project.service';
import { AuditAttachmentComponent } from './project-audit-badges/files-detail-form/audit-attachment-upload/audit-attachment.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

describe('ProjectAuditComponent', () => {
	let component: ProjectAuditComponent;
	let fixture: ComponentFixture<ProjectAuditComponent>;
	let projectService: ProjectService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectAuditComponent, ProjectAuditBadgesComponent,
				TableCategoriesComponent, AuditBadgeComponent, ReportDetailFormComponent,
				AuditGraphicBadgeComponent, FilesDetailFormComponent, AuditAttachmentComponent],
			providers: [ReferentialService, CinematicService],
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
		project.audit = {};
		projectService = TestBed.inject(ProjectService);
		projectService.project = project;
		projectService.projectLoaded$ = new BehaviorSubject(true);
		fixture.detectChanges();
	});

	it('should be created without any error', () => {
		expect(component).toBeTruthy();
	});
});
