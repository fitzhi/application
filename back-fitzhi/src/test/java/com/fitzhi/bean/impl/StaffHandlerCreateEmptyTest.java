package com.fitzhi.bean.impl;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void before() {
		staffHandler.getStaff().put(1788, new Staff(1788, "login", "password"));
	}

	@Test
	public void testAuthorWith1Word() throws ApplicationException {
		
		Staff st = staffHandler.createEmptyStaff("one");

		Assert.assertEquals("one", st.getLastName());
		Assert.assertEquals("one", st.getNickName());
		Assert.assertEquals("one", st.getLogin());

		Assert.assertEquals(1789, st.getIdStaff());
		staffHandler.removeStaff(1789);
	}

	@Test
	public void testAuthorWith2Words() throws ApplicationException {
		
		Staff st = staffHandler.createEmptyStaff("one two");

		Assert.assertEquals("one", st.getFirstName());
		Assert.assertEquals("two", st.getLastName());
		Assert.assertEquals("one two", st.getNickName());

		Assert.assertEquals(1789, st.getIdStaff());
		staffHandler.removeStaff(1789);
	}

	@Test
	public void testAuthorWith3Words() throws ApplicationException {
		
		Staff st = staffHandler.createEmptyStaff("one two three");

		Assert.assertEquals("one", st.getFirstName());
		Assert.assertEquals("two three", st.getLastName());
		Assert.assertEquals("one two three", st.getNickName());

		Assert.assertEquals(1789, st.getIdStaff());
		staffHandler.removeStaff(1789);
	}

	@Test
	public void createStaffInAnEmptyCompayny() throws ApplicationException {
		//Deep clone
		Gson gson = new Gson();
		String jsonString = gson.toJson(staffHandler.getStaff());
		
		staffHandler.getStaff().clear();
		Staff st = staffHandler.createEmptyStaff("first One");
		Assert.assertEquals(1, st.getIdStaff());

		Type type = new TypeToken<HashMap<Integer, Staff>>(){}.getType();
		HashMap<Integer, Staff> clonedMap = gson.fromJson(jsonString, type); 
		staffHandler.getStaff().clear();
		staffHandler.getStaff().putAll(clonedMap);
	}

	@After
	public void after() {
		if (staffHandler.containsStaffMember(1789)) {
			staffHandler.removeStaff(1789);
		}
		staffHandler.removeStaff(1788);
	}
}
