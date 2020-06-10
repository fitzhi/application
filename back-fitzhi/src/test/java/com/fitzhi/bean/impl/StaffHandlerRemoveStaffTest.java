/**
 * 
 */
package com.fitzhi.bean.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerRemoveStaffTest {

	@Autowired
	private StaffHandler staffHandler;
	
	@Test
	public void testRemoveStaff() throws Exception {		
		staffHandler.addNewStaffMember(new Staff(1789, "firstName", "lastName", "nickName", "login", "email", "ET"));
		Assert.assertNotNull(staffHandler.getStaff(1789));
		staffHandler.removeStaff(1789);
		Assert.assertNull(staffHandler.getStaff(1789));
	}
	
}