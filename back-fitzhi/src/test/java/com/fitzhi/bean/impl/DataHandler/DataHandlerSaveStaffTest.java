package com.fitzhi.bean.impl.DataHandler;

import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.data.internal.Staff;

import org.junit.Before;
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
 * Test of the method {@link DataHandler#saveStaff(java.util.Map)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "applicationOutDirectory=./target/test-classes/out_dir_datahandler" })
@SpringBootTest
@DirtiesContext(classMode=ClassMode.BEFORE_CLASS)
public class DataHandlerSaveStaffTest {
 
	@Autowired
	DataHandler dataHandler;

	@MockBean
	ShuffleService shuffleService;

	@Value("${applicationOutDirectory}")
	private String saveDir;

	@Before
	public void before() throws Exception {
		Path path = Paths.get(saveDir).resolve("staff.json");
		if (Files.exists(path)) {
			Files.delete(path);
		}
	}

	@Test
	public void save() throws Exception {

		createIfNeededDirectory(saveDir);

		when(shuffleService.isShuffleMode()).thenReturn(false);
		
		Map<Integer, Staff> company = new HashMap<>();
		company.put(1111, new Staff(1111, "firstName", "lastName", "nickName", "login", "email", "level"));

		dataHandler.saveStaff(company);

		Path path = Paths.get(saveDir).resolve("staff.json");
		Assert.assertTrue(Files.exists(path));
	}

	@Test
	public void doNotSave() throws Exception {

		when(shuffleService.isShuffleMode()).thenReturn(true);
		
		Map<Integer, Staff> company = new HashMap<>();
		company.put(1111, new Staff(1111, "firstName", "lastName", "nickName", "login", "email", "level"));

		dataHandler.saveStaff(company);

		Path path = Paths.get(saveDir).resolve("staff.json");
		Assert.assertFalse(Files.exists(path));
	}

	@After
	public void after() throws Exception {
		Path path = Paths.get(saveDir).resolve("staff.json");
		if (Files.exists(path)) {
			Files.delete(path);
		}
	}

	private void createIfNeededDirectory(String dir) {

		Path path = Paths.get(saveDir);
		if (Files.notExists(path)) {
			try {
				Files.createDirectories(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
