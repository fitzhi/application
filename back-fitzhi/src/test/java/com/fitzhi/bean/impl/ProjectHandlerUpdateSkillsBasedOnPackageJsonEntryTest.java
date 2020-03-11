/**
 * 
 */
package com.fitzhi.bean.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.exception.SkillerException;

/**
 * This class tests the method {@link ProjectHandler#updateSkills(java.util.List)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerUpdateSkillsBasedOnPackageJsonEntryTest {

	
	private final int JAVA = 1;
	private final int ANGULAR = 3;
	
	List<CommitHistory> repo;
	
	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	SkillHandler skillHandler;
	
	@Before
	public void before() throws SkillerException {
		
		//
		// We are adding code files with a Java file within it.
		// We expect to retrieve the Java skill
		//
		CommitHistory java = new CommitHistory("/src/main/java/com/fitzhi/controller/PingController.java", 0); 
		CommitHistory php = new CommitHistory("/src/main/resources/other-sources-for-testing-purpose/sample.php", 0); 
		CommitHistory packageJson = new CommitHistory("/../front-fitzhi/package.json", 0); 
		
		repo = new ArrayList<CommitHistory>();
		repo.add(java);
		repo.add(php);
		repo.add(packageJson);
		
		project = new Project(1789, "my testing project");
		project.setLocationRepository(".");

		projectHandler.addNewProject(project);
		
	}
	
	@Test
	public void addANonExistentSkill() throws SkillerException {

		projectHandler.updateSkills(project, repo);
		Assert.assertFalse(projectHandler.get(1789).getSkills().isEmpty());
		Assert.assertEquals(2, projectHandler.get(1789).getSkills().size());
		
		Set<Integer> ids = new HashSet<>();
		projectHandler.get(1789).getSkills().stream().map(Skill::getId).forEach(ids::add);
		Assert.assertTrue("Java is detected", ids.contains(JAVA));
		Assert.assertTrue("Angular is detected", ids.contains(ANGULAR));
		
	}
	
	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(1789);
	}
}
