/**
 * 
 */
package fr.skiller.bean.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.DataSaver;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileDataSaverImplTest {

	@Autowired
	DataSaver dataSaver;
	
	@Test
	public void saveProjects() throws SkillerException {
		Map<Integer, Project> projects = new HashMap<Integer, Project>();
		projects.put(1, new Project(1, "TEST 1"));
		Project p = new Project(2, "TEST 2");
		projects.put(2, p);
		p.skills = new ArrayList<Skill>();
		p.skills.add(new Skill(1, "JAVA"));
		dataSaver.save(projects);
		projects.put(3, new Project(3, "TEST 3"));
		
		projects = dataSaver.load();
		Assert.assertEquals(2, projects.size());
		Assert.assertEquals(1, projects.get(1).id);
		Assert.assertEquals("TEST 1", projects.get(1).name);
		Assert.assertEquals(2, projects.get(2).id);
		Assert.assertEquals("TEST 2", projects.get(2).name);
		Assert.assertEquals(1, projects.get(2).skills.size());
		Assert.assertEquals(1, projects.get(2).skills.get(0).id);
		Assert.assertEquals("JAVA", projects.get(2).skills.get(0).title);
	}
}
