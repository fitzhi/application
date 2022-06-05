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
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ProjectAuditHandlerAddUpdateAttachmentFileTest {

	@Autowired
	ProjectAuditHandler projectAuditHandler;

	@MockBean
	ProjectHandler projectHandler;
	
	private Project project;
	
	private int ID_PROJECT = 314116;

	public Project project() throws ApplicationException {
		project = new Project(ID_PROJECT, "PI 2");
		project.setAudit(new HashMap<Integer, AuditTopic>());
		AuditTopic auditTopic = new AuditTopic();
		auditTopic.setEvaluation(40);
		auditTopic.setWeight(100);
		project.getAudit().put(1, new AuditTopic());
		return project;
	}

	@Test(expected = ApplicationException.class)
	public void addAttachmentFileOnUnknownProject() throws ApplicationException {
		projectAuditHandler.updateAttachmentFile(666, 1, new AttachmentFile(0, "theFilename", FileType.valueOf(0), "theLabel"));
	}

	@Test(expected = ApplicationException.class)
	public void addAttachmentFileOnUnknownTopic() throws ApplicationException {
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, -1002, new AttachmentFile(0, "theFilename", FileType.valueOf(0), "theLabel"));
	}
	
	@Test
	public void addFirstAttachmentFile() throws ApplicationException {
		when(projectHandler.lookup(ID_PROJECT)).thenReturn(project());
		AuditTopic at = projectAuditHandler.getTopic(ID_PROJECT, 1);
		Assert.assertEquals("attachment list is empty", 0, at.getAttachmentList().size());
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, 1, new AttachmentFile(0, "theFileName", FileType.valueOf(0), "theLabel"));		
		Assert.assertEquals("attachment list is NO MORE empty", 1, at.getAttachmentList().size());
		Assert.assertEquals("theFileName", at.getAttachmentList().get(0).getFileName());
		Assert.assertEquals("theLabel", at.getAttachmentList().get(0).getLabel());	
	}
	
	@Test
	public void updateSecondAttachmentFile() throws ApplicationException {
		when(projectHandler.lookup(ID_PROJECT)).thenReturn(project());
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
}
