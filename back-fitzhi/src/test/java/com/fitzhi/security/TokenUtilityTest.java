package com.fitzhi.security;

import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import com.fitzhi.data.internal.github.GithubIdentity;
import com.fitzhi.data.internal.github.GithubToken;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.token.util.TokenUtility;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;

/**
 * Test of the method {#link
 * {@link com.fitzhi.security.token.util.TokenUtility#httpLoadToken(String, Class, String...)}
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
	public void nominalTokenLoad() throws ApplicationException, IOException {
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		try {
			String body = "{ access_token: \"123AZERTY\", token_type: \"Bearer\", scope: \"read-write\" }";
			when(httpResponse.getEntity()).thenReturn(new StringEntity(body, ContentType.APPLICATION_JSON));
			when(httpClient.execute(Mockito.any(HttpPost.class))).thenReturn(httpResponse);
		} catch (IOException ioe) {
			Assert.fail(ioe.getMessage());
		}

		GithubToken token = new TokenUtility<GithubToken>().httpLoadToken(HttpMethod.POST, httpClient, "url", GithubToken.class, Collections.<String, String>emptyMap());
		Assert.assertEquals("123AZERTY", token.getAccess_token());
		Assert.assertEquals("Bearer", token.getToken_type());
		Assert.assertEquals("read-write", token.getScope());
	}

	@Test
	public void nominalIdentityLoad() throws ApplicationException, IOException {

		final File file = new File ("./src/test/resources/template-identity-github.json");

		final FileReader reader = new FileReader(file);
		BufferedReader br = new BufferedReader(reader);
		String body = br.lines().collect(Collectors.joining("\n"));
		br.close();
		
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		try {
			when(httpResponse.getEntity()).thenReturn(new StringEntity(body, ContentType.APPLICATION_JSON));
			when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
		} catch (IOException ioe) {
			Assert.fail(ioe.getMessage());
		}

		GithubIdentity identity = new TokenUtility<GithubIdentity>().httpLoadToken(
			HttpMethod.GET, 
			httpClient, "url", GithubIdentity.class, 
			Collections.<String, String>emptyMap());
		Assert.assertEquals("123456789", identity.getId());
		Assert.assertEquals("john.lennon.perso@gmail.com", identity.getEmail());
		Assert.assertEquals("John LENNON", identity.getName());
		Assert.assertEquals("jlennon", identity.getLogin());

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
		new TokenUtility<GithubToken>().httpLoadToken(HttpMethod.POST, "url", GithubToken.class, Collections.<String, String>emptyMap());
	}

}
