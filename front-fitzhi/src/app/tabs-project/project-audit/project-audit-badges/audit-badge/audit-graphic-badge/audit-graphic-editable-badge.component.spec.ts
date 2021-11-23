import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
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
import { RouterTestingModule } from '@angular/router/testing';
import { Project } from 'src/app/data/project';
import { RiskLegend } from 'src/app/data/riskLegend';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { AuditGraphicBadgeComponent } from './audit-graphic-badge.component';


describe('AuditGraphicBadgeComponent in editable mode', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;

	@Component({
		selector: 'app-host-component',
		template: `	<div style="width: 100px; height: 100px">
						<app-audit-graphic-badge [id]=1 [evaluation]=50 [editable]=true>
						</app-audit-graphic-badge>
					</div>`
	})

	class TestHostComponent {
		public project = new Project(1, 'One');
		constructor() {
			this.project.auditEvaluation = 80;
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

	it('should create the badge without error, but everything is hidden.', () => {
		expect(component).toBeTruthy();
		const badgeReadOnly = fixture.debugElement.query(By.css('#readOnlyBadge'));
		expect(badgeReadOnly).toBeNull();
		const editableBadge = fixture.debugElement.query(By.css('#editableBadge'));
		expect(editableBadge).toBeNull();

	});

	it('should create and display the editable badge.', done  => {
		projectService.project = new Project(1, 'One');
		projectService.project.auditEvaluation = 80;
		projectService.projectLoaded$.next(true);
		fixture.detectChanges();
		const badgeReadOnly = fixture.debugElement.query(By.css('#readOnlyBadge'));
		expect(badgeReadOnly).toBeNull();
		const editableBadge = fixture.debugElement.query(By.css('#editableBadge'));
		expect(editableBadge).toBeDefined();

		done();
	});

});
