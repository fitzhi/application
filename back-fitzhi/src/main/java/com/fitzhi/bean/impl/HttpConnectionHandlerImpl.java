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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
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
	
	private static final String HTTP_ERROR_WITH_S_S_S = "Http error with %s %s %s.";

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
	 * Socket timeout.
	 */
	@Value("${socket.timeout}")
	private int timeout;

	/**
	 * The authentication token sent back by the server.
	 */
	private Token token = null;

	private HttpClient client;
	
	private final static String BASIC = "Basic ";

	private String clientName = "fitzhi-trusted-client";

	private String secret = "secret";

	private String login;

	private String pass;

	@Override
	public void connect(String login, String pass) throws ApplicationException {
		// We initialize the HttpClient object.
		client = null;
		final String url = applicationUrl + "/oauth/token";
		if (log.isDebugEnabled()) {
			log.debug(String.format("Login %s into %s.", login, url));
		}
		try {
			HttpPost post = new HttpPost(url);
	
			post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.toString());
			post.setHeader(HttpHeaders.AUTHORIZATION, BASIC + Base64.getEncoder().encodeToString((clientName + ":" + secret).getBytes()));
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("username", login));
			params.add(new BasicNameValuePair("password", pass));
			params.add(new BasicNameValuePair("grant_type", "password"));
			post.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse response = httpClient().execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				token = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Token.class);
				// We saved the login/pass pair in order to be able to reconnect() the application if needed.
				this.login = login;
				this.pass = pass;
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format(HTTP_ERROR_WITH_S_S_S, url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
				}
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
			
		} catch (final IOException ioe) {
			throw new ApplicationException(CODE_HTTP_CLIENT_ERROR, MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), ioe);
		}
	}

	@Override
	public void reconnect() throws ApplicationException {
		connect(login, pass);
	}

	@Override
	public void refreshToken() throws ApplicationException {
		final String url = applicationUrl + "/oauth/token";
		if (log.isDebugEnabled()) {
			log.debug("Refreshing token.");
		}
		try {
			HttpPost post = new HttpPost(url);
	
			post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.toString());
			post.setHeader(HttpHeaders.AUTHORIZATION, BASIC + Base64.getEncoder().encodeToString((clientName + ":" + secret).getBytes()));
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("refresh_token", token.getRefresh_token()));
			params.add(new BasicNameValuePair("grant_type", "refresh_token"));
			post.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse response = httpClient().execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				token = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Token.class);
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format(HTTP_ERROR_WITH_S_S_S, url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
				}
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
			
		} catch (final IOException ioe) {
			throw new ApplicationException(CODE_HTTP_CLIENT_ERROR, MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), ioe);
		}
	}

	@Override
	public boolean isConnected() {
		return (token != null);
	}

	@Override
	public Token getToken() {
		return token;
	}

	@Override
	public void setHttpClient(HttpClient httpClient) {
		client = httpClient;
	}

	@Override
	public HttpClient httpClient() {
		if (client == null) {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout * 1000).build();
			SocketConfig sockerConfig = SocketConfig.custom().setSoTimeout(timeout * 1000).build();
			client = HttpClientBuilder.create().setDefaultSocketConfig(sockerConfig).setDefaultRequestConfig(config).build();
			if (log.isInfoEnabled()) {
				log.info(MessageFormat.format("HttpClient connected with a socket timeout of {0}s.", timeout));
			}
		} 
		return client;
	}

}
