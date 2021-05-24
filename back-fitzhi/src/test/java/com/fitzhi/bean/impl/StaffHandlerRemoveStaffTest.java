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

import lombok.extern.slf4j.Slf4j;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StaffHandlerRemoveStaffTest {

	@Autowired
	private StaffHandler staffHandler;
	
	@Test
	public void testRemoveStaff() throws Exception {		
		Staff staff = staffHandler.createWorkforceMember(new Staff(-1, "firstName", "lastName", "nickName", "flastname", "email", "ET"));
		log.debug (String.format("Staff created %d", staff.getIdStaff()));
		Assert.assertNotNull(staffHandler.lookup(staff.getIdStaff()));
		staffHandler.removeStaff(staff.getIdStaff());
		Assert.assertNull(staffHandler.lookup(staff.getIdStaff()));
	}
	
}