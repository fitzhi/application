import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { RiskLegend } from 'src/app/data/riskLegend';
import { SonarEvaluation } from 'src/app/data/sonar-evaluation';
import { SonarProject } from 'src/app/data/SonarProject';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { AuditGraphicBadgeComponent } from '../project-audit/project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { QuotationBadgeComponent } from '../project-sonar/sonar-dashboard/sonar-quotation/quotation-badge/quotation-badge.component';
import { ListProjectComponent } from './list-project.component';
import { ListProjectsService } from './list-projects-service/list-projects.service';


describe('ProjectAuditComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let referentialService: ReferentialService;
	let listProjectsService: ListProjectsService;
	let projectService: ProjectService;
	const projects = [];

	@Component({
		selector: 'app-host-component',
		template: 	`
					<div style="width:1000px; height: 600px">
						<app-list-project></app-list-project>
					</div>
					`
	})
	class TestHostComponent {
		constructor() {
			localStorage.setItem('project-list.pageSize', '7');
		}
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ListProjectComponent, AuditGraphicBadgeComponent, 
				TestHostComponent, QuotationBadgeComponent ],
			providers: [ReferentialService, StaffListService, ProjectService, 
				ListProjectsService, CinematicService],
			imports: [HttpClientTestingModule, 
				MatTableModule,  MatPaginatorModule, MatSortModule,
				MatDialogModule, RouterTestingModule, BrowserAnimationsModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		referentialService = TestBed.inject(ReferentialService);
		referentialService.legends.push(new RiskLegend(0, 'green'));
		referentialService.legends.push(new RiskLegend(3, 'red'));
		referentialService.legends.push(new RiskLegend(9, 'blue'));
		referentialService.referentialLoaded$.next(true);

		projectService = TestBed.inject(ProjectService);

		listProjectsService = TestBed.inject(ListProjectsService);
		
		listProjectsService.filteredProjects$.next(projects);
		fixture.detectChanges();


	});

	it('should be created without any error', () => {
		expect(component).toBeTruthy();
	});

	it('should display the help panel if the table is empty', () => {
		expect(fixture.debugElement.query(By.css('#help-search'))).toBeDefined();
	});

	it('should remove the help panel if the table is NOT empty', () => {
		initProject();
		fixture.detectChanges();
		expect(fixture.debugElement.query(By.css('#help-search'))).toBeNull();
	});

	it('should display a page with a number of projects equal to the "project-list.pageSize" parameter in localStorage', () => {
		initProject();
		fixture.detectChanges();

		expect(fixture.debugElement.query(By.css('#idProject-0'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#idProject-6'))).toBeDefined();

		expect(fixture.debugElement.query(By.css('#idProject-6')).nativeElement.textContent)
			.toBe('Project number 6');

		console.log(fixture.debugElement.query(By.css('#idProject-6')));

		expect(fixture.debugElement.query(By.css('#idProject-7'))).toBeNull();
	});

	function initProject() {
		for (let i = 0; i < 20; i++) {
			const p = new Project();
			p.id = i;
			p.name = 'Project number ' + i;
			p.auditEvaluation = 70;
			p.staffEvaluation = 0;
			const sp = new SonarProject();
			sp.key = 'key';
			sp.name = 'my Sonar';
			sp.sonarEvaluation = new SonarEvaluation(5, 1000);
			p.sonarProjects.push(sp);
			p.ecosystems = [];
			projects.push(p);
		}
		listProjectsService.filteredProjects$.next(projects);

	}
});
