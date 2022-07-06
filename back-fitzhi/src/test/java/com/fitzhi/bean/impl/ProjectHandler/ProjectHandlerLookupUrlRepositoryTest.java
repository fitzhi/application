package com.fitzhi.bean.impl.ProjectHandler;

import static com.fitzhi.data.internal.ProjectLookupCriteria.UrlRepository;
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
 * Test of the method {@link ProjectHandler#lookup(String, String, com.fitzhi.data.internal.ProjectLookupCriteria)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerLookupUrlRepositoryTest {

	@Autowired
	private ProjectHandler projectHandler;

	private Map<Integer, Project> projects() {
		Map<Integer, Project> projects = new HashMap<>();
		Project p = new Project(1789, "The Revolution", "http//theUrlRepositoryOfProject");
		p.setBranch("branch-master");
		projects.put(1789, p);

		p = new Project(1790, "One year after the Revolution", "http//theUrlRepositoryOfProjectV2");
		p.setBranch("branch-master");		
		projects.put(1790, p);

		p = new Project(1791, "two year after the Revolution", "http//theUrlRepositoryOfProjectV2");
		p.setBranch("branch-master");		
		projects.put(1791, p);

		p = new Project(70, "Destruction of the temple of Jerusalem", "http//noGitAtALl");
		p.setBranch("branch-master");		
		projects.put(70, p);

		return projects;
	}

	@Test
	public void found() throws ApplicationException {
		ProjectHandler spy = spy(projectHandler);
		
		when(spy.getProjects()).thenReturn(projects());

		Optional<Project> oP = spy.lookup("http//theUrlRepositoryOfProject", "branch-master", UrlRepository);
		Assert.assertTrue(oP.isPresent());
		Assert.assertEquals(1789, oP.get().getId());
		Assert.assertEquals("The Revolution", oP.get().getName());
	}

	@Test
	public void takeFirstOne() throws ApplicationException {
		ProjectHandler spy = spy(projectHandler);
		
		when(spy.getProjects()).thenReturn(projects());

		Optional<Project> oP = spy.lookup("http//theUrlRepositoryOfProjectV2", "branch-master", UrlRepository);
		Assert.assertTrue(oP.isPresent());
		Assert.assertEquals(1790, oP.get().getId());
		Assert.assertEquals("One year after the Revolution", oP.get().getName());
	}

	@Test
	public void urlNotfound() throws ApplicationException {
		ProjectHandler spy = spy(projectHandler);
		
		when(spy.getProjects()).thenReturn(projects());
		
		Optional<Project> oP = spy.lookup("http//anotherUrlRepositoryOfProject", "branch-master", UrlRepository);
		Assert.assertTrue(oP.isEmpty());
	}

	@Test
	public void empty() throws ApplicationException {
		ProjectHandler spy = spy(projectHandler);
		
		Map<Integer, Project> projects = new HashMap<>();
		when(spy.getProjects()).thenReturn(projects);

		Optional<Project> oP = spy.lookup("http//urlRepositoryOfProject", "n/a", UrlRepository);
		Assert.assertTrue(oP.isEmpty());
	}
}
