package com.fitzhi.bean.impl.ProjectAudit;

import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.impl.ProjectAuditHandlerImpl;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * Test of the class {@link ProjectAuditHandlerImpl}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ProjectAuditHandlerAddRemoveTopicTest {

	@Autowired
	ProjectAuditHandler projectAuditHandler;

	@MockBean
	ProjectHandler projectHandler;
	
	private Project project;
	
	private int ID_PROJECT = 314116;

	public Project project() throws ApplicationException {
		project = new Project(ID_PROJECT, "PI 1");
		project.setAudit(new HashMap<Integer, AuditTopic>());
		AuditTopic auditTopic = new AuditTopic();
		auditTopic.setEvaluation(40);
		auditTopic.setWeight(100);
		project.getAudit().put(1, new AuditTopic());
		project.setAuditEvaluation(40);
		return project;
	}
	
	@Test(expected = ApplicationException.class)
	public void addTopicOnUnknownProject() throws ApplicationException {
		projectAuditHandler.addTopic(666, 1);
	}

	@Test
	public void addNewTopic() throws ApplicationException {

		when(projectHandler.lookup(ID_PROJECT)).thenReturn(project());

		projectAuditHandler.addTopic(ID_PROJECT, 2);
		Assert.assertTrue("addNewTopic did not succeed", project.getAudit().containsKey(2));
		Assert.assertTrue("addNewTopic did not succeed", project.getAudit().get(2).getIdTopic() == 2);

		Assert.assertEquals("Weights have to be shared between all topics", 50, project.getAudit().get(1).getWeight());
		Assert.assertEquals("Weights have to be shared between all topics", 50, project.getAudit().get(2).getWeight());
		
		projectAuditHandler.addTopic(ID_PROJECT, 3);
		Assert.assertEquals("Weights have to be shared between all topics", 33, project.getAudit().get(1).getWeight());
		Assert.assertEquals("Weights have to be shared between all topics", 33, project.getAudit().get(2).getWeight());
		Assert.assertEquals("Weights have to be shared between all topics", 34, project.getAudit().get(3).getWeight());

	}
	
	@Test(expected = ApplicationException.class)
	public void removeTopicOnUnknownProject() throws ApplicationException {
		when(projectHandler.lookup(666)).thenReturn(null);
		projectAuditHandler.removeTopic(666, 1, false);
	}

	@Test
	public void removeTopic() throws ApplicationException {
		
		when(projectHandler.lookup(ID_PROJECT)).thenReturn(project());

		Assert.assertEquals("Evaluation is equal to 0", 40, project.getAuditEvaluation());
		
		projectAuditHandler.removeTopic(ID_PROJECT, 1, false);
		Assert.assertFalse("removeTopic did not succeed", project.getAudit().containsKey(1));
		
		projectHandler.lookup(ID_PROJECT);
		Assert.assertEquals("Audit topics collection is empty", 0, project.getAudit().values().size());
		Assert.assertEquals("Evaluation is equal to 0", 0, project.getAuditEvaluation());
		
	}
	
	@Test
	public void loadAnExistingTopic() throws ApplicationException {
		when(projectHandler.lookup(ID_PROJECT)).thenReturn(project());

		projectAuditHandler.addTopic(ID_PROJECT, 2);
		AuditTopic auditTopic = projectAuditHandler.getTopic(ID_PROJECT, 2);
		Assert.assertNotNull(auditTopic);
		Assert.assertTrue("addNewTopic did not succeed", auditTopic.getIdTopic() == 2);
		
	}

	@Test(expected = ApplicationException.class)
	public void loadfailedUnknownTopic() throws ApplicationException {
		when(projectHandler.getProject(ID_PROJECT)).thenReturn(project());
		projectAuditHandler.getTopic(ID_PROJECT, 666);		
	}
	
}
