package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.CODE_HTTP_ERROR;
import static com.fitzhi.Error.CODE_HTTP_NOT_CONNECTED;
import static com.fitzhi.Error.MESSAGE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_NOT_CONNECTED;

import java.io.IOException;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.exception.ApplicationException;

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
	
	private static final String BROKEN_PIPE_WRITE_FAILED = "Broken pipe (Write failed)";

	private static final String HTTP_ERROR_WITH_S_S_S = "Http error with %s %s %s";

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private HttpConnectionHandler httpConnectionHandler;

	HttpClient httpClient = null;

	/**
	 * This status is hosting the fact that a method from this bean is invoked for the first time.
	 */
	private boolean firstLaunch = true;


	/**
	 * This status is hosting the fact a socket exception has failed, and a reconnection has been made.
	 */
	private boolean firstSocketFailure = true;

	@Override
	public Map<Integer, T> loadMap(String url, TypeReference<Map<Integer, T>> typeReference) throws ApplicationException {
		try {
			
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}

			HttpClient client = (httpClient != null) ? httpClient : httpConnectionHandler.httpClient();
			HttpGet httpGet = new HttpGet(url);

			httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + httpConnectionHandler.getToken().getAccess_token());
			
			HttpResponse response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return objectMapper.readValue(EntityUtils.toString(response.getEntity()), typeReference);
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format(HTTP_ERROR_WITH_S_S_S, url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
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
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}

			HttpClient client = (httpClient != null) ? httpClient : httpConnectionHandler.httpClient();
			HttpGet httpGet = new HttpGet(url);
			
			httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + httpConnectionHandler.getToken().getAccess_token());

			HttpResponse response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return objectMapper.readValue(EntityUtils.toString(response.getEntity()), typeReference);
			} else {
				if (log.isWarnEnabled()) {
					log.warn(String.format(HTTP_ERROR_WITH_S_S_S, url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
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
			
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}

			HttpClient client = (httpClient != null) ? httpClient : httpConnectionHandler.httpClient();
			HttpPut httpPut = new HttpPut(url);

			httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + httpConnectionHandler.getToken().getAccess_token());

			if (o instanceof String) {
				httpPut.setEntity(new StringEntity((String) o));
				httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
			} else {
				httpPut.setEntity(new StringEntity(objectMapper.writeValueAsString(o), ContentType.APPLICATION_JSON));
				httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8.toString());
			}

			HttpResponse response = null;
			try {
				response = client.execute(httpPut);
			} catch (SocketException se) {
				// We force the reconnection, and we retry this method. 
				if (firstSocketFailure && BROKEN_PIPE_WRITE_FAILED.equals(se.getMessage())) {
					if (log.isWarnEnabled()) {
						log.warn("We retry this method after a force reconnection");
					}
					httpClient = null;
					httpConnectionHandler.setHttpClient(null);
					httpConnectionHandler.reconnect();
					firstSocketFailure = false;
					return put(url, o, typeReference);
				}
				throw se;
			}

			int statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode) {
				case HttpStatus.SC_OK:
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
				case HttpStatus.SC_UNAUTHORIZED:
					if (firstLaunch) {
						firstLaunch = false;
						httpConnectionHandler.refreshToken();
						return put(url, o, typeReference);
					} else {
						throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
					}
				case HttpStatus.SC_NO_CONTENT:
					// Operation is successfull but nothing to deserialize from the response.
					return null;
				default:
					if (log.isWarnEnabled()) {
						log.warn(String.format(HTTP_ERROR_WITH_S_S_S, url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
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
			
			// Slave has not been connected to the backend. Cannot proceed the request then.
			if (!httpConnectionHandler.isConnected()) {
				throw new ApplicationException(CODE_HTTP_NOT_CONNECTED, MESSAGE_HTTP_NOT_CONNECTED);
			}

			HttpClient client = (httpClient != null) ? httpClient : httpConnectionHandler.httpClient();
			HttpPut httpPut = new HttpPut(url);

			httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + httpConnectionHandler.getToken().getAccess_token());

			String s = objectMapper.writeValueAsString(list);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Sending %s", s));
			}
			httpPut.setEntity(new StringEntity(s, ContentType.APPLICATION_JSON));
			httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8.toString());

			HttpResponse response = client.execute(httpPut);
			int statusCode = response.getStatusLine().getStatusCode();

			switch (statusCode) {
				case HttpStatus.SC_NO_CONTENT:
					break;
				case HttpStatus.SC_UNAUTHORIZED:
					if (firstLaunch) {
						firstLaunch = false;
						httpConnectionHandler.refreshToken();
						putList(url, list);
					} else {
						throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
					}
					break;
				default:
					if (log.isWarnEnabled()) {
						log.warn(String.format(HTTP_ERROR_WITH_S_S_S, url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
						log.warn(EntityUtils.toString(response.getEntity()));
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