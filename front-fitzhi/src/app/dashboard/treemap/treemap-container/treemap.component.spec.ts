import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { TagifyStarsComponent } from 'src/app/tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { TreemapService } from '../service/treemap.service';
import { TreemapChartComponent } from '../treemap-chart/treemap-chart.component';
import { TreemapHeaderComponent } from '../treemap-header/treemap-header.component';
import { TreemapComponent } from './treemap.component';


describe('TreemapComponent container', () => {
	let component: TreemapComponent;
	let fixture: ComponentFixture<TreemapComponent>;

	const MOCK_DISTRIBUTIONS = [
		{
			name: 'java',
			value: '50',
			color: '#28a745'
		},
		{
			name: '.Net',
			value: '30',
			color: '#486E2A'
		},
		{
			name: 'Typescript',
			value: '20',
			color: 'darkred'
		}
	];

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TreemapComponent, TreemapChartComponent, TreemapHeaderComponent,
				TagifyStarsComponent ],
			providers: [ReferentialService, DashboardService, TreemapService, ProjectService, CinematicService],
			imports: [NgxChartsModule, BrowserAnimationsModule, MatCheckboxModule, MatDialogModule, HttpClientTestingModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		const dashboardService = TestBed.inject(DashboardService);
		const spy = spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_DISTRIBUTIONS);

		const projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);

		fixture.detectChanges();
		expect(component).toBeTruthy();
	});

});
