package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

/**
 * This class tests the method {@link ProjectHandler#getProject(int)}
 * 
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.BEFORE_CLASS)
public class ProjectHandlerGetProjectTest {

	@Autowired
	ProjectHandler projectHandler;
		
	private Map<Integer, Project> projects() {
		Map<Integer, Project> map = new HashMap<>();
		map.put(1515, new Project(1515, "Marignan"));
		map.put(1805, new Project(1805, "Austerlitz"));
		map.put(1815, new Project(1815, "Waterloo"));
		return map;
	}
	
	@Test
	public void nominal() throws ApplicationException {

		ProjectHandler spy = spy(projectHandler);
		when (spy.getProjects()).thenReturn(projects());

		Project project = spy.getProject(1515);
		Assert.assertEquals("Marignan", project.getName());
	}
	
	@Test
	public void notFound() throws ApplicationException {

		ProjectHandler spy = spy(projectHandler);
		when (spy.getProjects()).thenReturn(projects());

		try {
			spy.getProject(1807);
		} catch (ApplicationException ae) {
			Assert.assertEquals("There is no project for the identifier 1807", ae.getMessage());
		}

	}

}
