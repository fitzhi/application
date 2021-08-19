import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { doesNotReject } from 'assert';
import { DynamicPieChartModule } from 'dynamic-pie-chart';
import { take } from 'rxjs/operators';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { AnalysisTypeSlice } from '../analysis-type-slice';
import { LevelStaffRisk } from '../level-staff-risk';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { PieChartComponent } from './pie-chart.component';


describe('PieChartComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;


	@Component({
		selector: 'app-host-component',
		template: `<div style="width: 300px; height:300px; backgroundColor: transparent">
						<app-pie-chart
							[debug]=true
							[radius]=150
							[pie]=3
							[filteredId]=-1
							[active]=false>
						</app-pie-chart>
					</div>`
	})
	class TestHostComponent {

		constructor(pieDashboardService: PieDashboardService) {
			pieDashboardService.slices$.next(
				[
					{
						id: 0,
						type: AnalysisTypeSlice.Sonar,
						angle: 45,
						backgroundColor: 'green',
						textColor: 'black',
						textFontSize: '16px',
						offset: 0,
						activated: false,
						selected: false,
						children: [],
						data: LevelStaffRisk.low
					}
				]
			);
		}
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ PieChartComponent, TestHostComponent ],
			imports:  [DynamicPieChartModule, MatDialogModule, HttpClientTestingModule],
			providers: [ProjectService, ReferentialService, CinematicService, PieDashboardService]
		})
		.compileComponents();

	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		const pieDashboardService = TestBed.inject(PieDashboardService);
		setTimeout(() => {
			pieDashboardService.slices$.next(
				[
					{
						id: 0,
						type: AnalysisTypeSlice.Sonar,
						angle: 45,
						backgroundColor: 'green',
						textColor: 'black',
						textFontSize: '16px',
						offset: 0,
						activated: false,
						selected: false,
						children: [],
						data: LevelStaffRisk.low
					},
					{
						id: 1,
						type: AnalysisTypeSlice.Sonar,
						angle: 20,
						backgroundColor: 'orange',
						textColor: 'black',
						textFontSize: '16px',
						offset: 45,
						activated: false,
						selected: false,
						children: [],
						data: LevelStaffRisk.medium
					},
					{
						id: 2,
						type: AnalysisTypeSlice.Sonar,
						angle: 10,
						backgroundColor: 'red',
						textColor: 'black',
						textFontSize: '16px',
						offset: 65,
						activated: false,
						selected: false,
						children: [],
						data: LevelStaffRisk.high
					},
					{
						id: 3,
						type: AnalysisTypeSlice.Sonar,
						angle: 99,
						backgroundColor: 'blue',
						textColor: 'black',
						textFontSize: '16px',
						offset: 75,
						activated: false,
						selected: false,
						children: [],
						data: LevelStaffRisk.low
					}
				]
			);
			fixture.detectChanges();
		}, 100);
	});

	it('should create & display the component pie chart.', done => {
		expect(component).toBeTruthy();
		done();
	});
});
