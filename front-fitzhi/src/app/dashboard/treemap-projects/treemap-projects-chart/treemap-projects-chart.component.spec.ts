import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { TreemapProjectsChartComponent } from './treemap-projects-chart.component';


describe('TeamProjectsChartComponent', () => {
	let component: TreemapProjectsChartComponent;
	let fixture: ComponentFixture<TreemapProjectsChartComponent>;
	let dashboardService: DashboardService;
	let projectService: ProjectService;
	let spyProcessProjectsDistribution: any;

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

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ TreemapProjectsChartComponent ],
			imports: [ HttpClientTestingModule, NgxChartsModule, BrowserAnimationsModule, MatDialogModule, RouterTestingModule ],
			providers: [ SkillService, DashboardService, StaffListService, ReferentialService, ProjectService, CinematicService ]

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapProjectsChartComponent);
		component = fixture.componentInstance;
		dashboardService = TestBed.inject(DashboardService);
		spyProcessProjectsDistribution = spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_DISTRIBUTIONS);
		projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);
		fixture.detectChanges();
	});

	it('should instantiate successfully the chart.', () => {
		expect(component).toBeTruthy();
		expect(spyProcessProjectsDistribution).toHaveBeenCalled();
	});
});
