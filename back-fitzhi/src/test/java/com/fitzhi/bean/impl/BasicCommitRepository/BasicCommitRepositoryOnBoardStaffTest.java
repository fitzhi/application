package com.fitzhi.bean.impl.BasicCommitRepository;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.CommitRepository;

@RunWith(SpringRunner.class)

@SpringBootTest
@ContextConfiguration(initializers = BasicCommitRepositoryOnBoardStaffTest.Initializer.class)
@TestPropertySource(properties = { "cache_duration=100000" }) 
public class BasicCommitRepositoryOnBoardStaffTest {

	/**
	 * This initializer is there to setup the {@code cache.working.dir} with the appropriate path for the OS environment.
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(
				ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
					"cache.working.dir=" +  MessageFormat.format(".{0}src{0}test{0}resources{0}cacheDirRepository{0}", File.separator))
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
	
	
	@Autowired
	CacheDataHandler cacheDataHandler;
	
	@Autowired
	StaffHandler staffHandler;
	
	@Test
	public void testNominal() throws IOException {
		
		Project project = new Project(1917, "The Red Rev project");
		CommitRepository repository = cacheDataHandler.getRepository(project);
		
		Staff staff = new Staff(1, "Frédéric", "VIDAL", "altF4", "fvidal", "frvidal@void.com", "OIM");
		
		
		Assert.assertEquals(4,
				repository.getRepository().get("fr/test/test.java").operations.size());
		Assert.assertEquals(3,
				repository.getRepository().get("com/test.java").operations.size());
		
		Assert.assertEquals(1,
				repository.getRepository().get("com/test.java").operations.get(0).getIdStaff());
		Assert.assertEquals(-1,
				repository.getRepository().get("com/test.java").operations.get(1).getIdStaff());
		Assert.assertEquals(-1,
				repository.getRepository().get("com/test.java").operations.get(2).getIdStaff());
		
		repository.onBoardStaff(staffHandler, staff);
		
		Assert.assertEquals(2,
				repository.getRepository().get("fr/test/test.java").operations.size());
		
		Assert.assertEquals(2,
				repository.getRepository().get("com/test.java").operations.size());
		Assert.assertEquals(1,
				repository.getRepository().get("com/test.java").operations.get(0).getIdStaff());
		Assert.assertEquals(1,
				repository.getRepository().get("com/test.java").operations.get(1).getIdStaff());
		
	}
	
}
