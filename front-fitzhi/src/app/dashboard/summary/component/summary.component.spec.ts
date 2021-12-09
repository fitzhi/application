import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { NgxPopper } from 'angular-popper';
import { RiskLegend } from 'src/app/data/riskLegend';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { AuditGraphicBadgeComponent } from 'src/app/tabs-project/project-audit/project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { TreemapProjectsChartComponent } from '../../treemap-projects/treemap-projects-chart/treemap-projects-chart.component';
import { TreemapProjectsContainerComponent } from '../../treemap-projects/treemap-projects-container/treemap-projects-container.component';
import { TreemapProjectsService } from '../../treemap-projects/treemap-projects-service/treemap-projects.service';
import { TreemapSkillsChartComponent } from '../../treemap-skills/treemap-skills-chart/treemap-skills-chart.component';
import { SummaryService } from '../service/summary.service';
import { SummaryComponent } from './summary.component';
import { MOCK_PROJECTS_DISTRIBUTION } from '../../mock-projects-distribution.spec';
import { MOCK_SKILLS_DISTRIBUTION } from '../../mock-skills-distribution.spec';
import { selection } from '../../selection';
import { environment } from 'src/environments/environment';
import { TurnoverService } from 'src/app/service/turnover/turnover.service';
import { TurnoverData } from 'src/app/service/turnover/turnover-data';


