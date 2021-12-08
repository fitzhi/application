import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { assert } from 'console';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { StaffListService } from '../staff-list-service/staff-list.service';
import { TurnoverData } from './turnover-data';

import { TurnoverService } from './turnover.service';

describe('TurnoverService', () => {
	let turnoverService: TurnoverService;
	let staffListService: StaffListService;

	beforeEach(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [StaffService],
			imports: [MatDialogModule, HttpClientTestingModule]
		};
		TestBed.configureTestingModule(testConf);
		turnoverService = TestBed.inject(TurnoverService);
		staffListService = TestBed.inject(StaffListService);
	});

	it('should calculate a simple turnover for just ONE staff member with a small piece of real-production data.', () => {
		expect(turnoverService).toBeTruthy();
		const staff = require('./staff-test-from-prod.json');
		staff.forEach(st => {
			const collaborator = staffListService.createStaffFromJson(st);
			staffListService.allStaff.push(collaborator);
		});

		expect(staffListService.allStaff.length).toBe(2);
		expect(turnoverService.turnover(2021, true).calculation).toBe(0);
	});

	it('should calculate correctly the turnover for year 2021 with the testing data "staff-test-1".', () => {
		const staff = require('./staff-test-1.json');
		staff.forEach(st => {
			const collaborator = staffListService.createStaffFromJson(st);
			staffListService.allStaff.push(collaborator);
		});
		expect(staffListService.allStaff.length).toBe(3);
		expect(turnoverService.turnover(2021).calculation).toBe(50);
	});

	it('should calculate correctly the turnover for year 2020 with the testing data "staff-test-1".', () => {
		const staff = require('./staff-test-1.json');
		staffListService.loadAllStaff(staff);
		expect(turnoverService.turnover(2020).calculation).toBe(0);
	});

	it('should calculate correctly the turnover for year 2019 with the testing data "staff-test-1".', () => {
		const staff = require('./staff-test-1.json');
		staffListService.loadAllStaff(staff);
		expect(turnoverService.turnover(2019).calculation).toBe(25);
	});

	it('should take in account that a staff member is no more active with the testing data "staff-test-2".', () => {
		const staff = require('./staff-test-2.json');
		staffListService.loadAllStaff(staff);
		expect(turnoverService.turnover(2021).calculation).toBe(50);
	});

	it('should handle correctly the lack of data".', () => {
		const staff = require('./staff-test-2.json');
		staffListService.loadAllStaff(staff);
		expect(turnoverService.turnover(1789).calculation).toBe(TurnoverData.NO_DATA_AVAILABLE);
	});

});
