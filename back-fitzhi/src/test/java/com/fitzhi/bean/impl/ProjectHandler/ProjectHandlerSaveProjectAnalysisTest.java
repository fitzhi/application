package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectAnalysis;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#saveProjectAnalysis(com.fitzhi.data.internal.ProjectAnalysis)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerSaveProjectAnalysisTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	/**
	 * Critical error if the project does not exist anymore.
	 * @throws ApplicationException
	 */
	@Test (expected = ApplicationRuntimeException.class)
	public void projectNotFound() throws ApplicationException {
		ProjectAnalysis pa = new ProjectAnalysis(Integer.MAX_VALUE);
		projectHandler.saveProjectAnalysis(pa);
	}


	/**
	 * Update the dataUpdated flag when the project is updated.
	 * @throws ApplicationException
	 */
	@Test 
	public void dataUpdated() throws ApplicationException {
		Project project = new Project(312, "Milvius battle");
		ProjectHandler spy = spy(projectHandler);
		when(spy.lookup(312)).thenReturn(project);
		
		ProjectAnalysis pa = new ProjectAnalysis(312);
		spy.saveProjectAnalysis(pa);

		Assert.assertTrue(spy.isDataUpdated());

	}

	/**
	 * Update few data analysis into the project container.
	 * @throws ApplicationException
	 */
	@Test 
	public void updateStaffEvaluation() throws ApplicationException {
		Project project = new Project(312, "Milvius battle");
		project.setStaffEvaluation(10);
		ProjectHandler spy = spy(projectHandler);
		when(spy.lookup(312)).thenReturn(project);
		
		ProjectAnalysis pa = new ProjectAnalysis(312);
		pa.setStaffEvaluation(20);

		spy.saveProjectAnalysis(pa);

		Assert.assertEquals(20, project.getStaffEvaluation());

	}


	/**
	 * Update few data analysis into the project container.
	 * @throws ApplicationException
	 */
	@Test 
	public void updateEcosystem() throws ApplicationException {
		Project project = new Project(312, "Milvius battle");
		List<Integer> ecosystems = new ArrayList<>();
		ecosystems.add(1);
		ecosystems.add(2);
		project.setEcosystems(ecosystems);
		ProjectHandler spy = spy(projectHandler);
		when(spy.lookup(312)).thenReturn(project);
		
		ProjectAnalysis pa = new ProjectAnalysis(312);
		ecosystems = new ArrayList<>();
		ecosystems.add(3);
		ecosystems.add(4);
		pa.setEcosystems(ecosystems);

		spy.saveProjectAnalysis(pa);

		Assert.assertEquals(2, project.getEcosystems().size());
		Assert.assertEquals(3, project.getEcosystems().get(0).intValue());
		Assert.assertEquals(4, project.getEcosystems().get(1).intValue());

	}

	/**
	 * Update few data analysis into the project container.
	 * @throws ApplicationException
	 */
	@Test 
	public void updateProjectSkills() throws ApplicationException {
		Project project = new Project(312, "Milvius battle");
		Map<Integer, ProjectSkill> skills = new HashMap<Integer, ProjectSkill>();
		skills.put(1, new ProjectSkill(1, 10, 100));
		skills.put(2, new ProjectSkill(2, 20, 200));
		project.setSkills(skills);
		ProjectHandler spy = spy(projectHandler);
		when(spy.lookup(312)).thenReturn(project);
		
		ProjectAnalysis pa = new ProjectAnalysis(312);
		skills = new HashMap<Integer, ProjectSkill>();
		skills.put(1, new ProjectSkill(1, 100, 1000));
		skills.put(3, new ProjectSkill(3, 3, 30));
		skills.put(4, new ProjectSkill(4, 4, 40));
		pa.setSkills(skills);

		spy.saveProjectAnalysis(pa);

		Assert.assertEquals(4, project.getSkills().size());

		// We update existing record if necessary.
		Assert.assertTrue(project.getSkills().containsKey(1));
		Assert.assertEquals(100, project.getSkills().get(1).getNumberOfFiles());
		Assert.assertEquals(1000, project.getSkills().get(1).getTotalFilesSize());

		// We do NOT update existing record if they already exist.
		Assert.assertTrue(project.getSkills().containsKey(2));
		Assert.assertEquals(20, project.getSkills().get(2).getNumberOfFiles());
		Assert.assertEquals(200, project.getSkills().get(2).getTotalFilesSize());

		// We add the new detected skills.
		Assert.assertTrue(project.getSkills().containsKey(3));
		Assert.assertTrue(project.getSkills().containsKey(4));

	}


	/**
	 * Update few data analysis into the project container.
	 * @throws ApplicationException
	 */
	@Test 
	public void updateGhosts() throws ApplicationException {
		Project project = new Project(312, "Milvius battle");
		List<Ghost> ghosts = new ArrayList<>();
		ghosts.add(new Ghost("one", 1, false));
		ghosts.add(new Ghost("two", -1, true));
		project.setGhosts(ghosts);
		ProjectHandler spy = spy(projectHandler);
		when(spy.lookup(312)).thenReturn(project);
		
		ProjectAnalysis pa = new ProjectAnalysis(312);
		ghosts = new ArrayList<>();
		ghosts.add(new Ghost("one", false));
		ghosts.add(new Ghost("three", 4, false));
		pa.setGhosts(ghosts);

		spy.saveProjectAnalysis(pa);

		Assert.assertEquals(3, project.getGhosts().size());
		Assert.assertTrue(project.getGhosts().stream().anyMatch(g -> g.getPseudo().equals("one")));
		Ghost ghost = project.getGhosts().stream().filter(g -> g.getPseudo().equals("one")).findAny().get();
		Assert.assertEquals(1, ghost.getIdStaff());
		Assert.assertFalse(ghost.isTechnical());
		Assert.assertTrue(project.getGhosts().stream().anyMatch(g -> g.getPseudo().equals("two")));
		Assert.assertTrue(project.getGhosts().stream().anyMatch(g -> g.getPseudo().equals("three")));
	}

}
