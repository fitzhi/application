package fr.skiller.bean.impl;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.ProjectAuditHandler;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.data.internal.AuditTopic;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

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
public class ProjectAuditHandlerAddRemoveTopicTest {

	@Autowired
	ProjectAuditHandler projectAuditHandler;

	@Autowired
	ProjectHandler projectHandler;
	
	private Project project;
	
	@Before
	public void before() throws SkillerException {
		project = projectHandler.addNewProject(new Project(314116, "PI"));
		project.setAudit(new HashMap<Integer, AuditTopic>());
		project.getAudit().put(1, new AuditTopic());

	}
	
	@Test(expected = SkillerException.class)
	public void addTopicOnUnknownProject() throws SkillerException {
		projectAuditHandler.addTopic(666, 1);
	}

	@Test
	public void addNewTopic() throws SkillerException {
		projectAuditHandler.addTopic(314116, 2);
		Assert.assertTrue("addNewTopic did not succeed", project.getAudit().containsKey(2));
		Assert.assertTrue("addNewTopic did not succeed", project.getAudit().get(2).getIdTopic() == 2);
		
	}
	
	@Test(expected = SkillerException.class)
	public void removeTopicOnUnknownProject() throws SkillerException {
		projectAuditHandler.removeTopic(666, 1, false);
	}

	@Test
	public void removeTopic() throws SkillerException {
		projectAuditHandler.removeTopic(314116, 1, false);
		Assert.assertFalse("removeTopic did not succeed", project.getAudit().containsKey(1));
	}
	
	@Test
	public void loadExistingTopic() throws SkillerException {
		projectAuditHandler.addTopic(314116, 2);
		AuditTopic auditTopic = projectAuditHandler.getTopic(314116, 2);
		Assert.assertNotNull(auditTopic);
		Assert.assertTrue("addNewTopic did not succeed", auditTopic.getIdTopic() == 2);
		
	}

	@Test(expected = SkillerException.class)
	public void loadfailedUnknownTopic() throws SkillerException {
		projectAuditHandler.getTopic(314116, 666);		
	}
	
}