describe('SummaryComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let dashboardService: DashboardService;
	let projectService: ProjectService;
	let staffListService: StaffListService;
	let service: SummaryService;
	let turnoverService: TurnoverService;

	@Component({
		selector: 'app-test-host-component',
		template: `	<div style="width: 800px; height: 800px; top: 0; bottom: 0; left: 0; right: 0; position: fixed; background-color: transparent;">
						<app-summary (messengerSelectedSummary)="switchTo($event)"></app-summary>
					</div>`
	})
	class TestHostComponent {
		@ViewChild(SummaryComponent) summary: SummaryComponent;

		switchTo(sel: number) {
			console.log ('Received selection', sel);
		}
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ SummaryComponent, TestHostComponent, AuditGraphicBadgeComponent,
				TreemapProjectsContainerComponent, TreemapProjectsChartComponent,
				TreemapProjectsChartComponent, TreemapSkillsChartComponent ],
			providers: [ SummaryService, DashboardService, ReferentialService, CinematicService,
				TreemapProjectsService, ProjectService, StaffListService, TurnoverService],
			imports: [ HttpClientTestingModule, MatDialogModule, RouterTestingModule, NgxChartsModule,
				BrowserAnimationsModule, NgxPopper]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		dashboardService = TestBed.inject(DashboardService);

		service = TestBed.inject(SummaryService);

		const referentialService = TestBed.inject(ReferentialService);
		referentialService.legends.push(new RiskLegend(4, 'green'));
		referentialService.referentialLoaded$.next(true);

		projectService = TestBed.inject(ProjectService);
		projectService.allProjectsIsLoaded$.next(true);

		
		turnoverService = TestBed.inject(TurnoverService);
		spyOn(turnoverService, 'turnover').and.callFake (year => { return new TurnoverData(year, 1, 1, 1, year - 2000); });
		
		staffListService = TestBed.inject(StaffListService);
		staffListService.informStaffLoaded();

		// By default, we are in autoConnect OFF.
		environment.autoConnect = false;

		fixture.detectChanges();

	});

	function loadTheCharts(generalAverage: number) {
		spyOn(dashboardService, 'processProjectsDistribution').and.returnValue(MOCK_PROJECTS_DISTRIBUTION);
		spyOn(dashboardService, 'processSkillDistribution').and.returnValue(MOCK_SKILLS_DISTRIBUTION);
		const spy = spyOn(dashboardService, 'calculateGeneralAverage').and.returnValue(generalAverage);
		service.showGeneralAverage();
		fixture.detectChanges();
		return spy;
	}

	it('should display the main logo at startup.', () => {
		expect(component).toBeTruthy();
		expect(fixture.debugElement.query(By.css('#logo'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#summaries'))).toBeNull();
		expect(fixture.debugElement.query(By.css('#small-logo'))).toBeNull();
	});

	it('show the small logo when the first summary is loaded.', done => {

		const spy = loadTheCharts(1);

		expect(spy).toHaveBeenCalled();
		service.summary$.subscribe({
			next: sum => {
				expect(fixture.debugElement.query(By.css('#logo'))).toBeNull();
				expect(fixture.debugElement.query(By.css('#summaries'))).toBeDefined();
				expect(fixture.debugElement.query(By.css('#small-logo'))).toBeDefined();
				expect(fixture.debugElement.query(By.css('#invitation'))).toBeNull();
				done();
			}
		});
	});

	it('should display the contact invitation panel if the environment is set to autoConnect (fitzhi.com release)', done => {

		// we switch to the autoConnect mode.
		environment.autoConnect = true;

		const spy = loadTheCharts(1);
		expect(spy).toHaveBeenCalled();
		service.summary$.subscribe({
			next: sum => {
				expect(fixture.debugElement.query(By.css('#logo'))).toBeNull();
				expect(fixture.debugElement.query(By.css('#summaries'))).toBeDefined();
				expect(fixture.debugElement.query(By.css('#small-logo'))).toBeDefined();
				expect(fixture.debugElement.query(By.css('#invitation'))).toBeDefined();
				done();
			}
		});
	});

	it('should display the general average badge.', done => {
		const treemapProjectsService  = TestBed.inject(TreemapProjectsService);
		treemapProjectsService.informSelectedProjects([1, 2, 3]);
		staffListService.informStaffLoaded();
		projectService.allProjectsIsLoaded$.next(true);

		const spy = loadTheCharts(6);

		service.summary$.subscribe({
			next: sum => {
				expect(fixture.debugElement.query(By.css('#general-average'))).toBeDefined();
				expect(spy).toHaveBeenCalled();
				done();
			}
		});
	});

	it('should display an help-popup when the mouse moves over the widget.', done => {

		expect(fixture.debugElement.query(By.css('#help-general-average'))).toBeNull();

		loadTheCharts(3);

		service.summary$.subscribe({
			next: sum => {
				const score = fixture.debugElement.query(By.css('#general-average')).nativeElement;
				score.dispatchEvent(new Event('mouseenter'));
				fixture.detectChanges();
				expect(fixture.debugElement.query(By.css('#help-general-average'))).toBeDefined();
				done();
			}
		});
	});

	it('should display an help-popup when the methoid hasGeneralAverage() returns TRUE.', () => {
		let help = fixture.debugElement.query(By.css('#help-general-average'));
		expect(help).toBeNull();

		spyOn(component.summary, 'hasGeneralAverage').and.returnValue(true);
		help = fixture.debugElement.query(By.css('#help-general-average'));

		expect(help).toBeDefined();
	});


	it('should switch to the skills coverage chart if the user clicks on its thumbnail.', done => {

		loadTheCharts(3);

		const spy = spyOn(component.summary, 'switchTo').and.callThrough();
		const spyComponentParent = spyOn(component, 'switchTo').withArgs(selection.treeMapSkills).and.callThrough();

		service.summary$.subscribe({
			next: sum => {
				const thumbnail = fixture.debugElement.query(By.css('#thumbnail-treeMapSkills'));
				thumbnail.triggerEventHandler('click', null);
				fixture.detectChanges();
				expect(spy).toHaveBeenCalled();
				expect(spyComponentParent).toHaveBeenCalled();
				done();
			}
		});
	});

	it('should switch to the treemap projects chart if the user clicks on its thumbnail.', done => {

		loadTheCharts(8);

		const spy = spyOn(component.summary, 'switchTo').and.callThrough();
		const spyComponentParent = spyOn(component, 'switchTo').withArgs(selection.treeMapProjects).and.callThrough();

		service.summary$.subscribe({
			next: sum => {
				const thumbnail = fixture.debugElement.query(By.css('#thumbnail-treeMapProjects'));
				thumbnail.triggerEventHandler('click', null);
				fixture.detectChanges();
				expect(spy).toHaveBeenCalled();
				expect(spyComponentParent).toHaveBeenCalled();
				done();
			}
		});
	});


	it('should display the 3 turnover panels.', done => {

		const currentYear = new Date(Date.now()).getFullYear();
		
		let thumbnailTurnoverCurrentYear = fixture.debugElement.query(By.css('#thumbnail-turnover-' + currentYear));
		let thumbnailTurnoverLastYear = fixture.debugElement.query(By.css('#thumbnail-turnover-' + (currentYear-1)));
		let thumbnailTurnoverPenultimateYear = fixture.debugElement.query(By.css('#thumbnail-turnover-' + (currentYear-2)));
		
		expect(thumbnailTurnoverCurrentYear).toBeNull();
		expect(thumbnailTurnoverLastYear).toBeNull();
		expect(thumbnailTurnoverPenultimateYear).toBeNull();
		
		loadTheCharts(8);
		
		setTimeout(() => {
			staffListService.allStaffLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						expect(fixture.debugElement.query(By.css('#thumbnail-turnover-' + currentYear))).not.toBeNull();
						expect(fixture.debugElement.query(By.css('#thumbnail-turnover-' + (currentYear-1)))).not.toBeNull();
						expect(fixture.debugElement.query(By.css('#thumbnail-turnover-' + (currentYear-2)))).not.toBeNull();
						done();
					} 
				}
			});			
		}, 0);

	});


	it('should write the turnover in the turnover panels.', done => {

		const currentYear = new Date(Date.now()).getFullYear();
		let turnoverCurrentYear = fixture.debugElement.query(By.css('#turnover-' + currentYear));
		let turnoverLastYear = fixture.debugElement.query(By.css('#turnover-' + (currentYear-1)));
		let turnoverPenultimateYear = fixture.debugElement.query(By.css('#turnover-' + (currentYear-2)));

		expect(turnoverCurrentYear).toBeNull();
		expect(turnoverLastYear).toBeNull();
		expect(turnoverPenultimateYear).toBeNull();

		loadTheCharts(6);

		staffListService.allStaffLoaded$.subscribe({
			next: doneAndOk => {
				if (doneAndOk) {
					expect(fixture.debugElement.query(By.css('#turnover-' + currentYear)).nativeNode.innerText).toBe('21');
					expect(fixture.debugElement.query(By.css('#turnover-' + (currentYear-1))).nativeNode.innerText).toBe('20');
					expect(fixture.debugElement.query(By.css('#turnover-' + (currentYear-2))).nativeNode.innerText).toBe('19');
					done();
				} 
			}
		});
	});

});
