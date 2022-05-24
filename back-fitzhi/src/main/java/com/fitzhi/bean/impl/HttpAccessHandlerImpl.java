package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.CODE_HTTP_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_CLIENT_ERROR;
import static com.fitzhi.Error.MESSAGE_HTTP_ERROR;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Map;

import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Implementation of the data access handler.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
@Profile("slave")
public class HttpAccessHandlerImpl<T> implements HttpAccessHandler<T> {

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	HttpClient client = null;

	@Override
	public Map<Integer, T> loadMap(String url, TypeToken<Map<Integer, T>> typeToken) throws ApplicationException {
		try {
			HttpClient client = httpClient();
			HttpResponse response = client.execute(new HttpGet(url));
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				Type listEntityType = typeToken.getType();
				Map<Integer, T> theMap = gson.fromJson(EntityUtils.toString(response.getEntity()), listEntityType);
				return theMap;
			} else {
				throw new ApplicationException(CODE_HTTP_ERROR, MessageFormat.format(MESSAGE_HTTP_ERROR, response.getStatusLine().getReasonPhrase(), url));
			}
		} catch (final IOException ioe) {
			throw new ApplicationException(
				CODE_HTTP_CLIENT_ERROR, 
				MessageFormat.format(MESSAGE_HTTP_CLIENT_ERROR, url), 
				ioe);
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