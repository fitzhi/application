package com.tixhi.bean.impl;

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

import com.tixhi.bean.ProjectAuditHandler;
import com.tixhi.bean.ProjectHandler;
import com.tixhi.bean.impl.ProjectAuditHandlerImpl;
import com.tixhi.data.internal.AttachmentFile;
import com.tixhi.data.internal.AuditTopic;
import com.tixhi.data.internal.Project;
import com.tixhi.exception.SkillerException;
import com.tixhi.service.FileType;

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
public class ProjectAuditHandlerAddUpdateAttachmentFileTest {

	@Autowired
	ProjectAuditHandler projectAuditHandler;

	@Autowired
	ProjectHandler projectHandler;
	
	private Project project;
	
	private int ID_PROJECT = 314116;
	@Before
	public void before() throws SkillerException {
		project = projectHandler.addNewProject(new Project(ID_PROJECT, "PI"));
		project.setAudit(new HashMap<Integer, AuditTopic>());
		AuditTopic auditTopic = new AuditTopic();
		auditTopic.setEvaluation(40);
		auditTopic.setWeight(100);
		project.getAudit().put(1, new AuditTopic());

	}
	
	@Test(expected = SkillerException.class)
	public void addAttachmentFileOnUnknownProject() throws SkillerException {
		projectAuditHandler.updateAttachmentFile(666, 1, new AttachmentFile(0, "theFilename", FileType.valueOf(0), "theLabel"));
	}

	@Test(expected = SkillerException.class)
	public void addAttachmentFileOnUnknownTopic() throws SkillerException {
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, -1002, new AttachmentFile(0, "theFilename", FileType.valueOf(0), "theLabel"));
	}
	
	@Test
	public void addFirstAttachmentFile() throws SkillerException {
		AuditTopic at = projectAuditHandler.getTopic(ID_PROJECT, 1);
		Assert.assertEquals("attachment list is empty", 0, at.getAttachmentList().size());
		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, 1, new AttachmentFile(0, "theFileName", FileType.valueOf(0), "theLabel"));		
		Assert.assertEquals("attachment list is NO MORE empty", 1, at.getAttachmentList().size());
		Assert.assertEquals("theFileName", at.getAttachmentList().get(0).getFileName());
		Assert.assertEquals("theLabel", at.getAttachmentList().get(0).getLabel());	
	}
	
	@Test
	public void updateSecondAttachmentFile() throws SkillerException {
		AuditTopic at = projectAuditHandler.getTopic(ID_PROJECT, 1);
		Assert.assertEquals("attachment list is empty", 0, at.getAttachmentList().size());
		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, 1, new AttachmentFile(0, "theFileName", FileType.valueOf(0), "theLabel"));		
		Assert.assertEquals("attachment list is NO MORE empty", 1, at.getAttachmentList().size());
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
		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, 1, new AttachmentFile(1, "clever FileName.doc", FileType.valueOf(0), "clever label"));		
		Assert.assertEquals("3 attachment files", 3, at.getAttachmentList().size());
		Assert.assertEquals("clever FileName.doc", at.getAttachmentList().get(1).getFileName());
		Assert.assertEquals("clever label", at.getAttachmentList().get(1).getLabel());	
	}
	
	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(ID_PROJECT);	
	}
	
}
