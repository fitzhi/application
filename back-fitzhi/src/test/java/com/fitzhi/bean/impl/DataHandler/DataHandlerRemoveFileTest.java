package com.fitzhi.bean.impl.DataHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.impl.FileDataHandlerImpl;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of the method {@link DataHandler#saveStaff(java.util.Map)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.BEFORE_CLASS)
public class DataHandlerRemoveFileTest {
 
	@Test
	public void remove() throws Exception {
		Path path = Files.createTempFile("test", "tmp");
		Assert.assertTrue(Files.exists(path));
		FileDataHandlerImpl.removeFile(path.toFile());
		Assert.assertFalse(Files.exists(path));
	}

	/**
	 * DO not throw an error message if the file does not exist.
	 * @throws ApplicationException
	 */
	@Test
	public void doNotRemove() throws ApplicationException {
		File f = new File("/path/unknown/file");
		Assert.assertFalse(Files.exists(f.toPath()));
		FileDataHandlerImpl.removeFile(f);
	}

	@Test (expected = ApplicationException.class)
	public void error() throws Exception {
		File f = new File("./target");
		FileDataHandlerImpl.removeFile(f);
	}
}
