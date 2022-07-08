package com.fitzhi.bean.impl.ProjectAudit;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

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
	public void before() throws ApplicationException {
		project = projectHandler.addNewProject(new Project(1789, "Great revolutionary project"));
		projectHandler.disableDataSaving();
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
	
	@Test(expected = ApplicationException.class)
	public void processAndSaveGlobalAuditEvaluationOnUnknownProject() throws ApplicationException {
		projectAuditHandler.processAndSaveGlobalAuditEvaluation(666);
	}

	@Test
	public void processAndSaveGlobalAuditEvaluationNominal() throws ApplicationException {
		projectAuditHandler.processAndSaveGlobalAuditEvaluation(1789);
		Assert.assertEquals("Audit evaluation has to be equal to 52", 52, project.getAuditEvaluation());
	}
		
	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1789); 
	}
}
