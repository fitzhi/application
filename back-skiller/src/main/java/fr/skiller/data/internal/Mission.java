/**
 * 
 */
package fr.skiller.data.internal;

import java.util.Date;

/**
 * Mission of a developer inside a project
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Mission {

	/**
	 * The project identifier.
	 */
	public int idProject;
	
	/**
	 * Date of the latest commit.
	 */
	public Date lastCommit;
	
	/**
	 * @return number of commit submitted by a developer inside the project.
	 */
	public int numberOfCommits;
	
	/**
	 * @return number of files modifier by a developer inside the project.
	 */
	public int numberOfFiles;

	/**
	 * @param idProject the identifier of the project
	 * @param lastCommit the date/time of the last commit
	 * @param numberOfCommits the number of commits submitted
	 * @param numberOfFiles the number of files
	 */
	public Mission(final int idProject, final Date lastCommit, final int numberOfCommits, final int numberOfFiles) {
		this.idProject = idProject;
		this.lastCommit = lastCommit;
		this.numberOfCommits = numberOfCommits;
		this.numberOfFiles = numberOfFiles;
	}
	
	/**
	 * @param idProject the identifier of the project
	 */
	public Mission(int idProject) {
		this.idProject = idProject;
		this.lastCommit = null;
		this.numberOfCommits = 0;
		this.numberOfFiles = 0;
	}	
}
