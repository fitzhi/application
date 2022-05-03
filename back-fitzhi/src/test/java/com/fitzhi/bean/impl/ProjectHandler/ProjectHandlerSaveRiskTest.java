package com.fitzhi.bean.impl.ProjectHandler;

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
 * This class tests the method {@link ProjectHandler#saveRisk(Project, int)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerSaveRiskTest {

	@Autowired
	ProjectHandler projectHandler;
	
	@Test
	public void save() throws ApplicationException {
		Project project = new Project(1789, "my testing project");
		projectHandler.saveRisk(project, 123);
		Assert.assertEquals(123, project.getStaffEvaluation());
		Assert.assertTrue(projectHandler.isDataUpdated());
	}
	
}
