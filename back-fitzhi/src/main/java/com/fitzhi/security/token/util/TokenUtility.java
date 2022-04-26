package com.fitzhi.security.token.util;

import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.CODE_OPENID_HTTP_ERROR;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_OPENID_HTTP_ERROR;
import static com.fitzhi.Error.CODE_INVALID_HTTP_VERB;
import static com.fitzhi.Error.MESSAGE_INVALID_HTTP_VERB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;

import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpMethod;

/**
 * This class provides some utilities methods useful to manage the oauth & openid tokens  
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class TokenUtility<T> {
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	/**
	 * @see #httpLoadToken(CloseableHttpClient, String, Class, String...)
	 */
	public T httpLoadToken(HttpMethod verb, String url, Class<T> sample,  Map<String, String> headers) throws ApplicationException {
		CloseableHttpClient client = HttpClients.createDefault();
		return this.httpLoadToken(verb, client, url, sample, headers);
	}
	
	/**
	 * Load the token from the server.
	 * @param verb the HTTP verb
	 * @param client the created HTTP client
	 * @param url the identity server URL 
	 * @param sample the T class definitition in order to deserialize correctly the JSON String into the corresponding T.
	 * @param headers the headers 
	 * @return The token class retrieved from the server
	 * @throws ApplicationException
	 */
	public T httpLoadToken(HttpMethod verb, CloseableHttpClient client, String url, Class<T> sample, Map<String, String> headers) throws ApplicationException {

		try {
			HttpUriRequest uriRequest = null;
			switch (verb) {
				case POST:
					uriRequest = new HttpPost(url);
					break;
				case GET:
					uriRequest = new HttpGet(url);
					break;
				default:
					throw new ApplicationException(
						CODE_INVALID_HTTP_VERB, 
						MessageFormat.format(MESSAGE_INVALID_HTTP_VERB, verb));
			}

			// uri has to be final
			final HttpUriRequest uri = uriRequest;
			headers.forEach((K, V) -> uri.setHeader(K, V));

			CloseableHttpResponse response = client.execute(uriRequest);
			if (response.getStatusLine().getStatusCode() == 200) {

				try ( BufferedReader br = new BufferedReader(new InputStreamReader( (response.getEntity().getContent())))) { 
					String content = br.lines().collect(Collectors.joining("\n"));
					return gson.fromJson(content, sample);
				}

			} else {
				throw new ApplicationException(
					CODE_OPENID_HTTP_ERROR,
					MessageFormat.format(MESSAGE_OPENID_HTTP_ERROR, 
						response.getStatusLine().getStatusCode(),
						response.getStatusLine().getReasonPhrase(),
						url));
			}
		} catch (IOException ioe) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, url));
		}
	}

}
