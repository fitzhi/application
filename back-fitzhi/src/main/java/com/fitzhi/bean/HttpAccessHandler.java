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
	 * <blockquote>
	 * This parameter is linked to java Type erasure. Generics are only known at compile time.
	 * Technical reference :
	 * {@link https://stackoverflow.com/questions/14503881/strange-behavior-when-deserializing-nested-generic-classes-with-gson/14506181#14506181}
	 * </blockquote>
	 * @return the resulting <b>map</b>
	 * @throws ApplicationException thrown if any problem occurs. It might be either a NETWORK error, or a BACKEND error. 
	 */
	Map<Integer, T> loadMap(String url, TypeReference<Map<Integer, T>> typeReference) throws ApplicationException;

	/**
	 * Load data on network through an HTTP GET to the backend server. The response body will be parsed and returned.
	 * 
	 * @param url the <b>url</b> of the backend server
	 * @param typeReference the reference to the generic
	 * <blockquote>
	 * This parameter is linked to java Type erasure. Generics are only known at compile time.
	 * Technical reference :
	 * {@link https://stackoverflow.com/questions/14503881/strange-behavior-when-deserializing-nested-generic-classes-with-gson/14506181#14506181}
	 * </blockquote>
	 * @return the resulting <b>list</b>
	 * @throws ApplicationException thrown if any problem occurs. It might be either a NETWORK error, or a BACKEND error. 
	 */
	List<T> loadList(String url, TypeReference<List<T>> typeReference) throws ApplicationException;


	/**
	 * Send data on network through an <strong>HTTP PUT</strong> to the backend server. The response body will be parsed and returned.
	 * 
	 * @param url the <b>url</b> of the backend server
	 * @param o the object to be sent in the body of the request
	 * @param typeReference the reference to the generic
	 * <blockquote>
	 * This parameter is linked to java Type erasure. Generics are only known at compile time.
	 * <p>
	 * Technical reference : {@link https://stackoverflow.com/questions/14503881/strange-behavior-when-deserializing-nested-generic-classes-with-gson/14506181#14506181}
	 * </p>
	 * </blockquote>
     *
	 * @return the content of the response from server
	 * 
	 * @throws ApplicationException thrown if any problem occurs. It might be either a NETWORK error, or a BACKEND error. 
	 */
	T put(String url, Object o, TypeReference<T> typeReference) throws ApplicationException;

	/**
	 * Send a <strong>LIST</strong> of data on network through an <strong>HTTP PUT</strong> to the backend server. 
	 * We assume that the body of the server response is a void.
	 * 
	 * @param url the <b>url</b> of the backend server
	 * @param list the collection to be sent
	 * @param typeReference the reference to the generic hosted by the list
	 * <blockquote>
	 * This parameter is linked to java Type erasure. Generics are only known at compile time.
	 * <p>
	 * Technical reference : {@link https://stackoverflow.com/questions/14503881/strange-behavior-when-deserializing-nested-generic-classes-with-gson/14506181#14506181}
	 * </p>
	 * </blockquote>
     *
	 * @throws ApplicationException thrown if any problem occurs. It might be either a NETWORK error, or a BACKEND error. 
	 */
	void putList(String url, List<T> list, TypeReference<List<T>> typeReference) throws ApplicationException;

	/**
	 * This method exists only <u>for testing purpose</u>, in order to inject a mock of HttpClient.
	 * @param httpClient the HTTP client to be used.
	 */
	void setHttpClient(HttpClient httpClient);

}
