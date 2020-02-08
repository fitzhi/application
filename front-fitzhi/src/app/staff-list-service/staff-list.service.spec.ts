import { TestBed, inject, TestModuleMetadata, async } from '@angular/core/testing';

import { StaffListService } from './staff-list.service';
import { InitTest } from '../test/init-test';

describe('ListStaffService', () => {

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	it('should be created', inject([StaffListService], (service: StaffListService) => {
		expect(service).toBeTruthy();
	}));

});


