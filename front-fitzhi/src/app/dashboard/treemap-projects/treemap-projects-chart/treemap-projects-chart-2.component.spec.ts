import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { TreemapProjectsService } from '../treemap-projects-service/treemap-projects.service';
import { TreemapProjectsChartComponent } from './treemap-projects-chart.component';


describe('TeamProjectsChartComponent', () => {
	let component: TreemapProjectsChartComponent;
	let fixture: ComponentFixture<TreemapProjectsChartComponent>;
	let dashboardService: DashboardService;
	let projectService: ProjectService;
	let spyProcessProjectsDistribution: any;
	let treemapProjectsService: TreemapProjectsService;

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

	/**
	 * We load all labels present in the chart
	 */
	function loadLabels(): string[] {
		const html = fixture.debugElement.queryAll(By.css('.treemap-label'));
		const labels = [];
		html.forEach(element => {
			if (element.name === 'span') {
				labels.push(element.nativeNode.innerText);
			}
		});
		return labels;
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ TreemapProjectsChartComponent ],
			imports: [ HttpClientTestingModule, NgxChartsModule, BrowserAnimationsModule, MatDialogModule, RouterTestingModule ],
			providers: [ SkillService, DashboardService, StaffListService, ReferentialService, ProjectService, CinematicService,
					TreemapProjectsService ]

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapProjectsChartComponent);
		component = fixture.componentInstance;
		dashboardService = TestBed.inject(DashboardService);
		projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);
		treemapProjectsService  = TestBed.inject(TreemapProjectsService);
		treemapProjectsService.informSelectedProjects([1, 2, 3]);
		spyProcessProjectsDistribution = spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_DISTRIBUTIONS);
	});

	it('should instantiate successfully the chart with just 2 projects.', () => {
		treemapProjectsService.informSelectedProjects([1, 2]);
		fixture.detectChanges();

		const labels = loadLabels();
		expect(labels.length).toBe(2);
		expect(labels[0].replace(/\s+/g, ' ')).toEqual(`Spring 76,000 lines`);
		expect(labels[1].replace(/\s+/g, ' ')).toEqual(`Fitzhi 32,300 lines`);
	});
});
