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
import { MOCK_PROJECTS_DISTRIBUTION } from '../../mock-projects-distribution.spec';
import { MOCK_SKILLS_DISTRIBUTION } from '../../mock-skills-distribution.spec';
import { selection } from '../../selection';
import { environment } from 'src/environments/environment';
import { TurnoverService } from 'src/app/service/turnover/turnover.service';
import { TurnoverData } from 'src/app/service/turnover/turnover-data';


describe('SummaryComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let dashboardService: DashboardService;
	let projectService: ProjectService;
	let staffListService: StaffListService;
	let service: SummaryService;
	let turnoverService: TurnoverService;

	@Component({
		selector: 'app-test-host-component',
		template: `	<div style="width: 800px; height: 800px; top: 0; bottom: 0; left: 0; right: 0; position: fixed; background-color: transparent;">
						<app-summary (messengerSelectedSummary)="switchTo($event)"></app-summary>
					</div>`
	})
	class TestHostComponent {
		@ViewChild(SummaryComponent) summary: SummaryComponent;

		switchTo(sel: number) {
			console.log ('Received selection', sel);
		}
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ SummaryComponent, TestHostComponent, AuditGraphicBadgeComponent,
				TreemapProjectsContainerComponent, TreemapProjectsChartComponent,
				TreemapProjectsChartComponent, TreemapSkillsChartComponent ],
			providers: [ SummaryService, DashboardService, ReferentialService, CinematicService,
				TreemapProjectsService, ProjectService, StaffListService, TurnoverService],
			imports: [ HttpClientTestingModule, MatDialogModule, RouterTestingModule, NgxChartsModule,
				BrowserAnimationsModule, NgxPopper]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		dashboardService = TestBed.inject(DashboardService);

		service = TestBed.inject(SummaryService);

		const referentialService = TestBed.inject(ReferentialService);
		referentialService.legends.push(new RiskLegend(4, 'green'));
		referentialService.referentialLoaded$.next(true);

		projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);

		turnoverService = TestBed.inject(TurnoverService);
		spyOn(turnoverService, 'turnover').and.callFake (year => new TurnoverData(year, 1, 1, 1, -1));

		staffListService = TestBed.inject(StaffListService);
		staffListService.informStaffLoaded();

		// By default, we are in autoConnect OFF.
		environment.autoConnect = false;

		fixture.detectChanges();

	});

	function loadTheCharts(generalAverage: number) {
		spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_PROJECTS_DISTRIBUTION);
		spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_SKILLS_DISTRIBUTION);
		const spy = spyOn(dashboardService, 'calculateGeneralAverage').and.returnValue(generalAverage);
		service.showGeneralAverage();
		fixture.detectChanges();
		return spy;
	}

	it('should not display the 3 turnover panels, if no data is available to compute the turnovers.', done => {

		const currentYear = new Date(Date.now()).getFullYear();

		expect(fixture.debugElement.query(By.css('#turnover-' + currentYear))).toBeNull();
		expect(fixture.debugElement.query(By.css('#turnover-' + (currentYear - 1)))).toBeNull();
		expect(fixture.debugElement.query(By.css('#turnover-' + (currentYear - 2)))).toBeNull();

		loadTheCharts(8);

		staffListService.allStaffLoaded$.subscribe({
			next: doneAndOk => {
				if (doneAndOk) {
					expect(fixture.debugElement.query(By.css('#turnover-' + currentYear))).toBeNull();
					expect(fixture.debugElement.query(By.css('#turnover-' + (currentYear - 1)))).toBeNull();
					expect(fixture.debugElement.query(By.css('#turnover-' + (currentYear - 2)))).toBeNull();
					done();
				}
			}
		});

	});

});
