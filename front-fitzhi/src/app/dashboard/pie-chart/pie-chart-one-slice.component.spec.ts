import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PieChartComponent } from './pie-chart.component';
import { MatTableModule } from '@angular/material/table';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Component } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { AnalysisTypeSlice } from '../analysis-type-slice';
import { LevelStaffRisk } from '../level-staff-risk';
import { DynamicPieChartModule } from 'dynamic-pie-chart';

describe('PieChartComponent with only One Slice', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `<table>
						<tr>
							<td>
								<div style="width:400px;height:400px;background-color:whiteSmoke">
										<app-pie-chart
											[radius]=150
											[pie]=7
											[filteredId]=3
											[active]=true>
										</app-pie-chart>
								</div>
							</td>
							<td>
								<div style="width:300px;height:300px;background-color:transparent">
									<app-pie-chart
										[radius]=100
										[pie]=3
										[active]=true>
									</app-pie-chart>
								</div>
							</td>
						</tr>
					</table>`
	})
	class TestHostComponent {
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ PieChartComponent, TestHostComponent ],
			imports: [MatTableModule, HttpClientTestingModule, RouterTestingModule, MatDialogModule, DynamicPieChartModule],
			providers: [ReferentialService, CinematicService, PieDashboardService]
		})
		.compileComponents();

		const pieDashboardService = TestBed.inject(PieDashboardService);
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
					data: LevelStaffRisk.low,
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
					data: LevelStaffRisk.medium,
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
					data: LevelStaffRisk.high,
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
					data: LevelStaffRisk.undefined,
				}
			]);
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create & display the Pie chart', done => {
		expect(component).toBeTruthy();
		done();
	});
});
