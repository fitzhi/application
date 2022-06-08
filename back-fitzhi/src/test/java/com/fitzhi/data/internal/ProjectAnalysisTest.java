package com.fitzhi.data.internal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
/**
 * Testing {@link ProjectAnalysis}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ProjectAnalysisTest {
	
	/**
	 * Test the method creation of a ProjectAnalysis.
	 */
	@Test
	public void getInstance()  {
		Project project = new Project(1515, "Marignan");
		project.getGhosts().add(new Ghost("pseudo", 123, false));
		project.getEcosystems().add(1);
		project.setStaffEvaluation(33);
		project.getSkills().put(1, new ProjectSkill(1));
		
		ProjectAnalysis pa = new ProjectAnalysis(project);
		Assert.assertEquals(1515, pa.getId());
		Assert.assertEquals(1, pa.getGhosts().size());
		Assert.assertEquals(1, pa.getEcosystems().size());
		Assert.assertEquals(1, pa.getSkills().size());
	}


}
