package com.tixhi.controller.in;

import com.tixhi.controller.ProjectSonarController;
import com.tixhi.data.internal.SonarProject;

import lombok.Data;

/**
 * <p>
 * Body of data containing parameters to a HTTP Post call.<br/>
 * This class is used AT LEAST by {@link ProjectSonarController#saveUrlSonarServer(BodyParamSonarEntry)} 
 * linked to the URL  {@code /api/project/sonar/saveUrl
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class BodyParamProjectSonarServer {

	/**
	 * Project identifier
	 */
	private int idProject;
	
	/**
	 * The URL Sonar server selected by the end-user for the given project.
	 */
	private String urlSonarServer;
	
	/**
	 * Empty constructor.
	 */
	public BodyParamProjectSonarServer() {
		// Empty constructor declared for serialization / deserialization purpose 		
	}
	
}
