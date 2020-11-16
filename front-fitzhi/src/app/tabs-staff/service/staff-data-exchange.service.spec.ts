import { TestBed } from '@angular/core/testing';

import { StaffDataExchangeService } from './staff-data-exchange.service';

describe('StaffDataExchangeService', () => {
	beforeEach(() => TestBed.configureTestingModule({}));

	it('should be created', () => {
		const service: StaffDataExchangeService = TestBed.inject(StaffDataExchangeService);
		expect(service).toBeTruthy();
	});
});
