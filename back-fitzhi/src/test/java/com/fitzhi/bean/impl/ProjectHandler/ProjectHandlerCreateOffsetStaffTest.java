package com.fitzhi.bean.impl.ProjectHandler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.exception.ApplicationException;

/**
 * Test of method {@link ProjectHandler#createOffSetStaff}
 * 
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerCreateOffsetStaffTest {

	@Autowired
	StaffHandler staffHandler;

	@Test
	public void nominal() throws ApplicationException {
		Assert.assertFalse("No offset staff record", staffHandler.containsStaffMember(StaffHandler.SLAVE_OFFSET));
		staffHandler.createOffSetStaff();
		// To avoid any serialization on the file system.
		staffHandler.dataAreSaved();
		Assert.assertTrue("Offset staff is recorded", staffHandler.containsStaffMember(StaffHandler.SLAVE_OFFSET));
	}

	@After
	public void after() {
		staffHandler.removeStaff(StaffHandler.SLAVE_OFFSET);
	}

}
