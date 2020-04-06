/**
 * 
 */
package com.fitzhi.bean.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileDataSaverImplTest {

	@Autowired
	DataHandler dataSaver;
	
	@Test
	public void saveProjects() throws SkillerException {
		Map<Integer, Project> projects = new HashMap<>();
		projects.put(1, new Project(1, "TEST 1"));
		Project p = new Project(2, "TEST 2");
		projects.put(2, p);
		p.setSkills(new HashMap<Integer, ProjectSkill>());
		p.getSkills().put(1, new ProjectSkill(1));
		projects.put(3, new Project(3, "TEST 3"));
		dataSaver.saveProjects(projects);
		
		projects = dataSaver.loadProjects();
		Assert.assertEquals(3, projects.size());
		Assert.assertEquals(1, projects.get(1).getId());
		Assert.assertEquals("TEST 1", projects.get(1).getName());
		Assert.assertEquals(2, projects.get(2).getId());
		Assert.assertEquals("TEST 2", projects.get(2).getName());
		Assert.assertEquals(1, projects.get(2).getSkills().size());		
		Assert.assertTrue(projects.get(2).getSkills().containsKey(1));
	}
}
