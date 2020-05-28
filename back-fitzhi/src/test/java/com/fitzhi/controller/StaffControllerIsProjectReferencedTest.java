package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.SkillerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing the method {@link StaffHandler#isProjectReferenced(int)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffControllerIsProjectReferencedTest {

	@Autowired
	private ProjectHandler projectHandler;

	@Autowired
	private StaffHandler staffHandler;
	
	@Before 
	public void before() throws SkillerException {
		projectHandler.addNewProject(new Project(1789, "The revolutionary project"));
		projectHandler.addNewProject(new Project(1805, "Austerlitz"));
		staffHandler.getStaff().put(10000, 
				new Staff(10000, "firstName", "lastName", "nickName", "login", "email", "level"));
		staffHandler.getStaff().put(10001, 
				new Staff(10001, "firstName", "lastName", "nickName", "login", "email", "level"));
		staffHandler.getStaff().put(10002, 
				new Staff(10002, "firstName", "lastName", "nickName", "login", "email", "level"));
	}
	
	@Test
	@WithMockUser
	public void testNotPresent() throws Exception {
		staffHandler.getStaff(10000).addMission(new Mission(10000, 1, "Project test"));
		staffHandler.getStaff(10002).addMission(new Mission(10002, 1805, "Austerlitz"));
		Assert.assertFalse(staffHandler.isProjectReferenced(1789));
	}	

	@Test
	@WithMockUser
	public void testPresent() throws Exception {
		staffHandler.getStaff(10000).addMission(new Mission(10000, 1, "Project test"));
		staffHandler.getStaff(10002).addMission(new Mission(10002, 1805, "Austerlitz"));
		staffHandler.getStaff(10002).addMission(new Mission(10002, 1789, "Revolution"));
		Assert.assertTrue(staffHandler.isProjectReferenced(1789));
	}	
	
	@Before 
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(1789);
		projectHandler.getProjects().remove(1805);
		staffHandler.getStaff().remove(10000);
		staffHandler.getStaff().remove(10001);
		staffHandler.getStaff().remove(10002);
	}

}