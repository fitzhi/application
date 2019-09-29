package fr.skiller.controller.in;

import java.util.ArrayList;
import java.util.List;

import fr.skiller.data.internal.FilesStats;
import fr.skiller.data.internal.SonarProject;
import lombok.Data;

/**
 * <p>
 * Body of data containing parameters to a HTTP Post call.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class BodyParamSonarFilesStats {

	/**
	 * Project identifier
	 */
	private int idProject;
	
	/**
	 * A Sonar project key linked to our project
	 */
	private String sonarProjectKey;
	
	/**
	 * Statistics of files per language.
	 */
	private List<FilesStats> filesStats = new ArrayList<>();
	
	/**
	 * Empty constructor.
	 */
	public BodyParamSonarFilesStats() {
		// Empty constructor declared for serialization / de-serialization purpose 		
	}

	/**
	 * @param idProject the project identifier
	 * @param sonarEntry the entry
	 */
	public BodyParamSonarFilesStats(int idProject, String sonarProjectKey) {
		super();
		this.idProject = idProject;
		this.sonarProjectKey = sonarProjectKey;
	}
	
}
