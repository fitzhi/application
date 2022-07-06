package com.fitzhi.bean.impl.ProjectHandler;

import static com.fitzhi.data.internal.ProjectLookupCriteria.Name;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the method {@link ProjectHandler#lookup(String, com.fitzhi.data.internal.ProjectLookupCriteria)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerLookupNameTest {

	@Autowired
	private ProjectHandler projectHandler;

	@Test
	public void found() throws ApplicationException {
		ProjectHandler spy = spy(projectHandler);
		
		Map<Integer, Project> projects = new HashMap<>();
		projects.put(1789, new Project(1789, "The Revolution", "http//theUrlRepositoryOfProject"));
		projects.put(1790, new Project(1790, "One year after the Revolution", "http//theUrlRepositoryOfProjectV2"));
		projects.put(79, new Project(70, "Destruction of the temple of Jerusalem", "http//noGitAtALl"));
		when(spy.getProjects()).thenReturn(projects);

		Optional<Project> oP = spy.lookup("The Revolution", null, Name);
		Assert.assertTrue(oP.isPresent());
		Assert.assertEquals(1789, oP.get().getId());
	}

	@Test
	public void takeFirstOne() throws ApplicationException {
		ProjectHandler spy = spy(projectHandler);
		
		Map<Integer, Project> projects = new HashMap<>();
		projects.put(1789, new Project(1789, "The Revolution", "http//theUrlRepositoryOfProject"));
		projects.put(1790, new Project(1790, "The revolution", "http//theUrlRepositoryOfProject"));
		projects.put(79, new Project(70, "Destruction of the temple of Jerusalem", "http//noGitAtALl"));
		when(spy.getProjects()).thenReturn(projects);

		Optional<Project> oP = spy.lookup("The Revolution", null, Name);
		Assert.assertTrue(oP.isPresent());
		Assert.assertEquals(1789, oP.get().getId());
	}

	@Test
	public void notfound() throws ApplicationException {
		ProjectHandler spy = spy(projectHandler);
		
		Map<Integer, Project> projects = new HashMap<>();
		projects.put(1789, new Project(1789, "The Revolution", "http//theUrlRepositoryOfProject"));
		projects.put(1790, new Project(1790, "One year after the Revolution", "http//theUrlRepositoryOfProjectV2"));
		projects.put(79, new Project(70, "Destruction of the temple of Jerusalem", "http//noGitAtALl"));
		when(spy.getProjects()).thenReturn(projects);

		Optional<Project> oP = spy.lookup("Unknown", null, Name);
		Assert.assertTrue(oP.isEmpty());
	}

	@Test
	public void empty() throws ApplicationException {
		ProjectHandler spy = spy(projectHandler);
		
		Map<Integer, Project> projects = new HashMap<>();
		when(spy.getProjects()).thenReturn(projects);

		Optional<Project> oP = spy.lookup("http//urlRepositoryOfProject",null, Name);
		Assert.assertTrue(oP.isEmpty());
	}
}
