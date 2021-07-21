import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreemapSkillsChartComponent } from './treemap-skills-chart.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { RouterTestingModule } from '@angular/router/testing';
import { dashCaseToCamelCase } from '@angular/compiler/src/util';

describe('TreemapSkillsChartComponent', () => {
	let component: TreemapSkillsChartComponent;
	let fixture: ComponentFixture<TreemapSkillsChartComponent>;
	let dashboardService: DashboardService;
	let projectService: ProjectService;
	let spyProcessSkillDistribution: any;

	const MOCK_DISTRIBUTIONS =  [
		{
			name: 'java',
			value: '50',
			color: '#28a745'
		},
		{
			name: '.Net',
			value: '20',
			color: '#486E2A'
		},
		{
			name: 'Typescript',
			value: '30',
			color: 'darkred'
		}
	];

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [NgxChartsModule, BrowserAnimationsModule, HttpClientTestingModule, MatDialogModule, RouterTestingModule],
			declarations: [ TreemapSkillsChartComponent ],
			providers: [DashboardService, ProjectService, ReferentialService, CinematicService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapSkillsChartComponent);
		component = fixture.componentInstance;

		dashboardService = TestBed.inject(DashboardService);
		spyProcessSkillDistribution = spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_DISTRIBUTIONS);

		projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();		
		expect(spyProcessSkillDistribution).toHaveBeenCalled();
	});
});
