/**
 * 
 */
package fr.skiller.data.external;

import java.util.Date;

import fr.skiller.data.internal.Mission;

/**
 * Data ready to use representing the mission of a developer
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class StaffMission {
	
	/**
	 * The project identifier.
	 */
	public int idProject;
	
	/**
	 * The project name.
	 */
	public String name;
	
	/**
	 * Date of the latest commit.
	 */
	public Date lastCommit;
	
	/**
	 * @return number of commit submitted by a developer inside the project.
	 */
	public int numberOfCommits;
	
	/**
	 * @return number of files modified by a developer inside the project.
	 */
	public int numberOfFiles;

	/**
	 * @param idProject identifier of project
	 * @param lastCommit date of last commit
	 * @param numberOfCommits number of commit submitted by a developer inside the project.
	 * @param numberOfFiles number of files modified by a developer inside the project.
	 */
	public StaffMission(int idProject, Date lastCommit, int numberOfCommits, int numberOfFiles) {
		super();
		this.idProject = idProject;
		this.lastCommit = lastCommit;
		this.numberOfCommits = numberOfCommits;
		this.numberOfFiles = numberOfFiles;
	}

	/**
	 * @param mission the mission of a staff member
	 * @param projectName projectName name of the project
	 */
	public StaffMission(final Mission mission, final String projectName) {
		this.idProject = mission.idProject;
		this.name = projectName;
		this.lastCommit = mission.lastCommit;
		this.numberOfCommits = mission.numberOfCommits;
	}

	/**
	 * @param idProject identifier of project
	 * @param name name of the project
	 */
	public StaffMission(int idProject, String name) {
		this.idProject = idProject;
		this.name = name;
	}

	
}
