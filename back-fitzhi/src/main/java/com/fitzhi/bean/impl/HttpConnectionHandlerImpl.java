package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.CODE_HTTP_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_ERROR;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.data.internal.Token;
import com.fitzhi.exception.ApplicationException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the connection handler.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
@Slf4j
@Profile("slave")
public class HttpConnectionHandlerImpl implements HttpConnectionHandler {
	
	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * URL of the backend which hosts the main application.
	 */
	@Value("${applicationUrl}")
	private String applicationUrl;

	/**
	 * Organization name. This name is unique and therefore can be considered as an ID.
	 */
	@Value("${organization}")
	private String organization;

	/**
	 * The authentication token sent back by the server.
	 */
	private Token token;

	private HttpClient client;
	
	@Override
	public void connection(String login, String pass) throws ApplicationException {
		final String url = applicationUrl + "/oauth/token";
		if (log.isDebugEnabled()) {
			log.debug(String.format("Login %s into %s.", login, url));
		}
		try {
			HttpClient client = httpClient();
			HttpPost post = new HttpPost(url);
	
			post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.toString());
			post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(("fitzhi-trusted-client:secret").getBytes()));

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("username", login));
			params.add(new BasicNameValuePair("password", pass));
			params.add(new BasicNameValuePair("grant_type", "password"));
			post.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				token = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Token.class);
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format("Http error with %s %s %s.", url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
				}
				System.out.println(response);
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
		} catch (final IOException ioe) {
			throw new ApplicationException(CODE_HTTP_CLIENT_ERROR, MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), ioe);
		}
	}

	@Override
	public Token getToken() {
		return token;
	}

	@Override
	public void setHttpClient(HttpClient httpClient) {
		client = httpClient;
	}

	private HttpClient httpClient() {
		if (client == null) {
			client = HttpClientBuilder.create().build();
		} 
		return client;
	}

}
