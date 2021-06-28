package com.fitzhi.bean.impl.ProjectHandler;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Map;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.StaffExperienceTemplate;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#processProjectsExperiences()} in the content parser model.
 *
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { 
	"applicationOutDirectory=src/test/resources/file-content-parser/application",
	"referential.dir=src/test/resources/file-content-parser/referential/"}) 
public class ProjectHandlerProcessProjectsExperiencesContentParserTest {
	
	final int ID_SKILL_FILE_DETECTION = 1;
	final int ID_SKILL_NOT_FILE_DETECTION = 2;

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	StaffHandler staffHandler;

	@Autowired
	SkillHandler skillHandler;

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;

	@Autowired
	DataHandler dataHandler;

	@Before
	public void before() throws IOException, ApplicationException {
		Project project = projectHandler.getProject(1789);
		project.setLocationRepository(Paths.get("../git_repo_for_test/application").toRealPath(LinkOption.NOFOLLOW_LINKS).toString());
	}

	@Test
	public void completeOperation() throws ApplicationException {
		
		projectHandler.processProjectsExperiences();
		Map<StaffExperienceTemplate, Integer> experiences = projectHandler.processGlobalExperiences();
		Assert.assertTrue(experiences.containsKey(StaffExperienceTemplate.of(0, 1789)));
		
		projectHandler.updateStaffSkillLevel(experiences);
		Assert.assertNotNull(staffHandler.lookup(1789));

		Experience exp = staffHandler.lookup(1789).getExperiences().get(0);
		Assert.assertNotNull(exp);
		Assert.assertEquals(3, exp.getSystemLevel());
		Assert.assertEquals(3, exp.getLevel());
	}

}
