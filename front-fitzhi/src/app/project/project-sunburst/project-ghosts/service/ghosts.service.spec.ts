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
	expect(service.reduceCharacters('Frédéric')).toBe('frederic')
	expect(service.reduceCharacters('  Frédéric    VIDAL ')).toBe('frederic vidal')
	expect(service.reduceCharacters('Frédéric-VIDAL')).toBe('frederic vidal')
  });


  	it('should handle the staff member Frédéric VIDAL', () => {
		expect(service).toBeTruthy();
		const staff: Collaborator = {
		idStaff: 1, firstName: 'Frédéric', lastName: 'VIDAL', login: 'frvidal', nickName: 'frvidal', email: 'frvidal@nope.com',
		level: 'developer',  active: true, forceActiveState: true, external: false, 
		missions: [], experiences: [], dateInactive: null, 
		application: '', typeOfApplication: 0 };

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
			level: 'developer',  active: true, forceActiveState: true, external: false, 
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
	
	it('should handle the staff member Jean VIDAL', () => {
		expect(service).toBeTruthy();
		const staff: Collaborator = {
			idStaff: 1, firstName: 'Frédéric', lastName: 'VIDAL', login: 'frvidal', nickName: 'frvidal', email: 'frvidal@nope.com',
			level: 'developer',  active: true, forceActiveState: true, external: false, 
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


/*
	@Test
	public void found() {
		Set<String> set = new HashSet<>();
		set.add("milou");
		set.add("tintin");
		set.add("frederic vidal");
		set.add("frvidal");
		commitRepository.setUnknownContributors(set);
		Assert.assertFalse(commitRepository.extractMatchingUnknownContributors(staffHandler, staff).isEmpty());
		Assert.assertEquals(2,commitRepository.extractMatchingUnknownContributors(staffHandler, staff).size());
	}
	
	@Test
	public void found2s() {
		Staff staff = new Staff(1, "Frédéric", "LOGIBEAU", null, "flogibeau","flogibeau@nope.com", "Gaulo-roman");
		Set<String> set = new HashSet<>();
		set.add("flogibeau");
		set.add("Frederic Logibeau");
		set.add("frederic vidal");
		set.add("frvidal");
		commitRepository.setUnknownContributors(set);
		Assert.assertFalse(commitRepository.extractMatchingUnknownContributors(staffHandler, staff).isEmpty());
		Assert.assertEquals(2, commitRepository.extractMatchingUnknownContributors(staffHandler, staff).size());
	}
	
	@Test
	public void notFound() {
		Staff staff = new Staff(1, "Frédéric", "VIDAL", "frvidal", "frvidal","frvidal@nope.com", "Gaulo-roman");
		Set<String> set = new HashSet<>();
		set.add("fvidal");
		set.add("tintin");
		set.add("jean vidal");
		commitRepository.setUnknownContributors(set);
		Assert.assertTrue(commitRepository.extractMatchingUnknownContributors(staffHandler, staff).isEmpty());
	}
*/