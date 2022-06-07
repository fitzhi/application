package com.fitzhi.bean.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.data.internal.Token;
import com.fitzhi.exception.ApplicationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing the class {@link HttpAccessHandlerImpl#put(String, Object, TypeReference)}.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"applicationUrl=my-mock-url", "organization=fitzhi" })
@ActiveProfiles("slave")
public class HttpAccessHandlerPutListStringTest {
 
	@Autowired
	HttpAccessHandler<String> httpAccessHandler;
 
	@MockBean
	HttpConnectionHandler httpConnectionHandler;
	
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

	private void injectToken() {
		when(httpConnectionHandler.isConnected()).thenReturn(true);
		when(httpConnectionHandler.getToken()).thenReturn(
			new Token("access_token", "refresh_token", "token_type", 100, "scope"));
	}

	@Test (expected = ApplicationException.class)
	public void loadServerError() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpClient.execute(any(HttpPut.class))).thenReturn(httpResponse);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(httpResponse.getEntity()).thenReturn(new StringEntity("error message"));
		when(statusLine.getStatusCode()).thenReturn(401);
		when(statusLine.getReasonPhrase()).thenReturn("a good reason to fail");

		injectToken();

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.putList("url", mockListData());
	}


	@Test (expected = ApplicationException.class)
	public void loadNetworkError() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpClient.execute(any(HttpPut.class))).thenThrow(new IOException("WTF NETWORK ERROR"));

		injectToken();

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.putList("url", mockListData());
	}

	@Test
	public void empty() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpClient.execute(any(HttpPut.class))).thenReturn(httpResponse);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_NO_CONTENT);

		injectToken();

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.putList("url", Collections.emptyList());
	}

	@Test
	public void save() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpClient.execute(any(HttpPut.class))).thenReturn(httpResponse);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_NO_CONTENT);
		
		injectToken();

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.putList("url", mockListData());
	}

	@Test (expected = ApplicationException.class)
	public void notConnected() throws IOException, ClientProtocolException, ApplicationException{
		// When
		when(httpConnectionHandler.isConnected()).thenReturn(false);

		httpAccessHandler.setHttpClient(httpClient);
		httpAccessHandler.putList("url", mockListData());
	}

	private List<String> mockListData() {
		List<String> l = new ArrayList<>();
		l.add("one");
		l.add("two");
		l.add("three");
		return l;
	}

}
