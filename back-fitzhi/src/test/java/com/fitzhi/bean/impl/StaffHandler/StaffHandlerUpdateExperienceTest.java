package com.fitzhi.bean.impl.StaffHandler;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.NotFoundException;

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
 * Test of method {@link StaffHandler#updateExperience(int, com.fitzhi.data.internal.Experience)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerUpdateExperienceTest {

	@Autowired
	private StaffHandler staffHandler;

	@Before
	public void before() {
		Staff st =  new Staff(1000,"Christian Aligato", "Chavez Tugo", "cact" , "cact", "cact@void.com", "");
		st.getExperiences().add(new Experience(1, 0));
		staffHandler.put(1000, st);
	}

	@Test
	public void nominalUpdate() throws NotFoundException {
		Assert.assertEquals(0, staffHandler.getStaff(1000).getExperience(1).getLevel());
		Assert.assertFalse(staffHandler.getStaff(1000).getExperience(1).isForced());
		staffHandler.updateExperience(1000, new Experience(1, 3));
		Assert.assertEquals(3, staffHandler.getStaff(1000).getExperience(1).getLevel());
		Assert.assertTrue(staffHandler.getStaff(1000).getExperience(1).isForced());
	}

	@Test
	public void doNotThrowErrorIfExperienceNotFound()  {
		staffHandler.updateExperience(1000, new Experience(1000, 3));
	}

	@After
	public void after() {
		staffHandler.removeStaff(1000);
	}
}
