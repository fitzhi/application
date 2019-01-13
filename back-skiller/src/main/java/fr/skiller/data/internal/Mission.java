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
	 * The project name.
	 */
	public String name;
	
	/**
	 * Date of the first commit.
	 */
	public Date firstCommit;

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
	 * Empty constructor for (de)serialization usage.
	 */
	public Mission() {
	}
	
	/**
	 * @param idProject the identifier of the project
	 * @param name the name of the project
	 * @param lastCommit the date/time of the last commit
	 * @param firstCommit the date/time of the last commit
	 * @param numberOfCommits the number of commits submitted
	 * @param numberOfFiles the number of files
	 */
	public Mission(final int idProject, final String name, final Date firstCommit, final Date lastCommit, final int numberOfCommits, final int numberOfFiles) {
		this.idProject = idProject;
		this.name = name;
		this.lastCommit = lastCommit;
		this.firstCommit = firstCommit;
		this.numberOfCommits = numberOfCommits;
		this.numberOfFiles = numberOfFiles;
	}
	
	/**
	 * @param idProject the identifier of the project
	 * @param name the name of the project
	 */
	public Mission(final int idProject, final String name) {
		this(idProject, name, null, null, 0, 0);
	}	
}
