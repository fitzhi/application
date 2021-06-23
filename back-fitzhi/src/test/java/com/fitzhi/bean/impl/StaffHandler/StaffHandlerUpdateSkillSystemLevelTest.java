package com.fitzhi.bean.impl.StaffHandler;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Experience;
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
 * <p>
 * Test of method {@link StaffHandler#updateSkillSystemLevel(int, int, int)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerUpdateSkillSystemLevelTest {

	@Autowired
	private StaffHandler staffHandler;

	@Before
	public void before() {
		Staff staff =  new Staff(1789,"John", "Doo", "jdoo" , "jdoo", "jdoo@void.com", "");
		staff.getExperiences().add(new Experience(1, 0, 0));
		staffHandler.getStaff().put(1789, staff);
	}

	@Test
	public void nominalUpdate() throws ApplicationException {
		Assert.assertEquals(0, staffHandler.getStaff(1789).getExperience(1).getSystemLevel());
		staffHandler.updateSkillSystemLevel(1789, 1, 3);
		Assert.assertEquals(3, staffHandler.getStaff(1789).getExperience(1).getSystemLevel());
	}

	@Test
	public void whenExperienceNotFoundAddNewOne() throws ApplicationException {
		Assert.assertNull(staffHandler.getStaff(1789).getExperience(2));
		staffHandler.updateSkillSystemLevel(1789, 2, 1);
		Assert.assertNotNull(staffHandler.getStaff(1789).getExperience(2));
		Assert.assertEquals(1, staffHandler.getStaff(1789).getExperience(2).getSystemLevel());
	}

	@Test (expected = ApplicationException.class)
	public void whenStaffNotFoundThrowException() throws ApplicationException {
		staffHandler.updateSkillSystemLevel(1790, 2, 1);
	}

	@After
	public void after() {
		staffHandler.removeStaff(1789);
	}
}
