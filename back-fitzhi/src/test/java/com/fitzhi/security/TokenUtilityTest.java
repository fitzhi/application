package com.fitzhi.security;

import static org.mockito.Mockito.when;

import java.io.IOException;

import com.fitzhi.data.internal.GithubToken;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.token.TokenUtility;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test of the method {#link
 * {@link com.fitzhi.security.token.TokenUtility#httpLoadToken(String, Class, String...)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class TokenUtilityTest {

	private CloseableHttpClient httpClient;
	private CloseableHttpResponse httpResponse;
	private StatusLine statusLine;
	
	@Before
	public void before() {
		httpClient = Mockito.mock(CloseableHttpClient.class);
		httpResponse = Mockito.mock(CloseableHttpResponse.class);
		statusLine = Mockito.mock(StatusLine.class);
	}

	@Test
	public void nominal() throws ApplicationException, IOException {
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		try {
			String body = "{ access_token: \"123AZERTY\", token_type: \"Bearer\", scope: \"read-write\" }";
			when(httpResponse.getEntity()).thenReturn(new StringEntity(body, ContentType.APPLICATION_JSON));
			when(httpClient.execute(Mockito.any(HttpPost.class))).thenReturn(httpResponse);
		} catch (IOException ioe) {
			Assert.fail(ioe.getMessage());
		}

		GithubToken token = new TokenUtility<GithubToken>().httpLoadToken(httpClient, "url", GithubToken.class);
		Assert.assertEquals("123AZERTY", token.getAccess_token());
		Assert.assertEquals("Bearer", token.getToken_type());
		Assert.assertEquals("read-write", token.getScope());
	}

	@Test(expected = ApplicationException.class)
	public void postFailed() throws IOException, ApplicationException {
		when(statusLine.getStatusCode()).thenReturn(500);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		try {
			when(httpClient.execute(Mockito.any(HttpPost.class))).thenReturn(httpResponse);
		} catch (IOException ioe) {
			Assert.fail(ioe.getMessage());
		}
		new TokenUtility<GithubToken>().httpLoadToken(httpClient, "url", GithubToken.class);
	}

}
