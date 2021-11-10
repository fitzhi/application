import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { RiskLegend } from 'src/app/data/riskLegend';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { AuditGraphicBadgeComponent } from 'src/app/tabs-project/project-audit/project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { TreemapProjectsChartComponent } from '../../treemap-projects/treemap-projects-chart/treemap-projects-chart.component';
import { TreemapProjectsContainerComponent } from '../../treemap-projects/treemap-projects-container/treemap-projects-container.component';
import { TreemapProjectsService } from '../../treemap-projects/treemap-projects-service/treemap-projects.service';
import { SummaryService } from '../service/summary.service';
import { SummaryComponent } from './summary.component';


describe('SummaryComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let dashboardService: DashboardService;
	let projectService: ProjectService;

	const MOCK_DISTRIBUTIONS = [
		{
			id: 1,
			name: 'Spring',
			value: '76000',
			color: '#28a745'
		},
		{
			id: 2,
			name: 'Fitzhi',
			value: '32300',
			color: '#486E2A'
		},
		{
			id: 3,
			name: 'Small',
			value: '1000',
			color: 'darkred'
		}
	];

	@Component({
		selector: 'app-test-host-component',
		template: `	<div style="width: 800px; height: 800px; top: 0; bottom: 0; left: 0; right: 0; position: fixed; background-color: transparent;">
						<app-summary></app-summary>
					</div>`
	})

	class TestHostComponent {
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ SummaryComponent, TestHostComponent, AuditGraphicBadgeComponent,
				TreemapProjectsContainerComponent, TreemapProjectsChartComponent,
				TreemapProjectsChartComponent ],
			providers: [ SummaryService, DashboardService, ReferentialService, CinematicService,
				TreemapProjectsService],
			imports: [ HttpClientTestingModule, MatDialogModule, RouterTestingModule, NgxChartsModule,
				BrowserAnimationsModule ]
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
		service.showGeneralAverage();
		fixture.detectChanges();
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
		const spyProcessProjectsDistribution = spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_DISTRIBUTIONS);
		const spy = spyOn(dashboardService, 'calculateGeneralAverage').and.returnValue(6);
		const service = TestBed.inject(SummaryService);
		service.showGeneralAverage();
		projectService.allProjectsIsLoaded$.next(true);
		fixture.detectChanges();
		service.summary$.subscribe({
			next: sum => {
				expect(fixture.debugElement.query(By.css('#general-average'))).toBeDefined();
				expect(spy).toHaveBeenCalled();
				done();
			}
		});
	});

});
