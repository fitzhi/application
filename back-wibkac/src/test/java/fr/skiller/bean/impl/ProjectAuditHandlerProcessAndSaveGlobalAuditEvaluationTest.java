package fr.skiller.bean.impl;

import static org.junit.Assert.assertEquals;

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
 * Test of the class {@link ProjectAuditHandler#processAndSaveGlobalAuditEvaluation(int)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectAuditHandlerProcessAndSaveGlobalAuditEvaluationTest {

	@Autowired
	ProjectAuditHandler projectAuditHandler;

	@Autowired
	ProjectHandler projectHandler;
	
	private Project project;
	
	@Before
	public void before() throws SkillerException {
		project = projectHandler.addNewProject(new Project(1789, "Revolutionary project"));
		project.setAudit(new HashMap<Integer, AuditTopic>());
		
		AuditTopic at1 = new AuditTopic();
		at1.setWeight(30);
		at1.setEvaluation(80);
		project.getAudit().put(1, at1);
		
		AuditTopic at2 = new AuditTopic();
		at2.setWeight(70);
		at2.setEvaluation(40);
		project.getAudit().put(2, at2);
		
	}
	
	@Test(expected = SkillerException.class)
	public void processAndSaveGlobalAuditEvaluationOnUnknownProject() throws SkillerException {
		projectAuditHandler.processAndSaveGlobalAuditEvaluation(666);
	}

	@Test
	public void processAndSaveGlobalAuditEvaluationNominal() throws SkillerException {
		projectAuditHandler.processAndSaveGlobalAuditEvaluation(1789);
		Assert.assertEquals("Audit evaluation has to be equal to 52", 52, project.getAuditEvaluation());
	}
		
}
