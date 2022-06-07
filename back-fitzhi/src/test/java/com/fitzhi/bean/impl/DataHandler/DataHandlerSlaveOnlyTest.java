package com.fitzhi.bean.impl.DataHandler;

import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectAnalysis;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

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
public class DataHandlerSlaveOnlyTest {
 
	@Autowired
	DataHandler dataHandler;

	@Test( expected = ApplicationRuntimeException.class)
	public void saveProjectAnalysisForbidden() throws ApplicationException {
		dataHandler.saveProjectAnalysis(new ProjectAnalysis());
	}

	@Test( expected = ApplicationRuntimeException.class)
	public void saveStaffOnlyForAProjectForbidden() throws ApplicationException {
		dataHandler.saveStaff(new Project(), new HashMap<Integer, Staff>());
	}

}
