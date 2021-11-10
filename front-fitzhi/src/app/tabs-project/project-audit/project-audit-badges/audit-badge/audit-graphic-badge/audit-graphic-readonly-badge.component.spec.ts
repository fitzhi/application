import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AuditGraphicBadgeComponent } from './audit-graphic-badge.component';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RiskLegend } from 'src/app/data/riskLegend';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClientModule } from '@angular/common/http';
import { MatTableModule } from '@angular/material/table';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ProjectService } from 'src/app/service/project/project.service';
import { Project } from 'src/app/data/project';
import { By } from '@angular/platform-browser';

describe('AuditGraphicBadgeComponent in readonly mode', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;

	@Component({
		selector: 'app-host-component',
		template: `	<div style="width: 100px; height: 100px">
						<app-audit-graphic-badge [id]=1 [project]=project [editable]=false>
						</app-audit-graphic-badge>
					</div>`
	})

	class TestHostComponent {
		public project = new Project(1, 'One');
		constructor() {
			this.project.auditEvaluation = 50;
		}
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [TestHostComponent, AuditGraphicBadgeComponent],
			providers: [ReferentialService, CinematicService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule,
				RouterTestingModule.withRoutes([])]

		})
			.compileComponents();
	}));

	beforeEach(() => {
		const referentialService: ReferentialService = TestBed.inject(ReferentialService);
		const risk = new RiskLegend();
		risk.level = 5;
		risk.color = 'blue';
		referentialService.legends.push (risk);
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		projectService = TestBed.inject(ProjectService);
		fixture.detectChanges();
	});

	it('should create and draw the badge IN READONLY mode.', () => {
		expect(component).toBeTruthy();
		const badgeReadOnly = fixture.debugElement.query(By.css('#readOnlyBadge'));
		expect(badgeReadOnly).toBeDefined();
		const editableBadge = fixture.debugElement.query(By.css('#editableBadge'));
		expect(editableBadge).toBeNull();
	});

});
