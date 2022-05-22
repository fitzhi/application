package com.fitzhi.bean.impl.DataHandler;

import java.time.LocalDate;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.exception.ApplicationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of the method {@link DataHandler#saveSkills(Map))}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "applicationOutDirectory=./target/test-classes/out_dir_corrupted_data" })
@SpringBootTest
@DirtiesContext(classMode=ClassMode.BEFORE_CLASS)
public class DataHandlerCorruptedDataTest {
 
	@Autowired
	DataHandler dataHandler;

	@MockBean
	ShuffleService shuffleService;

	@Value("${applicationOutDirectory}")
	private String saveDir;


	@Test (expected = ApplicationException.class)
	public void loadSkills() throws Exception {
		dataHandler.loadSkills();
	}

	@Test (expected = ApplicationException.class)
	public void loadStaff() throws Exception {
		dataHandler.loadStaff();
	}

	@Test (expected = ApplicationException.class)
	public void loadProjects() throws Exception {
		dataHandler.loadProjects();
	}

	@Test (expected = ApplicationException.class)
	public void loadSkillsConstellations() throws Exception {
		dataHandler.loadSkillsConstellations(LocalDate.of(2022, 5, 8));
	}

}
