import { TestBed } from '@angular/core/testing';

import { GhostsService } from './ghosts.service';

describe('GhostsService', () => {
  let service: GhostsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GhostsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});


/*
	@Test
	public void found() {
		Staff staff = new Staff(1, "Frédéric", "VIDAL", "frvidal", "frvidal","frvidal@nope.com", "Gaulo-roman");
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