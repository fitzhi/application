import { TestBed } from '@angular/core/testing';

import { GhostsService } from './ghosts.service';
import { Collaborator } from 'src/app/data/collaborator';

describe('GhostsService', () => {
	let service: GhostsService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(GhostsService);
	});

	it('tests some combinations with "Frédéric VIDAL"', () => {
		expect(service).toBeTruthy();
		expect(service.reduceCharacters('Frédéric')).toBe('frederic');
		expect(service.reduceCharacters('  Frédéric    VIDAL ')).toBe('frederic vidal');
		expect(service.reduceCharacters('Frédéric-VIDAL')).toBe('frederic vidal');
	});


	it('should handle the staff member Frédéric VIDAL', () => {
		expect(service).toBeTruthy();
		const staff: Collaborator = {
			idStaff: 1, firstName: 'Frédéric', lastName: 'VIDAL', login: 'frvidal', nickName: 'frvidal', email: 'frvidal@nope.com',
			level: 'developer', active: true, forceActiveState: true, external: false,
			missions: [], experiences: [], dateInactive: null,
			application: '', typeOfApplication: 0
		};

		const logins = [];
		logins.push('milou');
		logins.push('tintin');
		logins.push('frederic vidal');
		logins.push('frvidal');

		const eligibles = service.extractMatchingUnknownContributors(logins, staff);
		expect(eligibles.length).toBe(2);

	});

	it('should handle the staff member Frédéric LOGIBEAU', () => {
		expect(service).toBeTruthy();
		const staff: Collaborator = {
			idStaff: 1, firstName: 'Frédéric', lastName: 'LOGIBEAU', login: 'flogibeau', nickName: 'flogibeau', email: 'flogibeau@nope.com',
			level: 'developer', active: true, forceActiveState: true, external: false,
			missions: [], experiences: [], dateInactive: null,
			application: '', typeOfApplication: 0
		};

		const logins = [];
		logins.push('flogibeau');
		logins.push('Frederic Logibeau');
		logins.push('frederic vidal');
		logins.push('frvidal');

		const eligibles = service.extractMatchingUnknownContributors(logins, staff);
		expect(eligibles.length).toBe(2);

	});

	it('should include "Pommier Philippe" when the staff member is Frédéric POMMIER', () => {
		expect(service).toBeTruthy();
		const staff: Collaborator = {
			idStaff: 1, firstName: 'Frédéric', lastName: 'POMMIER', login: 'fpommier', nickName: 'fpommier', email: 'fpommier@nope.com',
			level: 'developer', active: true, forceActiveState: true, external: false,
			missions: [], experiences: [], dateInactive: null,
			application: '', typeOfApplication: 0
		};

		const logins = [];
		logins.push('Frédéric Pommier');
		logins.push('POMMIER Frédéric');
		logins.push('frederic pommier');
		logins.push('fpommier');

		const eligibles = service.extractMatchingUnknownContributors(logins, staff);
		expect(eligibles.length).toBe(4);
		expect(eligibles[0]).toBe('Frédéric Pommier');
		expect(eligibles[1]).toBe('POMMIER Frédéric');
		expect(eligibles[2]).toBe('frederic pommier');
		expect(eligibles[3]).toBe('fpommier');
	});

	it('should handle the staff member Jean VIDAL', () => {
		expect(service).toBeTruthy();
		const staff: Collaborator = {
			idStaff: 1, firstName: 'Frédéric', lastName: 'VIDAL', login: 'frvidal', nickName: 'frvidal', email: 'frvidal@nope.com',
			level: 'developer', active: true, forceActiveState: true, external: false,
			missions: [], experiences: [], dateInactive: null,
			application: '', typeOfApplication: 0
		};

		const logins = [];
		logins.push('fvidal');
		logins.push('tintin');
		logins.push('jean vidal');

		const eligibles = service.extractMatchingUnknownContributors(logins, staff);
		expect(eligibles.length).toBe(0);

	});

});

