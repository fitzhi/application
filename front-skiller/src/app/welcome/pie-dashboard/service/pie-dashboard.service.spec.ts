import { TestBed } from '@angular/core/testing';

import { PieDashboardService } from './pie-dashboard.service';

describe('PieDashboardService', () => {
	beforeEach(() => TestBed.configureTestingModule({}));

	it('should be created', () => {
		const service: PieDashboardService = TestBed.get(PieDashboardService);
		expect(service).toBeTruthy();
	});
});
