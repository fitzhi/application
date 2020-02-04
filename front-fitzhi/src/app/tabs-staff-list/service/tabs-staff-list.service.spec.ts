import { TestBed } from '@angular/core/testing';

import { TabsStaffListService } from './tabs-staff-list.service';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('TabsStaffListService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		imports: [RootTestModule]
	}));

	it('should be created', () => {
		const service: TabsStaffListService = TestBed.get(TabsStaffListService);
		expect(service).toBeTruthy();
	});
});
