import { TestBed } from '@angular/core/testing';
import { Constellation } from '../data/constellation';

import { StarfieldService } from './starfield.service';

describe('StarfieldService', () => {
	let service: StarfieldService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(StarfieldService);
	});

	it('should be successfully created', () => {
		expect(service).toBeTruthy();
	});

	it('should correctly assemble the constellations.', done => {
		const constellations = [];
		constellations.push(new Constellation(1, 2, 'black', 'lightGreen'));
		constellations.push(new Constellation(2, 3));
		service.assembleTheStars(constellations);

		service.stars$.subscribe({
			next: stars => {
				expect(stars.length).toBe(5);
				expect(stars[0].idSkill).toBe(1);
				expect(stars[1].idSkill).toBe(1);
				expect(stars[2].idSkill).toBe(2);
				expect(stars[3].idSkill).toBe(2);
				expect(stars[4].idSkill).toBe(2);
				done();
			}
		});
	});
	
});
