import { TestBed } from '@angular/core/testing';

import { SunburstCinematicService } from './sunburst-cinematic.service';

describe('SunburstCinematicService', () => {
	beforeEach(() => TestBed.configureTestingModule({}));

	it('should be created', () => {
		const service: SunburstCinematicService = TestBed.inject(SunburstCinematicService);
		expect(service).toBeTruthy();
	});
});
