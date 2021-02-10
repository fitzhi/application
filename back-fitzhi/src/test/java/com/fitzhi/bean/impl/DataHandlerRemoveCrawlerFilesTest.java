package com.fitzhi.bean.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Testing the method {@link DataHandler#removeCrawlerFiles}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataHandlerRemoveCrawlerFilesTest {

	private String DIR_PATHS = "src/test/resources/out_dir/pathnames-data/";

	private String DIR_CHANGES = "src/test/resources/out_dir/changes-data/";

	@Autowired
	DataHandler dataHandler;

	@Before
	public void before() throws Exception {
		Path newFilePath = Paths.get(DIR_PATHS + "2021-branch-pathsAll.txt");
		Files.createFile(newFilePath);
		newFilePath = Paths.get(DIR_PATHS + "2021-branch-pathsModified.txt");
		Files.createFile(newFilePath);
		newFilePath = Paths.get(DIR_PATHS + "2021-branch-pathsCandidate.txt");
		Files.createFile(newFilePath);
		newFilePath = Paths.get(DIR_PATHS + "2021-branch-pathsAdded.txt");
		Files.createFile(newFilePath);

		newFilePath = Paths.get(DIR_CHANGES + "2021-changes.csv");
		Files.createFile(newFilePath);

		newFilePath = Paths.get(DIR_CHANGES + "2021-project-layers.json");
		Files.createFile(newFilePath);	}

	@Test
	public void test() throws ApplicationException {
		Project p = new Project(2021, "Covid war");
		p.setBranch("branch");
		log.debug(String.format("Remove all intermediate files for project %d %s", p.getId(), p.getName()));
		dataHandler.removeCrawlerFiles(p);

		Path file = Paths.get(DIR_PATHS + "2021-branch-pathsAll.txt");
		Assert.assertFalse("pathsAll should not exist anymore", file.toFile().exists());

		file = Paths.get(DIR_PATHS + "2021-branch-pathsModified.txt");
		Assert.assertFalse("pathsModified should not exist anymore", file.toFile().exists());

		file = Paths.get(DIR_PATHS + "2021-branch-pathsCandidate.txt");
		Assert.assertFalse("pathsCandidate should not exist anymore", file.toFile().exists());

		file = Paths.get(DIR_PATHS + "2021-branch-pathsAdded.txt");
		Assert.assertFalse("pathsAdded should not exist anymore", file.toFile().exists());

		file = Paths.get(DIR_CHANGES + "2021-changes.csv");
		Assert.assertFalse(
			String.format("Changes file %s should not exist anymore", file.toFile().getAbsolutePath()), 
			file.toFile().exists());

		file = Paths.get(DIR_CHANGES + "2021-project-layers.json");
		Assert.assertFalse(
			String.format("Changes file %s should not exist anymore", file.toFile().getAbsolutePath()), 
			file.toFile().exists());
	}

	@After
	public void after() {
		Path file = Paths.get(DIR_PATHS + "2021-branch-pathsAll.txt");
		file.toFile().delete();
		file = Paths.get(DIR_PATHS + "2021-branch-pathsModified.txt");
		file.toFile().delete();
		file = Paths.get(DIR_PATHS + "2021-branch-pathsCandidate.txt");
		file.toFile().delete();
		file = Paths.get(DIR_PATHS + "2021-branch-pathsAdded.txt");
		file.toFile().delete();
		file = Paths.get(DIR_CHANGES + "2021-changes.csv");
		file.toFile().delete();
		file = Paths.get(DIR_CHANGES + "2021-project-layers.json");
		file.toFile().delete();
	}
}
