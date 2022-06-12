package com.fitzhi.bean.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.exception.ApplicationException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"applicationUrl=http://zorglub.com", "organization=fitzhi" })
@DirtiesContext (classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("slave")
public class HttpConnectionHandlerTest {
	
	@Autowired()
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

  	@Test
	public void ok() throws Exception {
		// When
		when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getEntity()).thenReturn(new StringEntity(
			"{\"access_token\": \"access_token\", \"refresh_token\": \"refresh_token\", \"token_type\": \"Bearer\", \"expires_in\": 1789, \"scope\": \"read, write\"}"));

		httpConnectionHandler.setHttpClient(httpClient);
		httpConnectionHandler.connect("admin", "nope");

		Assert.assertEquals("access_token", httpConnectionHandler.getToken().getAccess_token());
		Assert.assertEquals("refresh_token", httpConnectionHandler.getToken().getRefresh_token());
		Assert.assertEquals("read, write", httpConnectionHandler.getToken().getScope());
		Assert.assertEquals("Bearer", httpConnectionHandler.getToken().getToken_type());
		Assert.assertEquals(1789, httpConnectionHandler.getToken().getExpires_in());

	}

	@Test (expected = ApplicationException.class)
	public void networkError() throws Exception {
		httpConnectionHandler.connect("admin", "nope");
	}

	@Test
	public void notConnected() throws Exception {
		Assert.assertFalse(httpConnectionHandler.isConnected());
	}

	@Test
	public void connected() throws Exception {
		// When
		when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getEntity()).thenReturn(new StringEntity(
			"{\"access_token\": \"access_token\", \"refresh_token\": \"refresh_token\", \"token_type\": \"Bearer\", \"expires_in\": 1789, \"scope\": \"read, write\"}"));

		httpConnectionHandler.setHttpClient(httpClient);
		httpConnectionHandler.connect("admin", "nope");

		Assert.assertTrue(httpConnectionHandler.isConnected());
	}

}
