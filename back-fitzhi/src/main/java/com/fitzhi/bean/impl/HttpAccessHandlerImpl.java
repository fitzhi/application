package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.CODE_HTTP_ERROR;
import static com.fitzhi.Error.CODE_HTTP_NOT_CONNECTED;
import static com.fitzhi.Error.MESSAGE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_NOT_CONNECTED;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.exception.ApplicationException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
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

	HttpClient httpClient = null;

	@Override
	public Map<Integer, T> loadMap(String url, TypeReference<Map<Integer, T>> typeReference) throws ApplicationException {
		try {
			HttpClient client = (httpClient != null) ? httpClient : HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(url);
			
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}
			httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + httpConnectionHandler.getToken().getAccess_token());
			
			HttpResponse response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return objectMapper.readValue(EntityUtils.toString(response.getEntity()), typeReference);
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format("Http error with %s %s %s", url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
				}
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
		} catch (final IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			throw new ApplicationException(CODE_HTTP_CLIENT_ERROR,  MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), ioe);
		}
	}

	@Override
	public List<T> loadList(String url, TypeReference<List<T>> typeReference) throws ApplicationException {
		try {
			HttpClient client = (httpClient != null) ? httpClient : HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(url);
			
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}
			httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + httpConnectionHandler.getToken().getAccess_token());

			HttpResponse response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return objectMapper.readValue(EntityUtils.toString(response.getEntity()), typeReference);
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format("Http error with %s %s %s", url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
				}
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
		} catch (final IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			throw new ApplicationException(CODE_HTTP_CLIENT_ERROR, MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), ioe);
		}
	}

	@Override
	public T put(String url, Object o, TypeReference<T> typeReference) throws ApplicationException {
		try {
			HttpClient client = (httpClient != null) ? httpClient : HttpClientBuilder.create().build();
			HttpPut httpPut = new HttpPut(url);
			
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}
			httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + httpConnectionHandler.getToken().getAccess_token());

			if (o instanceof String) {
				httpPut.setEntity(new StringEntity((String) o));
				httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
			} else {
				httpPut.setEntity(new StringEntity(objectMapper.writeValueAsString(o)));
				httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8.toString());
			}

			HttpResponse response = client.execute(httpPut);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				if ((response.getHeaders(HttpHeaders.CONTENT_TYPE) != null) && (response.getFirstHeader(HttpHeaders.CONTENT_TYPE) != null)) {
					String contentType = response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
					if (log.isDebugEnabled()) {
						log.debug(String.format("Content Type %s.", contentType));
					}
					if ( (MediaType.APPLICATION_JSON_VALUE.equals (contentType)) || (MediaType.APPLICATION_JSON_UTF8_VALUE.equals (contentType))) {
						return objectMapper.readValue(EntityUtils.toString(response.getEntity()), typeReference);
					}
					if ( (MediaType.TEXT_PLAIN_VALUE.equals (contentType)) ) {
						if (typeReference.getType().equals(String.class)) {
							return cast(response.getEntity());
						} else {
							throw new ApplicationRuntimeException("Not implemented yet!");
						}
					}
					throw new ApplicationRuntimeException(String.format("Content-type %s not implemented yet!", contentType));
				} else {
					throw new ApplicationRuntimeException("No content-type found in response.");
				}
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format("Http error with %s %s %s", url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
				}
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
		} catch (final IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			throw new ApplicationException(CODE_HTTP_CLIENT_ERROR, MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), ioe);
		}
	}

	@Override
	public void putList(String url, List<T> list) throws ApplicationException {
		try {
			HttpClient client = (httpClient != null) ? httpClient : HttpClientBuilder.create().build();
			HttpPut httpPut = new HttpPut(url);
			
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}
			httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + httpConnectionHandler.getToken().getAccess_token());

			httpPut.setEntity(new StringEntity(objectMapper.writeValueAsString(list)));
			httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8.toString());

			HttpResponse response = client.execute(httpPut);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				if (log.isWarnEnabled()) {
					log.warn(String.format("Http error with %s %s %s", url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
				}
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
		} catch (final IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			throw new ApplicationException(CODE_HTTP_CLIENT_ERROR, MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), ioe);
		}
	}

	@SuppressWarnings("unchecked")
	private T cast(Object o) {
		return (T) o;
	}

	@Override
	public void setHttpClient(HttpClient client) {
		httpClient = client;
	}

} 