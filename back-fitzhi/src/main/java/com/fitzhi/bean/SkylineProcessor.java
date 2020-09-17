package com.fitzhi.bean;

import java.util.List;

import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.SourceControlChanges;

/**
 * <p>
 * This interface is in charge of the generation of the Fitzhì skyline. 
 * The main implementation (and currently the single one) is 
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface SkylineProcessor {
    
	/**
	 * <p>
	 * generate the build layers of the given project.
	 * </br>
	 * Each layer represents the size of the project, day by day.
	 * </p>
	 * @param project project whose changes have to be serialized in CSV
	 * @param changes changes retrieved from the repository
	 */
	List<ProjectLayer> generateProjectLayers(Project project, SourceControlChanges changes);
    
}
