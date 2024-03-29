import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { DynamicPieChartModule } from 'dynamic-pie-chart';
import { RisingSkylineModule, SkylineComponent } from 'rising-skyline';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { DashboardService } from '../service/dashboard/dashboard.service';
import { ProjectService } from '../service/project/project.service';
import { StaffListService } from '../service/staff-list-service/staff-list.service';
import { TagifyStarsComponent } from '../tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { FitzhiDashboardComponent } from './fitzhi-dashboard.component';
import { PieChartComponent } from './pie-chart/pie-chart.component';
import { PieProjectsComponent } from './pie-projects/pie-projects.component';
import { selection } from './selection';
import { SkylineService } from './skyline/service/skyline.service';
import { SkylineIconComponent } from './skyline/skyline-icon/skyline-icon.component';
import { TreemapSkillsChartComponent } from './treemap-skills/treemap-skills-chart/treemap-skills-chart.component';
import { TreemapSkillsComponent } from './treemap-skills/treemap-skills-container/treemap-skills.component';
import { TreemapHeaderComponent } from './treemap-skills/treemap-skills-header/treemap-skills-header.component';
import { MOCK_PROJECTS_DISTRIBUTION } from './mock-projects-distribution.spec';
import { MOCK_SKILLS_DISTRIBUTION } from './mock-skills-distribution.spec';
import { RouterTestingModule } from '@angular/router/testing';

describe('FitzhiDashboardComponent initialization', () => {
	let component: FitzhiDashboardComponent;
	let fixture: ComponentFixture<FitzhiDashboardComponent>;
	let skylineService: SkylineService;
	let projectService: ProjectService;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ FitzhiDashboardComponent, PieChartComponent, PieProjectsComponent,
				TagifyStarsComponent, SkylineIconComponent, SkylineComponent,
				TreemapSkillsChartComponent, TreemapHeaderComponent, TreemapSkillsChartComponent,
				TreemapSkillsComponent, SkylineComponent],
			imports: [MatTableModule, MatSortModule, MatPaginatorModule, HttpClientTestingModule,
				MatDialogModule, NgxChartsModule, BrowserAnimationsModule, MatCheckboxModule,
				DynamicPieChartModule, RisingSkylineModule, RouterTestingModule],
			providers: [ReferentialService, CinematicService, SkylineService, DatePipe]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(FitzhiDashboardComponent);
		component = fixture.componentInstance;
		skylineService = TestBed.inject(SkylineService);
		skylineService.skylineLoaded$.next(true);

		const staffListService = TestBed.inject(StaffListService);
		staffListService.informStaffLoaded();
		projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);

		const dashboardService = TestBed.inject(DashboardService);
		spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_SKILLS_DISTRIBUTION);
		spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_PROJECTS_DISTRIBUTION);

		fixture.detectChanges();
	});

	it('should be created without any error', () => {
		component.selected = selection.summary;
		fixture.detectChanges();
		expect(component).toBeTruthy();
		expect(document.getElementById('container-skyline')).toBeNull();
		expect(document.getElementById('host-treemap')).toBeNull();
		expect(document.getElementById('logo')).toBeDefined();
	});

	it('Skyline is set to be the selected pane', () => {
		component.selected = selection.skyline;
		fixture.detectChanges();
		expect(document.getElementById('container-skyline')).toBeDefined();
		expect(document.getElementById('host-treemap')).toBeNull();
		expect(document.getElementById('logo')).toBeNull();
	});

	it('The button for Skyline is clicked', fakeAsync(() => {
		const skyline = fixture.debugElement.query(By.css('#iconSkyline'));
		skyline.triggerEventHandler('click', null);
		tick(); // simulates the passage of time until all pending asynchronous activities finish
		fixture.detectChanges();
		expect(document.getElementById('container-skyline')).toBeDefined();
		expect(document.getElementById('host-treemap')).toBeNull();
		expect(document.getElementById('logo')).toBeNull();
	}));

	it('The method switchTo is invoked when the button for Skyline is clicked', fakeAsync(() => {
		expect(document.getElementById('container-skyline')).toBeNull();
		const onClickMock = spyOn(component, 'switchTo');
		fixture.debugElement.query(By.css('#iconSkyline')).triggerEventHandler('click', null);
		expect(onClickMock).toHaveBeenCalled();
	}));

	it('The button for Skyline is clicked, BUT THE SKYLINE IS NOT YET LOADED', fakeAsync(() => {
		skylineService.skylineLoaded$.next(false);
		const btn = fixture.debugElement.query(By.css('#iconSkyline'));
		btn.triggerEventHandler('click', null);
		tick();
		fixture.detectChanges();
		expect(document.getElementById('container-skyline')).toBeNull();
		expect(document.getElementById('host-treemap')).toBeNull();
		expect(document.getElementById('logo')).toBeDefined();
	}));

	it('The method switchTo is invoked when the button for TreeMap is clicked', fakeAsync(() => {
		const onClickMock = spyOn(component, 'switchTo');
		fixture.debugElement.query(By.css('#treeMap-skills')).triggerEventHandler('click', null);
		expect(onClickMock).toHaveBeenCalled();
	}));

	it('The button for Treemap is clicked, BUT ALL PROJECTS ARE NOT loaded', () => {
		projectService.allProjectsIsLoaded$.next(false);
		component.selected = selection.treeMapSkills;
		fixture.detectChanges();
		expect(document.getElementById('container-skyline')).toBeNull();
		expect(document.getElementById('container-treemap-skills')).toBeNull();
		expect(document.getElementById('logo')).toBeDefined();
	});

/*
	// https://blog.nrwl.io/controlling-time-with-zone-js-and-fakeasync-f0002dfbf48c
	it('The button for Treemap is clicked, AND ALL PROJECTS ARE LOADED', fakeAsync(() => {

		const spy1 = spyOn(pieDashboardService, 'generatePieSlices').and.returnValue();
		const spy2 = spyOn(dashboardService, 'processSkillDistribution').and.returnValue(distribution);

		component.selected = selection.treeMapSkills;
		fixture.detectChanges();

		setTimeout(() => {
			expect(document.getElementById('container-skyline')).toBeNull();
			expect(document.getElementById('container-treemap-skills')).toBeTruthy();
			expect(document.getElementById('logo')).toBeNull();
		}, 0);
	}));
*/

});
