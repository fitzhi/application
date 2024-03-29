package com.fitzhi.bean.impl.DataHandler;

import java.io.File;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.DetectedExperience;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * Testing the method {@link DataHandler#saveDetectedExperiences(com.fitzhi.data.internal.Project, com.fitzhi.data.internal.ProjectDetectedExperiences)}
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataHandlerLoadSaveDetectedExperiencesTest {
  
	@Autowired
	DataHandler dataHandler;

	/**
	 * Testing the successfull save.
	 * @throws ApplicationException
	 */
	@Test
	public void saveOK() throws ApplicationException {
		Project project = new Project(1790, "One year after the revolution");
		ProjectDetectedExperiences experiences = new ProjectDetectedExperiences();
		experiences.add(DetectedExperience.of(1, 1790, new Author("gbale", "gareth.bale@failed-penalty.com")));
		dataHandler.saveDetectedExperiences(project, experiences);
		File f = new File("./src/test/resources/out_dir/changes-data/1790-project-detected-experiences.json");
		if (log.isDebugEnabled()) {
			log.debug(String.format("dataHandler.saveDetectedExperiences into %s ", f.getAbsolutePath()));
		}
		Assert.assertTrue("Test that we correctly save the detected experiences into their dedicated file", f.exists());
	}

	/**
	 * Testing the successfull load.
	 * @throws ApplicationException
	 */
	@Test
	public void loadOk() throws ApplicationException {
		Project project = new Project(1788, "One year before the revolution");
		ProjectDetectedExperiences experiences = dataHandler.loadDetectedExperiences(project);
		Assert.assertEquals(1, experiences.content().size());
		Assert.assertEquals(1788, experiences.content().get(0).getIdProject());
		Assert.assertEquals(10, experiences.content().get(0).getCount());
		Assert.assertEquals("frvidal", experiences.content().get(0).getAuthor().getName());
		Assert.assertEquals("frederic.vidal@fitzhi.com", experiences.content().get(0).getAuthor().getEmail());
	}

	/**
	 * Testing the failed load.
	 * @throws ApplicationException
	 */
	@Test
	public void loadKO() throws ApplicationException {
		Project project = new Project(666, "Unknown project");
		Assert.assertNull(dataHandler.loadDetectedExperiences(project));
	}

}
