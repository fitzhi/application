/**
 * 
 */
package fr.skiller.bean.impl;


import static org.junit.Assume.assumeTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.Administration;
import fr.skiller.exception.SkillerException;
/**
 * Test the administration bean.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AdministrationVeryFirstConnectionTest {

	@Autowired
	Administration administration;

	/**
	 * Directory where the footprint of the very first solution is made.
	 */
	@Value("${applicationOutDirectory}")
	private String rootLocation;

	@Test
	public void testIsFirstConnection() {
		assumeTrue(administration.isVeryFirstConnection());
	}

	@Test
	public void testSaveIsFirstConnection() throws SkillerException {
		Assert.assertTrue(
				"No file firstConnection.txt created", 
				administration.isVeryFirstConnection());
		administration.saveVeryFirstConnection();
		Assert.assertFalse(
				"First connection file has been created", 
				administration.isVeryFirstConnection());
	}
	
	@After
	public void after()  {
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		Files.delete(firstConnection.toFile());
	}
	
}
