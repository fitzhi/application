/**
 * 
 */
package com.fitzhi.bean.impl.ProjectHandler;

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
public class ProjectHandlerUpdateSkillsBasedOnPackageJsonEntryTest {

	
	private final int JAVA = 1;
	private final int ANGULAR = 4;
	
	List<CommitHistory> repo;
	
	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	SkillHandler skillHandler;
	
	@Before
	public void before() throws ApplicationException {
		
		//
		// We are adding code files with a Java file within it.
		// We expect to retrieve the Java skill
		//
		CommitHistory java = new CommitHistory("src/main/java/com/fitzhi/controller/PingController.java", 0); 
		CommitHistory php = new CommitHistory("src/test/resources/other-sources-for-testing-purpose/sample.php", 0); 
		CommitHistory packageJson = new CommitHistory("../front-fitzhi/package.json", 0); 
		
		repo = new ArrayList<CommitHistory>();
		repo.add(java);
		repo.add(php);
		repo.add(packageJson);
		
		project = new Project(1789, "my testing project");
		project.setLocationRepository(".");

		projectHandler.addNewProject(project);
		
	}
	
	@Test
	public void addANonExistentSkill() throws ApplicationException {

		projectHandler.updateSkills(project, repo);
		Assert.assertFalse(projectHandler.lookup(1789).getSkills().isEmpty());
		Assert.assertEquals(2, projectHandler.lookup(1789).getSkills().size());
		
		Assert.assertTrue("Java is detected", projectHandler.lookup(1789).getSkills().containsKey(JAVA));
		Assert.assertTrue("Angular is detected", projectHandler.lookup(1789).getSkills().containsKey(ANGULAR));
		
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1789);
	}
}
