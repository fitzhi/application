package com.fitzhi.bean.impl;

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
import com.fitzhi.data.internal.AttachmentFile;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.service.FileType;

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
public class ProjectAuditHandlerRemoveAttachmentFileTest {

	@Autowired
	ProjectAuditHandler projectAuditHandler;

	@Autowired
	ProjectHandler projectHandler;
	
	private Project project;
	
	private int ID_PROJECT = 314116;
	@Before
	public void before() throws ApplicationException {
		project = projectHandler.addNewProject(new Project(ID_PROJECT, "PI"));
		project.setAudit(new HashMap<Integer, AuditTopic>());
		AuditTopic auditTopic = new AuditTopic();
		auditTopic.setEvaluation(40);
		auditTopic.setWeight(100);
		project.getAudit().put(1, new AuditTopic());

	}
	
	@Test(expected = ApplicationException.class)
	public void AttachmentFileOnUnknownProject() throws ApplicationException {
		projectAuditHandler.removeAttachmentFile(666, 1, 0);
	}

	@Test(expected = ApplicationException.class)
	public void removeAttachmentFileOnUnknownTopic() throws ApplicationException {
		projectAuditHandler.removeAttachmentFile(ID_PROJECT, -1002, 0);
	}
	
	@Test
	public void removeAttachmentFile() throws ApplicationException {
		AuditTopic at = projectAuditHandler.getTopic(ID_PROJECT, 1);
		Assert.assertEquals("Attachment list is empty", 0, at.getAttachmentList().size());
		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, 1, new AttachmentFile(0, "theFileName", FileType.valueOf(0), "theLabel"));		
		Assert.assertEquals("Attachment list is NO MORE empty", 1, at.getAttachmentList().size());
		Assert.assertEquals("theFileName", at.getAttachmentList().get(0).getFileName());
		Assert.assertEquals("theLabel", at.getAttachmentList().get(0).getLabel());	
		
		projectAuditHandler.removeAttachmentFile(ID_PROJECT, 1, 0);		
		Assert.assertEquals("Attachment list is empty", 0, at.getAttachmentList().size());
	}
	
	@Test
	public void removeSecondAttachmentFile() throws ApplicationException {
		AuditTopic at = projectAuditHandler.getTopic(ID_PROJECT, 1);
		Assert.assertEquals("Attachment list is empty", 0, at.getAttachmentList().size());
		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, 1, new AttachmentFile(0, "theFileName", FileType.valueOf(0), "theLabel"));		
		Assert.assertEquals("Attachment list is NO MORE empty", 1, at.getAttachmentList().size());
		Assert.assertEquals("theFileName", at.getAttachmentList().get(0).getFileName());
		Assert.assertEquals("theLabel", at.getAttachmentList().get(0).getLabel());	
		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, 1, new AttachmentFile(1, "stupidFileName.doc", FileType.valueOf(0), "stupid label"));		
		Assert.assertEquals("2 attachment files", 2, at.getAttachmentList().size());
		Assert.assertEquals("stupidFileName.doc", at.getAttachmentList().get(1).getFileName());
		Assert.assertEquals("stupid label", at.getAttachmentList().get(1).getLabel());	

		projectAuditHandler.updateAttachmentFile(ID_PROJECT, 1, new AttachmentFile(2, "lastFileName.doc", FileType.valueOf(0), "last label"));		
		Assert.assertEquals("3 attachment files", 3, at.getAttachmentList().size());
		Assert.assertEquals("lastFileName.doc", at.getAttachmentList().get(2).getFileName());
		Assert.assertEquals("last label", at.getAttachmentList().get(2).getLabel());	
		
		projectAuditHandler.removeAttachmentFile(ID_PROJECT, 1, 1);		
		Assert.assertEquals("2 attachment files", 2, at.getAttachmentList().size());
		Assert.assertEquals("lastFileName.doc", at.getAttachmentList().get(1).getFileName());
		Assert.assertEquals("last label", at.getAttachmentList().get(1).getLabel());	
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.getProjects().remove(ID_PROJECT);	
	}
	
}
