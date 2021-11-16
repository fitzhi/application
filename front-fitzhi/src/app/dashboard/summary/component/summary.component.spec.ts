import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { NgxPopper } from 'angular-popper';
import { RiskLegend } from 'src/app/data/riskLegend';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { AuditGraphicBadgeComponent } from 'src/app/tabs-project/project-audit/project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { TreemapProjectsChartComponent } from '../../treemap-projects/treemap-projects-chart/treemap-projects-chart.component';
import { TreemapProjectsContainerComponent } from '../../treemap-projects/treemap-projects-container/treemap-projects-container.component';
import { TreemapProjectsService } from '../../treemap-projects/treemap-projects-service/treemap-projects.service';
import { TreemapSkillsChartComponent } from '../../treemap-skills/treemap-skills-chart/treemap-skills-chart.component';
import { SummaryService } from '../service/summary.service';
import { SummaryComponent } from './summary.component';
import { MOCK_PROJECTS_DISTRIBUTION }  from '../../mock-projects-distribution.spec';
import { MOCK_SKILLS_DISTRIBUTION }  from '../../mock-skills-distribution.spec';


describe('SummaryComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let dashboardService: DashboardService;
	let projectService: ProjectService;
	let staffListService: StaffListService;

	@Component({
		selector: 'app-test-host-component',
		template: `	<div style="width: 800px; height: 800px; top: 0; bottom: 0; left: 0; right: 0; position: fixed; background-color: transparent;">
						<app-summary></app-summary>
					</div>`
	})
	class TestHostComponent {
		@ViewChild(SummaryComponent) summary: SummaryComponent;
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ SummaryComponent, TestHostComponent, AuditGraphicBadgeComponent,
				TreemapProjectsContainerComponent, TreemapProjectsChartComponent,
				TreemapProjectsChartComponent, TreemapSkillsChartComponent ],
			providers: [ SummaryService, DashboardService, ReferentialService, CinematicService,
				TreemapProjectsService, ProjectService, StaffListService],
			imports: [ HttpClientTestingModule, MatDialogModule, RouterTestingModule, NgxChartsModule,
				BrowserAnimationsModule, NgxPopper]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		dashboardService = TestBed.inject(DashboardService);

		const referentialService = TestBed.inject(ReferentialService);
		referentialService.legends.push(new RiskLegend(4, 'green'));
		referentialService.referentialLoaded$.next(true);

		projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);

		staffListService = TestBed.inject(StaffListService);
		staffListService.informStaffLoaded();
		fixture.detectChanges();
	});

	it('should display the main logo at startup.', () => {
		expect(component).toBeTruthy();
		expect(fixture.debugElement.query(By.css('#logo'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#summaries'))).toBeNull();
		expect(fixture.debugElement.query(By.css('#small-logo'))).toBeNull();
	});

	it('show the small logo when the first summary is loaded.', done => {
		const service = TestBed.inject(SummaryService);
		spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_PROJECTS_DISTRIBUTION);
		spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_SKILLS_DISTRIBUTION);
		const spy = spyOn(dashboardService, 'calculateGeneralAverage').and.returnValue(1);
		service.showGeneralAverage();
		fixture.detectChanges();
		expect(spy).toHaveBeenCalled();
		service.summary$.subscribe({
			next: sum => {
				expect(fixture.debugElement.query(By.css('#logo'))).toBeNull();
				expect(fixture.debugElement.query(By.css('#summaries'))).toBeDefined();
				expect(fixture.debugElement.query(By.css('#small-logo'))).toBeDefined();
				done();
			}
		});
	});

	it('should display the general average badge.', done => {
		const treemapProjectsService  = TestBed.inject(TreemapProjectsService);
		treemapProjectsService.informSelectedProjects([1, 2, 3]);
		staffListService.informStaffLoaded();
		projectService.allProjectsIsLoaded$.next(true);
		spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_PROJECTS_DISTRIBUTION);
		spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_SKILLS_DISTRIBUTION);
		const spy = spyOn(dashboardService, 'calculateGeneralAverage').and.returnValue(6);
		const service = TestBed.inject(SummaryService);
		service.showGeneralAverage();
		fixture.detectChanges();

		service.summary$.subscribe({
			next: sum => {
				expect(fixture.debugElement.query(By.css('#general-average'))).toBeDefined();
				expect(spy).toHaveBeenCalled();
				done();
			}
		});
	});

	it('should display an help-popup when the mouse moves over the widget.', done => {

		let help = fixture.debugElement.query(By.css('#help-general-average'));
		expect(help).toBeNull();

		spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_PROJECTS_DISTRIBUTION);
		spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_SKILLS_DISTRIBUTION);
		const spy = spyOn(dashboardService, 'calculateGeneralAverage').and.returnValue(3);
		const service = TestBed.inject(SummaryService);
		service.showGeneralAverage();
		fixture.detectChanges();

		service.summary$.subscribe({
			next: sum => {
				const score = fixture.debugElement.query(By.css('#general-average')).nativeElement;
				score.dispatchEvent(new Event('mouseenter'));
				fixture.detectChanges();
				help = fixture.debugElement.query(By.css('#help-general-average'));
				expect(help).toBeDefined();``
				done();
			}
		});
	});

	it('should display an help-popup when the methoid hasGeneralAverage() returns TRUE.', () => {
		let help = fixture.debugElement.query(By.css('#help-general-average'));
		expect(help).toBeNull();

		spyOn(component.summary, 'hasGeneralAverage').and.returnValue(true);
		help = fixture.debugElement.query(By.css('#help-general-average'));

		expect(help).toBeDefined();
	});
});
