import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
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
import { ReferentialService } from 'src/app/service/referential.service';
import { TagifyStarsComponent } from '../tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { FitzhiDashboardComponent } from './fitzhi-dashboard.component';
import { PieChartComponent } from './pie-chart/pie-chart.component';
import { PieProjectsComponent } from './pie-projects/pie-projects.component';
import { selection } from './selection';
import { SkylineService } from './skyline/service/skyline.service';
import { SkylineIconComponent } from './skyline/skyline-icon/skyline-icon.component';
import { TreemapChartComponent } from './treemap/treemap-chart/treemap-chart.component';
import { TreemapComponent } from './treemap/treemap-container/treemap.component';
import { TreemapHeaderComponent } from './treemap/treemap-header/treemap-header.component';


describe('FitzhiDashboardComponent initialization', () => {
	let component: FitzhiDashboardComponent;
	let fixture: ComponentFixture<FitzhiDashboardComponent>;
	let skylineService: SkylineService;

	const distribution =  [
		{
			name: 'java',
			value: '50'
		},
		{
			name: '.Net',
			value: '20'
		},
		{
			name: 'Typescript',
			value: '30'
		}
	];

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ FitzhiDashboardComponent, PieChartComponent, PieProjectsComponent,
				TagifyStarsComponent, SkylineIconComponent, SkylineComponent,
				TreemapChartComponent, TreemapHeaderComponent, TreemapChartComponent,
				TreemapComponent, SkylineComponent],
			imports: [MatTableModule, MatSortModule, MatPaginatorModule, HttpClientTestingModule,
				MatDialogModule, NgxChartsModule, BrowserAnimationsModule, MatCheckboxModule,
				DynamicPieChartModule, RisingSkylineModule],
			providers: [ReferentialService, CinematicService, SkylineService, DatePipe]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(FitzhiDashboardComponent);
		component = fixture.componentInstance;
		skylineService = TestBed.inject(SkylineService);
		skylineService.skylineLoaded$.next(true);
		fixture.detectChanges();
	});

	it('should be created without any error', () => {
		component.selected = selection.none;
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
		let skyline = fixture.debugElement.query(By.css('#iconSkyline'));
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
		let btn = fixture.debugElement.query(By.css('#iconSkyline'));
		btn.triggerEventHandler('click', null);
		tick(); 
		fixture.detectChanges();
		expect(document.getElementById('container-skyline')).toBeNull();
		expect(document.getElementById('host-treemap')).toBeNull();
		expect(document.getElementById('logo')).toBeDefined();		
	}));

	it('The method switchTo is invoked when the button for TreeMap is clicked', fakeAsync(() => {
		const onClickMock = spyOn(component, 'switchTo');
		fixture.debugElement.query(By.css('#treeMap')).triggerEventHandler('click', null);
		expect(onClickMock).toHaveBeenCalled();
	}));

	it('The button for Treemap is clicked, BUT ALL PROJECTS ARE NOT loaded', fakeAsync(() => {
		component.selected = selection.treeMap;
		fixture.detectChanges();
		expect(document.getElementById('container-skyline')).toBeNull();
		expect(document.getElementById('container-treemap')).toBeNull();
		expect(document.getElementById('logo')).toBeDefined();		
	}));

/*
	// https://blog.nrwl.io/controlling-time-with-zone-js-and-fakeasync-f0002dfbf48c
	it('The button for Treemap is clicked, AND ALL PROJECTS ARE LOADED', fakeAsync(() => {

		const spy1 = spyOn(pieDashboardService, 'generatePieSlices').and.returnValue();
		const spy2 = spyOn(dashboardService, 'processSkillDistribution').and.returnValue(distribution);

		component.selected = selection.treeMap;
		fixture.detectChanges();
		
		setTimeout(() => {
			expect(document.getElementById('container-skyline')).toBeNull();
			expect(document.getElementById('container-treemap')).toBeTruthy();
			expect(document.getElementById('logo')).toBeNull();				
		}, 0);
	}));
*/

});
