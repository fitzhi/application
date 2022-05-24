package com.fitzhi.bean.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing the class {@link HttpAccessHandlerImpl}.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"applicationUrl=my-mock-url", "organization=fitzhi" })
@ActiveProfiles("slave")
public class HttpAccessHandlerLoadListTest {
 
	@Autowired
	HttpAccessHandler<String> httpAccessHandler;
  
	@Autowired
	HttpAccessHandler<Project> httpAccessHandlerProject;
	
	private HttpClient httpClient;
	private HttpResponse httpResponse;
	private StatusLine statusLine;
 
	@Before
	public void before() {
		//
		// Given
		httpClient = mock(HttpClient.class);
		httpResponse = mock(HttpResponse.class);
		statusLine = mock(StatusLine.class);
	}

	@Test (expected = ApplicationException.class)
	public void loadServerError() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(401);
		when(statusLine.getReasonPhrase()).thenReturn("a good reason to fail");

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.loadList("url", new TypeReference<List<String>>(){});
	}

	@Test (expected = ApplicationException.class)
	public void loadNetworkError() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpClient.execute(any(HttpGet.class))).thenThrow(new IOException("WTF NETWORK ERROR"));

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.loadList("url", new TypeReference<List<String>>(){});
	}

	@Test
	public void load() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getEntity()).thenReturn(new StringEntity("[ \"one\", \"two\" ]"));

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.loadList("url", new TypeReference<List<String>>(){});
	}


	@Test
	public void loadProjects() throws IOException, ClientProtocolException, ApplicationException {
		File file = new File("./target/test-classes/sample-projects.json");
		System.out.println(file.getAbsolutePath());

		try (BufferedReader br = Files.newBufferedReader(file.toPath())) {

			//br returns as stream and convert it into a List
			StringBuilder sb = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

			// When
			when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
			when(httpResponse.getStatusLine()).thenReturn(statusLine);
			when(statusLine.getStatusCode()).thenReturn(200);
			when(httpResponse.getEntity()).thenReturn(new StringEntity(sb.toString()));

			httpAccessHandlerProject.setHttpClient(httpClient);
			httpAccessHandlerProject.loadList("url", new TypeReference<List<Project>>(){});

		} 
	}
}
