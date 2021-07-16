import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, HostBinding, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { FitzhiDashboardComponent } from '../../fitzhi-dashboard.component';
import { TreemapProjectsChartComponent } from '../treemap-projects-chart/treemap-projects-chart.component';
import { TreemapProjectsContainerComponent } from './treemap-projects-container.component';


describe('TreemapProjectsContainerComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let dashboardService: DashboardService;
	let projectService: ProjectService;
	let spyDistributionGeneration: any;

	const MOCK_DISTRIBUTIONS = [
		{
			id: 1,
			name: 'java',
			value: '50',
			color: '#28a745'
		},
		{
			id: 2,
			name: '.Net',
			value: '30',
			color: '#486E2A'
		},
		{
			id: 3,
			name: 'Typescript',
			value: '20',
			color: 'darkred'
		}
	];

	@Component({
		selector: 'app-host-component',
		template: `<div><app-treemap-projects width="300px" height="200px" ></app-treemap-projects></div>`
	})
	class TestHostComponent {
		@HostBinding('style.--sidebar-width')
		@Input() sidebarWidth = '200px';
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TreemapProjectsContainerComponent, TreemapProjectsChartComponent, TestHostComponent,
				FitzhiDashboardComponent],
			imports: [ HttpClientTestingModule, MatDialogModule, NgxChartsModule, BrowserAnimationsModule,
				RouterTestingModule ],
			providers: [ ReferentialService, ProjectService, CinematicService, DashboardService ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		dashboardService = TestBed.inject(DashboardService);

		spyDistributionGeneration = spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_DISTRIBUTIONS);

		projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
		expect(spyDistributionGeneration).toHaveBeenCalled();
	});
});
