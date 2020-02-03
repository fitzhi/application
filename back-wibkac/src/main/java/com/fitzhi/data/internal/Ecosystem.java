package com.fitzhi.data.internal;

import lombok.Data;

/**
 * <p>
 * Ecosystem detected on the repository.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Ecosystem {

	/**
	 * Identifier of the ecosystem
	 */
	int id;
	
	/**
	 * Title of the ecosystem represented by a Font Awesome icon.
	 */
	String awesomeTitle;
	
	/**
	 * Title of the ecosystem
	 */
	String title;
	
	/**
	 * Pattern to use for selecting the source files involved in the ecosystem.
	 */
	String pattern; 
}
