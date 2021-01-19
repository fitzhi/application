package com.fitzhi.bean.impl;

import java.util.Map;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing the method {@link StaffHandler#createEmptyStaff(String)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerCreateEmptyTest {

	@Autowired
	private StaffHandler staffHandler;

	@Test
	public void testAuthorWith1Word() throws ApplicationException {
		
		Map<Integer, Staff> company = staffHandler.getStaff();
		int size = company.size();

		Staff st = staffHandler.createEmptyStaff("one");

		Assert.assertEquals("one", st.getLastName());
		Assert.assertEquals("one", st.getNickName());
		Assert.assertEquals("one", st.getLogin());

		Assert.assertEquals(size+1, st.getIdStaff());

		staffHandler.removeStaff(st.getIdStaff());
	}

	@Test
	public void testAuthorWith2Words() throws ApplicationException {
		
		Map<Integer, Staff> company = staffHandler.getStaff();
		int size = company.size();

		Staff st = staffHandler.createEmptyStaff("one two");

		Assert.assertEquals("one", st.getFirstName());
		Assert.assertEquals("two", st.getLastName());
		Assert.assertEquals("one two", st.getNickName());

		Assert.assertEquals(size+1, st.getIdStaff());

		staffHandler.removeStaff(st.getIdStaff());
	}

	@Test
	public void testAuthorWith3Words() throws ApplicationException {
		
		Staff st = staffHandler.createEmptyStaff("one two three");

		Assert.assertEquals("one", st.getFirstName());
		Assert.assertEquals("two three", st.getLastName());
		Assert.assertEquals("one two three", st.getNickName());

		staffHandler.removeStaff(st.getIdStaff());
	}

}
