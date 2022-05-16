package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#contributors(int)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProjectHandlerContributorsTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@MockBean
	StaffHandler staffHandler;

	private Map<Integer, Staff> mockStaff = new HashMap<Integer, Staff>();

	@Before
	public void before() {
		Staff st1 = new Staff(1, "firstName", "lastName", "nickName", "login", "email", "level");
		st1.addMission(new Mission(1, 1, "one"));
		st1.addMission(new Mission(1, 1789, "Revolution"));
		mockStaff.put(1, st1);

		Staff st2 = new Staff(1, "firstName", "lastName", "nickName", "login", "email", "level");
		st1.addMission(new Mission(2, 1, "one"));
		st1.addMission(new Mission(2, 732, "one"));
		mockStaff.put(2, st2);
	}

	@Test
	public void empty() throws ApplicationException {
		Map<Integer, Staff> staffs = new HashMap<>();
		when(staffHandler.getStaff()).thenReturn(staffs);
		Assert.assertTrue(projectHandler.contributors(1789).isEmpty());
	}
	
	@Test
	public void found() throws ApplicationException {
		when(staffHandler.getStaff()).thenReturn(mockStaff);
		Assert.assertFalse(projectHandler.contributors(1789).isEmpty());
		Assert.assertEquals(1, projectHandler.contributors(1789).size());
		Assert.assertEquals(1, projectHandler.contributors(1789).get(0).getIdStaff());
	}

	@Test
	public void multiple() throws ApplicationException {
		Staff st3 = new Staff(3, "firstName", "lastName", "nickName", "login", "email", "level");
		st3.addMission(new Mission(3, 1, "one"));
		st3.addMission(new Mission(3, 1789, "Revolution"));
		mockStaff.put(2, st3);

		when(staffHandler.getStaff()).thenReturn(mockStaff);
		Assert.assertFalse(projectHandler.contributors(1789).isEmpty());
		Assert.assertEquals(2, projectHandler.contributors(1789).size());
		Assert.assertEquals(1, projectHandler.contributors(1789).get(0).getIdStaff());
		Assert.assertEquals(3, projectHandler.contributors(1789).get(1).getIdStaff());
	}

	@Test
	public void notFound() throws ApplicationException {
		when(staffHandler.getStaff()).thenReturn(mockStaff);
		Assert.assertTrue(projectHandler.contributors(1788).isEmpty());
	}


}
