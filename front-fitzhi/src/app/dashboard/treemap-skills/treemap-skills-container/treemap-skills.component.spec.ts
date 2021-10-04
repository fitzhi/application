import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { TagifyStarsComponent } from 'src/app/tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { TreemapProjectsService } from '../../treemap-projects/treemap-projects-service/treemap-projects.service';
import { TreemapSkillsChartComponent } from '../treemap-skills-chart/treemap-skills-chart.component';
import { TreemapHeaderComponent } from '../treemap-skills-header/treemap-skills-header.component';
import { TreemapSkillsComponent } from './treemap-skills.component';

describe('TreemapSkillsComponent container', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;

	@Component({
		selector: 'app-host-component',
		template: `
			<div style="position: fixed; bottom: 10px; right: 0; left: 0; top: 200px">
				<app-treemap-skills></app-treemap-skills>
			</div>`})
	class TestHostComponent {
		constructor() {
		}
	}

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

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ TreemapSkillsComponent, TreemapSkillsChartComponent, TreemapHeaderComponent, TagifyStarsComponent, TestHostComponent ],
			providers: [ReferentialService, DashboardService, TreemapProjectsService, ProjectService, CinematicService],
			imports: [NgxChartsModule, BrowserAnimationsModule, MatCheckboxModule, MatDialogModule, HttpClientTestingModule,
				RouterTestingModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		localStorage.clear();
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		projectService = TestBed.inject(ProjectService);

		fixture.detectChanges();
	});

	function loadChart() {
		localStorage.removeItem('helpHeight');
		const dashboardService = TestBed.inject(DashboardService);
		const spy = spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_DISTRIBUTIONS);
		projectService.allProjectsIsLoaded$.next(true);
	}

	it('should be created without error.', done => {
		loadChart();
		fixture.detectChanges();
		expect(component).toBeTruthy();
		done();

	});

	it('should display the help pane by default.', () => {
		loadChart();
		fixture.detectChanges();

		expect(document.getElementById('help')).not.toBeNull();
		expect(document.getElementById('btHelp')).not.toBeNull();
		expect(document.getElementById('btHelp').classList[0]).toBe('btn');
		expect(document.getElementById('btHelp').classList[1]).toBe('ml-2');
		expect(document.getElementById('btHelp').classList[2]).toBe('btn-outline-success');
	});

});
