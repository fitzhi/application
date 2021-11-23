package com.fitzhi.bean;

import java.io.IOException;
import java.util.List;

import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.Skyline;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.exception.ApplicationException;

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
	 * Generate the Build layers of the given project.
	 * Each layer represents the size of the project, day by day.
	 * </p>
	 * @param project project whose changes have to be serialized in CSV
	 * @param changes changes retrieved from the repository
	 * @return the project layers
	 * @throws ApplicationException thrown if any exception occurs, most probably an IOException
	 */
	ProjectLayers generateProjectLayers(Project project, SourceControlChanges changes) throws ApplicationException;
	
	/**
	 * <p>Complete the project layers until the actual date.</p>
	 * @param projectLayers the container of project layers
	 */
	public void completeProjectLayers(ProjectLayers projectLayers);

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
	 * <p>
	 * This method loads the {@link ProjectLayer project layers} from the filesystem and delegates the generation 
	 * to the generation {@link #generateProjectBuilding(Project) generateProjectBuilding}
	 * </p>
	 * @param project the given project of the associated changes
	 * @return the Project-building
	 * @throws ApplicationException thrown if any problem occurs, most probably an {@link IOException} when loading the data.
	 */
	ProjectBuilding generateProjectBuilding(Project project) throws ApplicationException;

	/**
	 * <p>Generate the history of the Project building in the projects skyline.</p>
	 * @param project the project whose building has to be generated.
	 * @param layers the container of the project layers
	 * @return the Project-building
	 */
	ProjectBuilding generateProjectBuilding(Project project, ProjectLayers layers);

	/**
	 * <p>
	 * Generate and return the skyline corresponding to the whole array of declared projects inside Fitzhi.
	 * </p>
	 * <p>
	 * <em>Only the <strong>active</strong> projects are included.</em>
	 * </p>
	 * @return the restulting skyline
	 * @throws ApplicationException thrown if any exception occurs.
	 */
	Skyline generateSkyline() throws ApplicationException;
	
	/**
	 * <p>
	 * Generate and return the skyline corresponding to the given array of projects.
	 * </p>
	 * @param projects the list of projects to include in the skyline
	 * @return the resulting skyline
	 * @throws ApplicationException thrown if any exception occurs.
	 */
	Skyline generateSkyline(List<Project> projects) throws ApplicationException;
}
