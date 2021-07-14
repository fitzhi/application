import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
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
	let spyProcessProjectsDistribution: any;

	const MOCK_DISTRIBUTIONS = [
		{
			name: 'Spring',
			value: '76000',
			color: '#28a745'
		},
		{
			name: 'Fitzhi',
			value: '32300',
			color: '#486E2A'
		},
		{
			name: 'Small',
			value: '1000',
			color: 'darkred'
		}
	];

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TreemapProjectsChartComponent ],
			imports: [ HttpClientTestingModule, NgxChartsModule, BrowserAnimationsModule, MatDialogModule ],
			providers: [ SkillService, DashboardService, StaffListService, ReferentialService, ProjectService, CinematicService ]

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapProjectsChartComponent);
		component = fixture.componentInstance;
		dashboardService = TestBed.inject(DashboardService);
		spyProcessProjectsDistribution = spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_DISTRIBUTIONS);
		fixture.detectChanges();
	});

	it('should instantiate successfully the chart.', () => {
		expect(component).toBeTruthy();
		expect(spyProcessProjectsDistribution).toHaveBeenCalled();
	});
});
