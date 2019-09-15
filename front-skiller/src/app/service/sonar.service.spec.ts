import { TestBed } from '@angular/core/testing';

import { SonarService } from './sonar.service';

describe('SonarService', () => {
	beforeEach(() => TestBed.configureTestingModule({}));

	it('should be created', () => {
		const service: SonarService = TestBed.get(SonarService);
		expect(service).toBeTruthy();
	});
});
