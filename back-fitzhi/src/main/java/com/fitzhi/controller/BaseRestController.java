package com.fitzhi.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Base class with common behavior for some controllers.
 */
public class BaseRestController {

	/**
	 * @return a generated HTTP Headers for the response
	 */
	protected HttpHeaders headers() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return headers;
	}
    
}
