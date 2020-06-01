import { async, ComponentFixture, TestBed } from '@angular/core/testing';

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
import { TagifyStarsComponent } from './../../tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { TreemapComponent } from './treemap/treemap-container/treemap.component';
import { CinematicService } from 'src/app/service/cinematic.service';

describe('FitzhiDashboardComponent', () => {
	let component: FitzhiDashboardComponent;
	let fixture: ComponentFixture<FitzhiDashboardComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ FitzhiDashboardComponent, PieChartComponent, PieProjectsComponent, TagifyStarsComponent,
				TreemapChartComponent, TreemapHeaderComponent, TreemapChartComponent, TreemapComponent ],
			imports: [MatTableModule, MatSortModule, MatPaginatorModule, HttpClientTestingModule, MatDialogModule,
				NgxChartsModule, BrowserAnimationsModule, MatCheckboxModule],
			providers: [ReferentialService, CinematicService]

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(FitzhiDashboardComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should be created without any error', () => {
		expect(component).toBeTruthy();
	});
});
