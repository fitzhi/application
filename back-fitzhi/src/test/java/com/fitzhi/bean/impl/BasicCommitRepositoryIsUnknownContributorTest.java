package com.fitzhi.bean.impl;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;

/**
 * <p>
 * Test unit for the method {@link BasicCommitRepository#isUnknownContributor(StaffHandler, Staff)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BasicCommitRepositoryIsUnknownContributorTest {

	CommitRepository commitRepository;
	
	@Autowired
	StaffHandler staffHandler;
	
	@Before()
	public void before() {
		commitRepository = new BasicCommitRepository();
	}
	
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

}
