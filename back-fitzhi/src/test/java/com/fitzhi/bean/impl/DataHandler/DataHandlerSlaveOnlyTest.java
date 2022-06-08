package com.fitzhi.bean.impl.DataHandler;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectAnalysis;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

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
