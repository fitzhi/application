/**
 * 
 */
package com.fitzhi.bean.impl.ProjectHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.exception.ApplicationException;

/**
 * This class tests the method {@link ProjectHandler#updateSkills(java.util.List)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerUpdateSkillsTest {

	List<CommitHistory> repo;
	
	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	SkillHandler skillHandler;
	
	final int ID_JAVA = 1;
	
	final int ID_TS = 2;

	long sizeJavaProject = 0;
	long sizeJavaPing = 0;
	
	@Before
	public void before() throws ApplicationException {
		
		//
		// We are adding code files with a Java file within it.
		// We expect to retrieve the Java skill
		//
		CommitHistory javaPing = new CommitHistory("src/main/java/com/fitzhi/controller/PingController.java", 0); 
		sizeJavaProject = new File(javaPing.getSourcePath()).length();
		
		CommitHistory javaProject = new CommitHistory("src/main/java/com/fitzhi/controller/ProjectController.java", 0); 
		sizeJavaPing = new File(javaProject.getSourcePath()).length();
		
		CommitHistory php = new CommitHistory("src/test/resources/other-sources-for-testing-purpose/sample.php", 0); 
		CommitHistory ts = new CommitHistory("../front-fitzhi/src/app/app.module.ts", 0); 
		
		repo = new ArrayList<CommitHistory>();
		repo.add(javaPing);
		repo.add(javaProject);
		repo.add(php);
		repo.add(ts);
		
		project = new Project(1789, "my testing project");
		project.setLocationRepository(".");
		projectHandler.addNewProject(project);
		
	}
	
	@Test
	public void addANonExistentSkill() throws ApplicationException {
		projectHandler.updateSkills(project, repo);
		Assert.assertFalse(projectHandler.lookup(1789).getSkills().isEmpty());
		Assert.assertEquals(2, projectHandler.lookup(1789).getSkills().size());
	
		Assert.assertTrue(projectHandler.lookup(1789).getSkills().containsKey(ID_JAVA));
		Assert.assertEquals(2, projectHandler.lookup(1789).getSkills().get(ID_JAVA).getNumberOfFiles());
		Assert.assertEquals(sizeJavaProject + sizeJavaPing, projectHandler.lookup(1789).getSkills().get(ID_JAVA).getTotalFilesSize());
		
		Assert.assertTrue(projectHandler.lookup(1789).getSkills().containsKey(ID_TS));
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.getProjects().remove(1789);
	}
}
