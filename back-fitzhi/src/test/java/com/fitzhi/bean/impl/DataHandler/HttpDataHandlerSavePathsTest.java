package com.fitzhi.bean.impl.DataHandler;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.DataHandler.PathsType;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.impl.HttpDataHandlerImpl;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Token;
import com.fitzhi.exception.ApplicationException;

import org.junit.Before;
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

/**
 * Test of some methods of {@link HttpDataHandlerImpl}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {"applicationUrl=http://mock-url", "organization=fitzhi" })
@ActiveProfiles("slave")
public class HttpDataHandlerSavePathsTest {
 
	@Autowired
	DataHandler dataHandler;

	@MockBean
	HttpAccessHandler<String> httpAccessHandler;

	@MockBean
	HttpConnectionHandler httpConnectionHandler;

	@MockBean
	ShuffleService shuffleService;

	@Before
	public void before() {
		when(httpConnectionHandler.isConnected()).thenReturn(true);
		when(httpConnectionHandler.getToken()).thenReturn(new Token("access_token", "refresh_token", "token_type", 100, "scope"));
	}

	@Test
	public void notConnected() throws ApplicationException {
		when(httpConnectionHandler.isConnected()).thenReturn(false);
		doNothing().when(httpConnectionHandler).connect(anyString(), anyString());
		doNothing().when(httpAccessHandler).putList(anyString(), anyList());

		Project project = new Project(1805, "Austerlitz");
		dataHandler.savePaths(project, mockListData(), PathsType.PATHS_ADDED);

		verify(httpAccessHandler, times(1)).putList(anyString(), anyList()); 
		verify(httpConnectionHandler, times(1)).connect(anyString(), anyString());
	}

	@Test
	public void saveAddedPaths() throws ApplicationException {
		Project project = new Project(1805, "Austerlitz");
		doNothing().when(httpAccessHandler).putList(anyString(), anyList());

		dataHandler.savePaths(project, mockListData(), PathsType.PATHS_ADDED);
		verify(httpAccessHandler, times(1)).putList("http://mock-url/api/project/1805/pathsAdded", mockListData()); 
		verify(httpConnectionHandler, never()).connect(anyString(), anyString());
	}

	@Test
	public void saveModifiedPaths() throws ApplicationException {
		Project project = new Project(1805, "Austerlitz");
		doNothing().when(httpAccessHandler).putList(anyString(), anyList());

		dataHandler.savePaths(project, mockListData(), PathsType.PATHS_MODIFIED);
		verify(httpAccessHandler, times(1)).putList("http://mock-url/api/project/1805/pathsModified", mockListData()); 
		verify(httpConnectionHandler, never()).connect(anyString(), anyString());
	}

	@Test
	public void saveCandidatePaths() throws ApplicationException {
		Project project = new Project(1805, "Austerlitz");
		doNothing().when(httpAccessHandler).putList(anyString(), anyList());

		dataHandler.savePaths(project, mockListData(), PathsType.PATHS_CANDIDATE);
		verify(httpAccessHandler, times(1)).putList("http://mock-url/api/project/1805/pathsCandidate", mockListData()); 
		verify(httpConnectionHandler, never()).connect(anyString(), anyString());
	}

	private List<String> mockListData() {
		List<String> l = new ArrayList<>();
		l.add("path/one");
		l.add("path/two");
		l.add("path/three");
		return l;
	}

}
