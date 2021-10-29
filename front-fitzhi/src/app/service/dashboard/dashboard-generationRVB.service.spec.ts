import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { DashboardColor } from './dashboard-color';

describe('DashboardService', () => {

	beforeEach(async () => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [],
			imports: []
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {
		TestBed.configureTestingModule({});
	});

	it('should correctly evaluate the level of RED color in a scale of "10" (the default value) for the index 0.', () => {
		expect (DashboardColor.red(0)).toBe('28');
	});

	it('should correctly evaluate the level of RED color in a scale of "10" (the default value) for the index 1.', () => {
		expect (DashboardColor.red(1)).toBe('32');
	});

	it('should correctly evaluate the level of RED color in a scale of "10" (the default value) for the index 2.', () => {
		expect (DashboardColor.red(2)).toBe('3C');
	});

	it('should correctly evaluate the level of RED color in a scale of "100" for the index 1.', () => {
		expect (DashboardColor.red(1, 100)).toBe('29');
	});

	it('should correctly evaluate the level of RED color in a scale of "100" for the index 2.', () => {
		expect (DashboardColor.red(2, 100)).toBe('2A');
	});


	it('should produce the application ("--color-selected: #28A745") color for the index 0.', () => {

		// --color-success: #28A745; rgb(40,167,69)
		// --color-error: darkred; #8B0000	rgb(139,0,0)

		expect (DashboardColor.rgb(0)).toBe('#28A745');
	});

	it('should produce the application ("--color-error: darkred; #8B0000") color for the index scale 10 (= scale).', () => {

		// --color-success: #28A745; rgb(40,167,69)
		// --color-error: darkred; #8B0000	rgb(139,0,0)

		const red = DashboardColor.red(10);
		const green = DashboardColor.green(10);
		const blue = DashboardColor.blue(10);
		const color = '#' + red + green + blue;
		expect (color).toBe('#8B0000');
	});

});
