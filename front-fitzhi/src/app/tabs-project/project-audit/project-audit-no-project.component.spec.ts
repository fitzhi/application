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
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BehaviorSubject } from 'rxjs';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { AuditBadgeComponent } from './project-audit-badges/audit-badge/audit-badge.component';
import { AuditGraphicBadgeComponent } from './project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { AuditAttachmentComponent } from './project-audit-badges/files-detail-form/audit-attachment-upload/audit-attachment.component';
import { FilesDetailFormComponent } from './project-audit-badges/files-detail-form/files-detail-form.component';
import { ProjectAuditBadgesComponent } from './project-audit-badges/project-audit-badges.component';
import { ReportDetailFormComponent } from './project-audit-badges/report-detail-form/report-detail-form.component';
import { ProjectAuditComponent } from './project-audit.component';
import { ProjectAuditService } from './service/project-audit.service';
import { TableCategoriesComponent } from './table-categories/table-categories.component';


describe('ProjectAuditComponent', () => {
	let component: ProjectAuditComponent;
	let fixture: ComponentFixture<ProjectAuditComponent>;
	let projectService: ProjectService;

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
		projectService = TestBed.inject(ProjectService);
		projectService.projectLoaded$ = new BehaviorSubject(false);
		fixture.detectChanges();
	});

	it('should display a warning message when the project is not loaded yet.', () => {
		expect(fixture.debugElement.query(By.css('no-project'))).toBeDefined();
	});


});
