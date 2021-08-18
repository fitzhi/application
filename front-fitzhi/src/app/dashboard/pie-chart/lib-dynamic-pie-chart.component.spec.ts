import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DynamicPieChartModule, Slice } from 'dynamic-pie-chart';
import { BehaviorSubject } from 'rxjs';
import { AnalysisTypeSlice } from '../analysis-type-slice';
import { LevelStaffRisk } from '../level-staff-risk';


describe('lib-dynamic-pie-chart (import)', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `<lib-dynamic-pie-chart
						[pie]=1
						[radius]=100
						[active]=false
						[slices$]=slices$>
					</lib-dynamic-pie-chart>`})

	class TestHostComponent {
		public slices$ = new BehaviorSubject<Slice[]>([]);
		constructor() {
		}
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ TestHostComponent ],
			imports: [DynamicPieChartModule],
			providers: []
		})
		.compileComponents();

	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		component.slices$.next([
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

		fixture.detectChanges();
	});

	it('should the imported dynamic pie chart.', done => {
		expect(component).toBeTruthy();
		expect(fixture.debugElement.query(By.css('#pieSlice-1-0'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#pieSlice-1-1'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#pieSlice-1-2'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#pieSlice-1-3'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#pieSlice-1-4'))).toBeNull();
		done();
	});
});
