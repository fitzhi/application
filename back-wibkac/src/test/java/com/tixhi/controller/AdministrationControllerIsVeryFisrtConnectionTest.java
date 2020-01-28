/**
 * 
 */
package com.tixhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.util.Files;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdministrationControllerIsVeryFisrtConnectionTest {

	@Autowired
	private MockMvc mvc;

	/**
	 * Directory where the footprint of the very first solution is made.
	 */
	@Value("${applicationOutDirectory}")
	private String rootLocation;
	
	@Test
	public void isFirstConnection() throws Exception {
		this.mvc.perform(get("/api/admin/isVeryFirstConnection"))
		.andExpect(status().isOk())
		.andExpect(content().string(CoreMatchers.containsString("true")));
	}

	@Test
	public void isNotFirstConnection() throws Exception {
		final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		if (!firstConnection.toFile().createNewFile()) {
			Assert.fail("connection.txt already exists");
		}

		this.mvc.perform(get("/api/admin/isVeryFirstConnection"))
		.andExpect(status().isOk())
		.andExpect(content().string(CoreMatchers.containsString("false")));
	}
	
	@After
	public void after()  {
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		Files.delete(firstConnection.toFile());
	}
	
}