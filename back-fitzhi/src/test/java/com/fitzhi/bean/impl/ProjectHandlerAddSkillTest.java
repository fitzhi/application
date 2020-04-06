/**
 * 
 */
package com.fitzhi.bean.impl;

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
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.exception.SkillerException;

/**
 * This class tests the method {@link ProjectHandler#addSkill(Project, ProjectSkill)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerAddSkillTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws SkillerException {
		project = new Project(1789, "my testing project");
		projectHandler.addSkill(project, new ProjectSkill(1, 10));
	}
	
	@Test
	public void addExistingProjectSkill() throws SkillerException {
		projectHandler.addSkill(project, new ProjectSkill(1, 1));
		ProjectSkill[] projectSkills = new ProjectSkill[0];
		ProjectSkill ps = project.getSkills().toArray(projectSkills)[0];
		Assert.assertEquals(10, ps.getNumberOfFiles());
	}
	
	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(1789);
	}
}
