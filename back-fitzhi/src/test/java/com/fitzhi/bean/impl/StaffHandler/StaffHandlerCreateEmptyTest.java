package com.fitzhi.bean.impl.StaffHandler;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Author;
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

import lombok.extern.slf4j.Slf4j;

/**
 * Testing the method {@link StaffHandler#createEmptyStaff(String)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StaffHandlerCreateEmptyTest {

	@Autowired
	private StaffHandler staffHandler;

	@Before
	public void before() {
		//
		// We cannot mock the method nextIdStaff() because this method is located on the same bean as createEmptyStaff
		//
		// We cleanup all staff entries with an id lower than 1788 
		// it looks like some tests keep data in the staff collection when execeting the tests on some IC
		staffHandler.getStaff().entrySet().removeIf(e -> e.getKey() > 1788);
		staffHandler.getStaff().put(1788, new Staff(1788, "login", "password"));
	}

	@Test
	public void testAuthorWith1Word() throws ApplicationException {
		
		Staff st = staffHandler.createEmptyStaff(new Author("one", "one@void.com"));

		Assert.assertEquals("one", st.getLastName());
		Assert.assertEquals("one", st.getNickName());
		Assert.assertEquals("one", st.getLogin());
		Assert.assertEquals("one@void.com", st.getEmail());

		Assert.assertEquals(1789, st.getIdStaff());
		staffHandler.removeStaff(1789);
	}

	@Test
	public void testAuthorWith2Words() throws ApplicationException {
		
		Staff st = staffHandler.createEmptyStaff(new Author("one two", "otwo@void.com"));

		Assert.assertEquals("one", st.getFirstName());
		Assert.assertEquals("two", st.getLastName());
		Assert.assertEquals("one two", st.getNickName());
		Assert.assertEquals("otwo@void.com", st.getEmail());

		Assert.assertEquals(1789, st.getIdStaff());
		staffHandler.removeStaff(1789);
	}

	@Test
	public void testAuthorWith3Words() throws ApplicationException {
		
		Staff st = staffHandler.createEmptyStaff(new Author("one two three", "otthree@void.com"));

		Assert.assertEquals("one", st.getFirstName());
		Assert.assertEquals("two three", st.getLastName());
		Assert.assertEquals("one two three", st.getNickName());
		Assert.assertEquals("otthree@void.com", st.getEmail());

		Assert.assertEquals(1789, st.getIdStaff());
		staffHandler.removeStaff(1789);
	}

	@Test
	public void createStaffInAnEmptyCompayny() throws ApplicationException {
		//Deep clone
		Gson gson = new Gson();
		String jsonString = gson.toJson(staffHandler.getStaff());
		
		staffHandler.getStaff().clear();
		Staff st = staffHandler.createEmptyStaff(new Author("first One", "firstone@void.com"));
		Assert.assertEquals(1, st.getIdStaff());
		Assert.assertEquals("firstone@void.com", st.getEmail());

		// 
		Type type = new TypeToken<HashMap<Integer, Staff>>(){}.getType();
		HashMap<Integer, Staff> clonedMap = gson.fromJson(jsonString, type); 
		staffHandler.getStaff().clear();
		staffHandler.getStaff().putAll(clonedMap);
		staffHandler.getStaff().values().stream().map(Staff::fullName).forEach(log::debug);
	}

	@After
	public void after() {
		if (staffHandler.containsStaffMember(1789)) {
			staffHandler.removeStaff(1789);
		}
		staffHandler.removeStaff(1788);
	}
}
