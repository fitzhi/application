package com.fitzhi.bean;

import java.io.IOError;
import java.io.IOException;
import java.util.List;

import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.exception.SkillerException;

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
	

	/**
	 * <p>
	 * Retrieve and actualize the staff team involved in the changes retrieved in the repository.
	 * </p>
	 * @param project given project of the associated changes
	 * @param changes changes retrieved from the repository
	 */
	void actualizeStaff(Project project, SourceControlChanges changes);

	/**
	 * <p>
	 * Generate the history of the Project building in the projects skyline. 
	 * </p>
	 * <P>
	 * This method loads the {@link ProjectLayer project layers} from the filesystem and delegates the generation 
	 * to the generation {@link #generateProjectBuilding(List) generateProjectBuilding}
	 * </p>
	 * @param project given project of the associated changes
	 * @return the Project-building
	 * @throws SkillerException thrown if any problem occurs, most probably an {@link IOException} when loading the data.
	 */
	ProjectBuilding generateProjectBuilding(Project project) throws SkillerException;

	/**
	 * <p>
	 * Generate the history of the Project building in the projects skyline.
	 * </p>
	 * @return the Project-building
	 * @param layers list of the project layers
	 */
	ProjectBuilding generateProjectBuilding(List<ProjectLayer> layers);

}
