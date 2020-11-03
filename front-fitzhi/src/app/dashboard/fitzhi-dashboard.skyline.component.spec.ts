import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { FitzhiDashboardComponent } from './fitzhi-dashboard.component';
import { PieChartComponent } from './pie-chart/pie-chart.component';
import { PieProjectsComponent } from './pie-projects/pie-projects.component';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatDialogModule } from '@angular/material/dialog';
import { TreemapChartComponent } from './treemap/treemap-chart/treemap-chart.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TreemapHeaderComponent } from './treemap/treemap-header/treemap-header.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { TagifyStarsComponent } from '../tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { TreemapComponent } from './treemap/treemap-container/treemap.component';
import { CinematicService } from 'src/app/service/cinematic.service';
import { selection } from './selection';
import { By } from '@angular/platform-browser';
import { SkylineService } from './skyline/service/skyline.service';


describe('FitzhiDashboardComponent initialization', () => {
	let component: FitzhiDashboardComponent;
	let fixture: ComponentFixture<FitzhiDashboardComponent>;
	let skylineService: SkylineService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ FitzhiDashboardComponent, PieChartComponent, PieProjectsComponent, TagifyStarsComponent,
				TreemapChartComponent, TreemapHeaderComponent, TreemapChartComponent, TreemapComponent ],
			imports: [MatTableModule, MatSortModule, MatPaginatorModule, HttpClientTestingModule, MatDialogModule,
				NgxChartsModule, BrowserAnimationsModule, MatCheckboxModule],
			providers: [ReferentialService, CinematicService, SkylineService]

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
		let btn = fixture.debugElement.query(By.css('#skyline'));
		btn.triggerEventHandler('click', null);
		tick(); // simulates the passage of time until all pending asynchronous activities finish
		fixture.detectChanges();
		expect(document.getElementById('container-skyline')).toBeDefined();
		expect(document.getElementById('host-treemap')).toBeNull();
		expect(document.getElementById('logo')).toBeNull();		
	}));

	it('The method switchTo is invoked when the button for Skyline is clicked', fakeAsync(() => {
		expect(document.getElementById('container-skyline')).toBeNull();
		const onClickMock = spyOn(component, 'switchTo');
		fixture.debugElement.query(By.css('#skyline')).triggerEventHandler('click', null);
		expect(onClickMock).toHaveBeenCalled();
	}));

	it('The button for Skyline is clicked, BUT THE SKYLINE IS NOT YET LOADED', fakeAsync(() => {
		skylineService.skylineLoaded$.next(false);
		let btn = fixture.debugElement.query(By.css('#skyline'));
		btn.triggerEventHandler('click', null);
		tick(); 
		fixture.detectChanges();
		expect(document.getElementById('container-skyline')).toBeNull();
		expect(document.getElementById('host-treemap')).toBeNull();
		expect(document.getElementById('logo')).toBeDefined();		
	}));
	
});
