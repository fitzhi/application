package com.fitzhi.bean;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fitzhi.exception.ApplicationException;

import org.apache.http.client.HttpClient;

/**
 * Interface in charge of loading data from network
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface HttpAccessHandler<T> {

	
	/**
	 * Load data on network through an HTTP GET to the backend server. The response body will be parsed and returned.
	 * 
	 * @param url the <b>url</b> of the backend server
	 * @param typeReference the reference to the generic
	 * <p>
	 * This parameter is linked to java Type erasure. Generics are only known at compile time.<br/>
	 * Technical reference :
	 * {@link https://stackoverflow.com/questions/14503881/strange-behavior-when-deserializing-nested-generic-classes-with-gson/14506181#14506181}
	 * </p>
	 * @return the resulting <b>map</b>
	 * @throws ApplicationException thrown if any problem occurs. It might be either a NETWORK error, or a BACKEND error. 
	 */
	Map<Integer, T> loadMap(String url, TypeReference<Map<Integer, T>> typeReference) throws ApplicationException;

	/**
	 * Load data on network through an HTTP GET to the backend server. The response body will be parsed and returned.
	 * 
	 * @param url the <b>url</b> of the backend server
	 * @param typeReference the reference to the generic
	 * <p>
	 * This parameter is linked to java Type erasure. Generics are only known at compile time.<br/>
	 * Technical reference :
	 * {@link https://stackoverflow.com/questions/14503881/strange-behavior-when-deserializing-nested-generic-classes-with-gson/14506181#14506181}
	 * </p>
	 * @return the resulting <b>list</b>
	 * @throws ApplicationException thrown if any problem occurs. It might be either a NETWORK error, or a BACKEND error. 
	 */
	List<T> loadList(String url, TypeReference<List<T>> typeReference) throws ApplicationException;

	/**
	 * This method exists only <u>for testing purpose</u>, in order to inject a mock a HttpClient.
	 * @param httpClient the HTTP client to be used.
	 */
	void setHttpClient(HttpClient httpClient);

}
