package com.fitzhi.bean.impl.StaffHandler;

import com.fitzhi.bean.StaffHandler;
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
 * Testing the method {@link StaffHandler#nextIdStaff()}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerNextIdStaffTest {

	@Autowired
	private StaffHandler staffHandler;

	int max = 1790;

	@Before
	public void before() {
		staffHandler.getStaff().put(1789, new Staff(1789, "user", "password"));
		// Due to a possible collision with an another test
		max = staffHandler.getStaff().keySet().stream().mapToInt(v->v).max().getAsInt();
	}

	@Test
	public void testNextIdStaff() throws ApplicationException {
		final int idStaff = staffHandler.nextIdStaff();
		Assert.assertEquals(max+1, idStaff);
	}

	@After
	public void after() {
		if (staffHandler.containsStaffMember(1789)) {
			staffHandler.removeStaff(1789);
		}
	}
}
