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
import { TreemapSkillsService } from '../treemap-skills-service/treemap-skills.service';
import { TreemapSkillsChartComponent } from '../treemap-skills-chart/treemap-skills-chart.component';
import { TreemapHeaderComponent } from '../treemap-skills-header/treemap-skills-header.component';
import { TreemapSkillsComponent } from './treemap-skills.component';
import { TreemapProjectsService } from '../../treemap-projects/treemap-projects-service/treemap-projects.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('TreemapSkillsComponent container', () => {
	let component: TreemapSkillsComponent;
	let fixture: ComponentFixture<TreemapSkillsComponent>;

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
			declarations: [ TreemapSkillsComponent, TreemapSkillsChartComponent, TreemapHeaderComponent, TagifyStarsComponent ],
			providers: [ReferentialService, DashboardService, TreemapProjectsService, ProjectService, CinematicService],
			imports: [NgxChartsModule, BrowserAnimationsModule, MatCheckboxModule, MatDialogModule, HttpClientTestingModule,
				RouterTestingModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapSkillsComponent);
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
