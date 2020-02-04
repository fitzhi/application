import { TestBed, inject } from '@angular/core/testing';

import { StaffListService } from './staff-list.service';
import { RootTestModule } from '../root-test/root-test.module';

describe('ListStaffService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [],
			imports: [RootTestModule]
		});
	});

	it('should be created', inject([StaffListService], (service: StaffListService) => {
		expect(service).toBeTruthy();
	}));
});
