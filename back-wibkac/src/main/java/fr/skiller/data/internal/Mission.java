/**
 * 
 */
package fr.skiller.data.internal;

import java.io.Serializable;
import java.util.Date;

/**
 * Mission of a developer inside a project
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Mission implements Serializable {

	/**
	 * For serialization purpose.
	 */
	private static final long serialVersionUID = 7291243543440703140L;

	/**
	 * The project identifier.
	 */
	private int idProject;

	/**
	 * The project name.
	 */
	private String name;
	
	/**
	 * Date of the first commit.
	 */
	private Date firstCommit;

	/**
	 * Date of the latest commit.
	 */
	private Date lastCommit;
	
	/**
	 * @return number of commit submitted by a developer inside the project.
	 */
	private int numberOfCommits;
	
	/**
	 * @return number of files modifier by a developer inside the project.
	 */
	private int numberOfFiles;

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

	@Override
	public String toString() {
		return "Mission [idProject=" + idProject + ", name=" + name + ", firstCommit=" + firstCommit + ", lastCommit="
				+ lastCommit + ", numberOfCommits=" + numberOfCommits + ", numberOfFiles=" + numberOfFiles + "]";
	}

	/**
	 * @return the idProject
	 */
	public int getIdProject() {
		return idProject;
	}

	/**
	 * @param idProject the idProject to set
	 */
	public void setIdProject(int idProject) {
		this.idProject = idProject;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the firstCommit
	 */
	public Date getFirstCommit() {
		return firstCommit;
	}

	/**
	 * @param firstCommit the firstCommit to set
	 */
	public void setFirstCommit(Date firstCommit) {
		this.firstCommit = firstCommit;
	}

	/**
	 * @return the lastCommit
	 */
	public Date getLastCommit() {
		return lastCommit;
	}

	/**
	 * @param lastCommit the lastCommit to set
	 */
	public void setLastCommit(Date lastCommit) {
		this.lastCommit = lastCommit;
	}

	/**
	 * @return the numberOfCommits
	 */
	public int getNumberOfCommits() {
		return numberOfCommits;
	}

	/**
	 * @param numberOfCommits the numberOfCommits to set
	 */
	public void setNumberOfCommits(int numberOfCommits) {
		this.numberOfCommits = numberOfCommits;
	}

	/**
	 * @return the numberOfFiles
	 */
	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	/**
	 * @param numberOfFiles the numberOfFiles to set
	 */
	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}	

}
