package com.fitzhi.bean.impl;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing the method {@link StaffHandler#removeProject(int)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerRemoveProjectTest {

	@Autowired
	private StaffHandler staffHandler;

	@Before
	public void before() {

		staffHandler.getStaff().put(1789, new Staff(1789, "user", "password"));
		Mission m = new Mission(1879, 1, "Nope");
		staffHandler.lookup(1789).getMissions().add(m);

		staffHandler.getStaff().put(1964, new Staff(1964, "Frédéric", "V"));
		m = new Mission(1964, 1, "Nope");
		staffHandler.lookup(1964).getMissions().add(m);
	}

	@Test
	public void remove() throws ApplicationException {

		Staff staff = staffHandler.getStaff().get(1789);
		Assert.assertTrue (staff.getMissions().stream().anyMatch(mission -> (mission.getIdProject() == 1)));
		
		staff = staffHandler.getStaff().get(1964);
		Assert.assertTrue (staff.getMissions().stream().anyMatch(mission -> (mission.getIdProject() == 1)));

		staffHandler.removeProject(1);

		staff = staffHandler.getStaff().get(1789);
		Assert.assertFalse (staff.getMissions().stream().anyMatch(mission -> (mission.getIdProject() == 1)));
		
		staff = staffHandler.getStaff().get(1964);
		Assert.assertFalse (staff.getMissions().stream().anyMatch(mission -> (mission.getIdProject() == 1)));

	}

	@After
	public void after() {
		if (staffHandler.containsStaffMember(1789)) {
			staffHandler.removeStaff(1789);
		}
		if (staffHandler.containsStaffMember(1964)) {
			staffHandler.removeStaff(1964);
		}
	}
}
