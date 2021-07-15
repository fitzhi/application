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
import { TreemapSkillsChartComponent } from './treemap-skills/treemap-skills-chart/treemap-skills-chart.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TreemapHeaderComponent } from './treemap-skills/treemap-skills-header/treemap-skills-header.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { TagifyStarsComponent } from '../tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { TreemapSkillsComponent } from './treemap-skills/treemap-skills-container/treemap-skills.component';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DatePipe } from '@angular/common';

describe('FitzhiDashboardComponent', () => {
	let component: FitzhiDashboardComponent;
	let fixture: ComponentFixture<FitzhiDashboardComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ FitzhiDashboardComponent, PieChartComponent, PieProjectsComponent, TagifyStarsComponent,
				TreemapSkillsChartComponent, TreemapHeaderComponent, TreemapSkillsChartComponent, TreemapSkillsComponent ],
			imports: [MatTableModule, MatSortModule, MatPaginatorModule, HttpClientTestingModule, MatDialogModule,
				NgxChartsModule, BrowserAnimationsModule, MatCheckboxModule],
			providers: [ReferentialService, CinematicService, DatePipe]

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
