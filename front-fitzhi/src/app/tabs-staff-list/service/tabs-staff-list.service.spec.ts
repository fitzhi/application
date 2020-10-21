import { TestBed, TestModuleMetadata } from '@angular/core/testing';

import { TabsStaffListService } from './tabs-staff-list.service';
import { InitTest } from 'src/app/test/init-test';

describe('TabsStaffListService', () => {

	beforeEach(() => {
		const testConf: TestModuleMetadata =  {
				declarations: [],
				providers: [],
				imports: []
			};
			InitTest.addImports(testConf.imports);
			InitTest.addProviders(testConf.providers);
			TestBed.configureTestingModule(testConf).compileComponents();
	});

	it('should be created', () => {
		const service: TabsStaffListService = TestBed.inject(TabsStaffListService);
		expect(service).toBeTruthy();
	});
});
