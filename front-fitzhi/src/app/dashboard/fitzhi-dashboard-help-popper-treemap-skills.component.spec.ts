import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { NgxPopper } from 'angular-popper';
import { ControlledRisingSkylineService } from 'controlled-rising-skyline';
import { DynamicPieChartModule } from 'dynamic-pie-chart';
import { RisingSkylineService } from 'rising-skyline';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { ProjectService } from '../service/project/project.service';
import { StaffListService } from '../service/staff-list-service/staff-list.service';
import { TagifyStarsComponent } from '../tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { FitzhiDashboardComponent } from './fitzhi-dashboard.component';
import { PieChartComponent } from './pie-chart/pie-chart.component';
import { PieLegendComponent } from './pie-legend/pie-legend.component';
import { PieProjectsComponent } from './pie-projects/pie-projects.component';
import { selection } from './selection';
import { PieDashboardService } from './service/pie-dashboard.service';
import { SkylineComponent } from './skyline/component/skyline.component';
import { SkylineIconComponent } from './skyline/skyline-icon/skyline-icon.component';
import { SummaryComponent } from './summary/component/summary.component';
import { TreemapProjectsChartComponent } from './treemap-projects/treemap-projects-chart/treemap-projects-chart.component';
import { TreemapProjectsContainerComponent } from './treemap-projects/treemap-projects-container/treemap-projects-container.component';
import { TreemapSkillsChartComponent } from './treemap-skills/treemap-skills-chart/treemap-skills-chart.component';
import { TreemapSkillsComponent } from './treemap-skills/treemap-skills-container/treemap-skills.component';
import { TreemapHeaderComponent } from './treemap-skills/treemap-skills-header/treemap-skills-header.component';


describe('FitzhiDashboardComponent', () => {
	let component: FitzhiDashboardComponent;
	let fixture: ComponentFixture<FitzhiDashboardComponent>;
	let referentialService: ReferentialService;
	let projectService: ProjectService;
	let staffListService: StaffListService;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ FitzhiDashboardComponent, PieChartComponent, PieProjectsComponent, TagifyStarsComponent,
				TreemapSkillsChartComponent, TreemapHeaderComponent, TreemapSkillsChartComponent, TreemapSkillsComponent, PieLegendComponent,
				SkylineComponent, SkylineIconComponent, TreemapProjectsContainerComponent, TreemapProjectsChartComponent,
				SummaryComponent ],
			imports: [MatTableModule, MatSortModule, MatPaginatorModule, HttpClientTestingModule, MatDialogModule,
				NgxChartsModule, BrowserAnimationsModule, MatCheckboxModule, RouterTestingModule, DynamicPieChartModule,
				NgxPopper],
			providers: [ReferentialService, CinematicService, ControlledRisingSkylineService, RisingSkylineService,
				ProjectService, StaffListService, DatePipe]

		})
		.compileComponents();

	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(FitzhiDashboardComponent);
		component = fixture.componentInstance;
		component.selected = selection.summary;
		component.popupHelper.mouseEnter(selection.treeMapSkills);
		referentialService = TestBed.inject(ReferentialService);
		referentialService.optimalStaffNumberPerMoOfCode = [];
		referentialService.optimalStaffNumberPerMoOfCode.push(1515);
		referentialService.optimalStaffNumberPerMoOfCode.push(1789);
		referentialService.optimalStaffNumberPerMoOfCode.push(1805);
		referentialService.optimalStaffNumberPerMoOfCode.push(1815);
		referentialService.optimalStaffNumberPerMoOfCode.push(1871);
		referentialService.referentialLoaded$.next(true);
		fixture.detectChanges();
	});

	it('should display the help popup message with the optimal number of developers loaded from server.', () => {
		expect(component).toBeTruthy();
		expect(document.getElementById('table-settings')).not.toBeNull();
		expect(document.getElementById('setting-0')).toBeDefined();
		expect(document.getElementById('setting-0').innerText).toBe('1515');
		expect(document.getElementById('setting-1').innerText).toBe('1789');
		expect(document.getElementById('setting-2').innerText).toBe('1805');
		expect(document.getElementById('setting-3').innerText).toBe('1815');
		expect(document.getElementById('setting-4').innerText).toBe('1871');

	});

	it('should not display the table inside the popup if the referential are not yet loaded.', done => {
		referentialService.referentialLoaded$.next(false);
		fixture.detectChanges();
		expect(document.getElementById('table-settings')).toBeNull();
		expect(document.getElementById('setting-0')).toBeNull();
		done();
	});

});
