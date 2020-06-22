import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PieChartComponent } from './pie-chart.component';
import { MatTableModule } from '@angular/material/table';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Component } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { TypeSlice } from '../type-slice';
import { LevelStaffRisk } from '../level-staff-risk';

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
														[filteredSlice]=3
														[active]=false>
													</app-pie-chart>
											</div>
										</td>
										<td>
											<div style="width:300px;height:300px;background-color:transparent">
												<app-pie-chart
													[radius]=100
													[pie]=3
													[active]=false>
												</app-pie-chart>
											</div>
										</td>
									</tr>
								</table>`
	})
	class TestHostComponent {
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ PieChartComponent, TestHostComponent ],
			imports: [MatTableModule, HttpClientTestingModule, RouterTestingModule, MatDialogModule],
			providers: [ReferentialService, CinematicService, PieDashboardService]
		})
		.compileComponents();

		const pieDashboardService = TestBed.inject(PieDashboardService);
		pieDashboardService.slices$.next(
			[
				{
					id: 0,
					type: TypeSlice.Sonar,
					angle: 45,
					levelStaffRisk: LevelStaffRisk.low,
					color: 'green',
					offset: 0,
					activated: false,
					selected: false,
					projects: []
				},
				{
					id: 1,
					type: TypeSlice.Sonar,
					angle: 20,
					levelStaffRisk: LevelStaffRisk.medium,
					color: 'orange',
					offset: 45,
					activated: false,
					selected: false,
					projects: []
				},
				{
					id: 2,
					type: TypeSlice.Sonar,
					angle: 10,
					levelStaffRisk: LevelStaffRisk.high,
					color: 'red',
					offset: 65,
					activated: false,
					selected: false,
					projects: []
				},
				{
					id: 3,
					type: TypeSlice.Sonar,
					angle: 99,
					levelStaffRisk: LevelStaffRisk.low,
					color: 'blue',
					offset: 75,
					activated: false,
					selected: false,
					projects: []
				}
			]);
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create & display the Pie chart', () => {

		expect(component).toBeTruthy();
	});
});
