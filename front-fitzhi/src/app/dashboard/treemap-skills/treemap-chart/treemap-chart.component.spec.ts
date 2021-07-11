import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreemapChartComponent } from './treemap-chart.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';

describe('TreemapComponent', () => {
	let component: TreemapChartComponent;
	let fixture: ComponentFixture<TreemapChartComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [NgxChartsModule, BrowserAnimationsModule, HttpClientTestingModule, MatDialogModule],
			declarations: [ TreemapChartComponent ],
			providers: [DashboardService, ProjectService, ReferentialService, CinematicService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapChartComponent);
		component = fixture.componentInstance;
		component.distribution =  [
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
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
