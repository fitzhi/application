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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Project } from 'src/app/data/project';
import { RiskLegend } from 'src/app/data/riskLegend';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { AuditGraphicBadgeComponent } from './audit-graphic-badge.component';


describe('AuditGraphicBadgeComponent in readonly mode', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;

	@Component({
		selector: 'app-host-component',
		template: `	<div style="display: inline-flex; width: 500px; height: 200px">
						<div style="width: 100px; height: 100px">
							<app-audit-graphic-badge [id]=1 [project]=p1 [editable]=false>
							</app-audit-graphic-badge>
						</div>
						<div style="width: 100px; height: 100px">
							<app-audit-graphic-badge [id]=2 [project]=p2 [editable]=false>
							</app-audit-graphic-badge>
						</div>
					</div>`
	})

	class TestHostComponent {
		public p1 = new Project(1, 'One');
		public p2 = new Project(2, 'Two');
		constructor() {
			this.p1.auditEvaluation = 99;
			this.p2.auditEvaluation = 100;
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

	it('should display 2 different badges.', () => {
		expect(component).toBeTruthy();
	});

});
