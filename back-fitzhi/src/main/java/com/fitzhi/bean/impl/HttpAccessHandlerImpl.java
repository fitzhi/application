package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.CODE_HTTP_ERROR;
import static com.fitzhi.Error.CODE_HTTP_NOT_CONNECTED;
import static com.fitzhi.Error.MESSAGE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_NOT_CONNECTED;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.exception.ApplicationException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the data access handler.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
@Slf4j
@Profile("slave")
public class HttpAccessHandlerImpl<T> implements HttpAccessHandler<T> {
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private HttpConnectionHandler httpConnectionHandler;

	HttpClient client = null;

	@Override
	public Map<Integer, T> loadMap(String url, TypeReference<Map<Integer, T>> typeReference) throws ApplicationException {
		try {
			HttpClient client = httpClient();
			HttpGet httpGet = new HttpGet(url);
			
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}
			httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + Base64.getEncoder().encodeToString((httpConnectionHandler.getToken().getAccess_token()).getBytes()));
			
			HttpResponse response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				Map<Integer, T> theMap = objectMapper.readValue(EntityUtils.toString(response.getEntity()), typeReference);
				return theMap;
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format("Http error with %s %s %s", url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
				}
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
		} catch (final IOException ioe) {
			throw new ApplicationException(
				CODE_HTTP_CLIENT_ERROR,  MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), ioe);
		}
	}

	@Override
	public List<T> loadList(String url, TypeReference<List<T>> typeReference) throws ApplicationException {
		try {
			HttpClient client = httpClient();
			HttpGet httpGet = new HttpGet(url);
			
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}
			httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + Base64.getEncoder().encodeToString((httpConnectionHandler.getToken().getAccess_token()).getBytes()));

			HttpResponse response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				List<T> theList = objectMapper.readValue(EntityUtils.toString(response.getEntity()), typeReference);
				return theList;
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format("Http error with %s %s %s", url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
				}
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
		} catch (final IOException ioe) {
			throw new ApplicationException(CODE_HTTP_CLIENT_ERROR, MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), ioe);
		}
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