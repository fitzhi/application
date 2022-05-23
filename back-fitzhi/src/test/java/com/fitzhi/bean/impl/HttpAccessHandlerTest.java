package com.fitzhi.bean.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.reflect.TypeToken;

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
public class HttpAccessHandlerTest {
 
	@Autowired
	HttpAccessHandler<String> httpAccessHandler;
  
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
		httpAccessHandler.loadMap("url", new TypeToken<Map<Integer, String>>() {});
	}

	@Test (expected = ApplicationException.class)
	public void loadNetworkError() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpClient.execute(any(HttpGet.class))).thenThrow(new IOException("WTF NETWORK ERROR"));

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.loadMap("url", new TypeToken<Map<Integer, String>>() {});
	}

	@Test
	public void load() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getEntity()).thenReturn(new StringEntity("{ \"1\": \"one\" }"));

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.loadMap("url", new TypeToken<Map<Integer, String>>() {});
	}

}
