package com.fitzhi.bean.impl.DataHandler;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.data.internal.Skill;
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
@TestPropertySource(properties = { "applicationOutDirectory=path/wtf" })
@SpringBootTest
@DirtiesContext(classMode=ClassMode.BEFORE_CLASS)
public class DataHandlerSaveSkillExceptionTest {
 
	@Autowired
	DataHandler dataHandler;

	@MockBean
	ShuffleService shuffleService;

	@Value("${applicationOutDirectory}")
	private String saveDir;

	@Test (expected = ApplicationException.class)
	public void handleIOException() throws Exception {

		when(shuffleService.isShuffleMode()).thenReturn(false);
		
		Map<Integer, Skill> skills = new HashMap<>();
		skills.put(1111, new Skill(1111, "one one one one"));

		dataHandler.saveSkills(skills);
	}

}
