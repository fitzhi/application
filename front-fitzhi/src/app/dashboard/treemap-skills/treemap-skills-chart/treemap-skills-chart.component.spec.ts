import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
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
import { TreemapSkillsChartComponent } from './treemap-skills-chart.component';


describe('TreemapSkillsChartComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
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

	@Component({
		selector: 'app-host-component',
		template: `
			<div style="position: fixed; bottom: 10px; right: 0; left: 0; top: 200px;">
				<div id="one" style="width: 100px; height: 100px;">
					<app-treemap-skills-chart [buttonOrChart]="'button'" [active]=false></app-treemap-skills-chart>
				</div>
				<div id="two" style="width: 120px; height: 120px;">
					<app-treemap-skills-chart [buttonOrChart]="'chart'" [active]=true></app-treemap-skills-chart>
				</div>
			</div>
			`})
	class TestHostComponent {
		constructor() {
		}
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			imports: [NgxChartsModule, BrowserAnimationsModule, HttpClientTestingModule, MatDialogModule, RouterTestingModule],
			declarations: [ TreemapSkillsChartComponent, TestHostComponent ],
			providers: [DashboardService, ProjectService, ReferentialService, CinematicService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		dashboardService = TestBed.inject(DashboardService);
		spyProcessSkillDistribution = spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_DISTRIBUTIONS);

		projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);

		fixture.detectChanges();
	});

	it('should be created successfully without error.', () => {
		expect(component).toBeTruthy();
	});

	it('should load the processed distribution from the dashboard service.', () => {
		expect(spyProcessSkillDistribution).toHaveBeenCalledTimes(2);
	});

	it('should NOT display labels in "button" mode.', () => {
		const contentOne: string = document.getElementById('one').innerText;
		expect(contentOne.indexOf('java')).toBe(-1);
	});
	it('should display labels in "chart" mode.', () => {
		const contentTwo: string = document.getElementById('two').innerText;
		expect(contentTwo.indexOf('java')).toBe(0);
	});
});
