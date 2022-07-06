package com.fitzhi.bean.impl.DataHandler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.bean.impl.HttpDataHandlerImpl;
import com.fitzhi.data.internal.Project;

/**
 * Test of the method {@link HttpDataHandlerImpl#saveStaff(Project, Map)}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {"applicationUrl=http://mock-url", "organization=fitzhi" })
@ActiveProfiles("slave")
public class HttpDataHandlerSaveProjectTest {
 
	@Autowired
	DataHandler dataHandler;

	@MockBean
	HttpAccessHandler<Void> httpAccessHandler;

	@MockBean
	HttpConnectionHandler httpConnectionHandler;

	@Test
	public void saveConnectedProject() throws Exception {
		when(httpConnectionHandler.isConnected()).thenReturn(true);
		Project p = new Project(1789, "The French revolution");
		dataHandler.saveProject(p);
		verify(httpAccessHandler, times(1)).post("http://mock-url/api/project/", p);
	}

	@Test
	public void saveNotConnectedProject() throws Exception {
		when(httpConnectionHandler.isConnected()).thenReturn(false);
		doNothing().when(httpConnectionHandler).connect(anyString(), anyString());

		Project p = new Project(1789, "The French revolution");
		dataHandler.saveProject(p);
		verify(httpConnectionHandler, times(1)).connect(anyString(), anyString());
		verify(httpAccessHandler, times(1)).post("http://mock-url/api/project/", p);
	}
}
