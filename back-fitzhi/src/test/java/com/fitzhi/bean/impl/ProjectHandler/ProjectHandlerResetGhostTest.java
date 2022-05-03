package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Ghost;
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
 * This class tests the method {@link ProjectHandler#resetGhost(Project, String)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerResetGhostTest {

	@Autowired
	ProjectHandler projectHandler;
	
	private Project project;


	@Before
	public void before() {
		project = new Project (1789, "French revolution");
	}

	/**
	 * There is not ghost in the project, so "pseudo" should cannot be reset.
	 * @throws ApplicationException
	 */
	@Test (expected = ApplicationRuntimeException.class)
	public void empty() throws ApplicationException {
		projectHandler.resetGhost(project, "pseudo");
	}

	/**
	 * The ghost "pseudo" is not present.
	 * @throws ApplicationException
	 */
	@Test (expected = ApplicationRuntimeException.class)
	public void notFound() throws ApplicationException {
		List<Ghost> ghosts = new ArrayList<>();
		ghosts.add(new Ghost("unknown", 1789, false));
		ghosts.add(new Ghost("tech", -1, true));
		project.setGhosts(ghosts);
		projectHandler.resetGhost(project, "pseudo");
	}
	
	/**
	 * The pseudo "pseudo" has been found.
	 * @throws ApplicationException
	 */
	@Test
	public void found() throws ApplicationException {
		List<Ghost> ghosts = new ArrayList<>();
		ghosts.add(new Ghost("pseudo", 1789, false));
		ghosts.add(new Ghost("tech", -1, true));
		project.setGhosts(ghosts);
		projectHandler.resetGhost(project, "pseudo");
		Assert.assertEquals(project.getGhosts().get(0).getPseudo(), "pseudo");
		Assert.assertEquals(project.getGhosts().get(0).getIdStaff(), Ghost.NULL);
		Assert.assertTrue(projectHandler.isDataUpdated());
	}
}
