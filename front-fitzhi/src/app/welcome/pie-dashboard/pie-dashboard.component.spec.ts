import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PieDashboardComponent } from './pie-dashboard.component';
import { PieChartComponent } from './pie-chart/pie-chart.component';
import { PieProjectsComponent } from './pie-projects/pie-projects.component';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatDialogModule } from '@angular/material/dialog';
import { TreemapComponent } from './treemap/treemap.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('PieDashboardComponent', () => {
	let component: PieDashboardComponent;
	let fixture: ComponentFixture<PieDashboardComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ PieDashboardComponent, PieChartComponent, PieProjectsComponent, TreemapComponent ],
			imports: [MatTableModule, MatSortModule, MatPaginatorModule, HttpClientTestingModule, MatDialogModule, 
				NgxChartsModule, BrowserAnimationsModule],
			providers: [ReferentialService]

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(PieDashboardComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should be created without any error', () => {
		expect(component).toBeTruthy();
	});
});
