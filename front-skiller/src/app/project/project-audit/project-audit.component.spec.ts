import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectAuditComponent } from './project-audit.component';
import { ProjectAuditBadgesComponent } from './project-audit-badges/project-audit-badges.component';
import { TableCategoriesComponent } from './table-categories/table-categories.component';
import { AuditBadgeComponent } from './project-audit-badges/audit-badge/audit-badge.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { FormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatGridListModule } from '@angular/material/grid-list';
import { AuditGraphicBadgeComponent } from './project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { ReferentialService } from 'src/app/service/referential.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClientModule } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReportDetailFormComponent } from './report-detail-form/report-detail-form.component';

describe('ProjectAuditComponent', () => {
	let component: ProjectAuditComponent;
	let fixture: ComponentFixture<ProjectAuditComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectAuditComponent, ProjectAuditBadgesComponent,
				TableCategoriesComponent, AuditBadgeComponent, ReportDetailFormComponent,
				AuditGraphicBadgeComponent,  ],
			providers: [ReferentialService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectAuditComponent);
		component = fixture.componentInstance;
		component.project$ = new BehaviorSubject(null);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
