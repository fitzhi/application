package com.fitzhi.security.token;

import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.CODE_OPENID_HTTP_ERROR;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_OPENID_HTTP_ERROR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.stream.Collectors;

import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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

	public T httpLoadToken(String url, Class<T> sample, String... params) throws ApplicationException {

		try {
			CloseableHttpClient client = HttpClients.createDefault();

			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			CloseableHttpResponse response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {

				BufferedReader br = new BufferedReader(
					new InputStreamReader( 
						(response.getEntity().getContent())
					)
				);
				
				String content = br.lines().collect(Collectors.joining("\n"));

				return gson.fromJson(content, sample);

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
