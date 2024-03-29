package com.fitzhi.data.external;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class ContributorView {
	
	/**
	 * the staff identifier.
	 */
	private int idStaff;
	
	/**
	 * the full name of this developer <i>(first name + last name)</i>.
	 */
	private String fullname;
	
	/**
	 * {@code true} if this staff member is in activity in the company, {@code false} otherwise
	 */
	private boolean active;
	
	/**
	 * {@code true} if this staff member is external from the company, {@code false} otherwise
	 */
	private boolean external;
	
	/**
	 * Date of the latest commit.
	 */
	private LocalDate firstCommit;
	
	/**
	 * Date of the latest commit.
	 */
	private LocalDate lastCommit;
	
	/**
	 * @return number of commit submitted by the developer inside the project.
	 */
	private int numberOfCommits;
	
	/**
	 * @return number of files modifier by the developer inside the project.
	 */
	private int numberOfFiles;
	
	/**
	 * @param idStaff staff identifier
	 * @param fullname complete name of the developer <i>(first name + last name)</i>
	 * @param firstCommit  Date of the <b>FIRST</b> commit for this developer.
	 * @param active {@code true} if this staff member is in activity in the staff, {@code false} otherwise
	 * @param external {@code true} if this staff member belongs to the company, {@code false} otherwise
	 * @param lastCommit  Date of the <b>LATEST</b> commit.
	 * @param numberOfCommits number of commit submitted by the developer inside the project.
	 * @param numberOfFiles number of files modifier by the developer inside the project.
	 */
	public ContributorView(final int idStaff, final String fullname, final boolean active, final boolean external, final LocalDate firstCommit, final LocalDate lastCommit, 
			final int numberOfCommits, final int numberOfFiles) {
		this.idStaff = idStaff;
		this.fullname = fullname;
		this.active = active;
		this.external = external;
		this.firstCommit = firstCommit;
		this.lastCommit = lastCommit;
		this.numberOfCommits = numberOfCommits;
		this.numberOfFiles = numberOfFiles;
	}

	/**
	 * @return the idStaff
	 */
	public int getIdStaff() {
		return idStaff;
	}

	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @return the external
	 */
	public boolean isExternal() {
		return external;
	}

	/**
	 * @return the firstCommit
	 */
	public LocalDate getFirstCommit() {
		return firstCommit;
	}

	/**
	 * @return the lastCommit
	 */
	public LocalDate getLastCommit() {
		return lastCommit;
	}

	/**
	 * @return the numberOfCommits
	 */
	public int getNumberOfCommits() {
		return numberOfCommits;
	}

	/**
	 * @return the numberOfFiles
	 */
	public int getNumberOfFiles() {
		return numberOfFiles;
	}

}

/**
 * Class in charge of transferring the developers involved in a project. 
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ProjectContributors {

	/**
	 * Project identifier.
	 */
	private int idProject;
	
	/**
	 * List of contributors for the project.
	 */
	private List<ContributorView> contributors;
	
	/**
	 * @param idProject the project identifier.
	 * @param contributors list of contributors.
	 */
	public ProjectContributors(final int idProject, final List<ContributorView> contributors) {
		this.idProject = idProject;
		this.contributors = contributors;
	}

	/**
	 * @param idProject the project identifier.
	 */
	public ProjectContributors(final int idProject) {
		this.idProject = idProject;
		contributors = new ArrayList<>();
	}
	
	/**
	 * @param idStaff staff identifier
	 * @param fullname complete name of the developer <i>(first name + last name)</i>
	 * @param active {@code true} if this staff member is in activity in the staff, {@code false} otherwise
	 * @param external {@code true} if this staff member belongs to the company, {@code false} otherwise
	 * @param firstCommit  Date of the <b>FIRST</b> commit for this developer.
	 * @param lastCommit  Date of the <b>LATEST</b> commit.
	 * @param numberOfCommits number of commit submitted by the developer inside the project.
	 * @param numberOfFiles number of files modifier by the developer inside the project.
	 */
	public void addContributor(final int idStaff, final String fullname, final boolean active, final boolean external, 
			final LocalDate firstCommit, final LocalDate lastCommit, final int numberOfCommits, final int numberOfFiles) {
		contributors.add(new ContributorView(idStaff, fullname, active, external, firstCommit, lastCommit, numberOfCommits, numberOfFiles));
	}

	/**
	 * @return the idProject
	 */
	public int getIdProject() {
		return idProject;
	}

	/**
	 * @return the contributors
	 */
	public List<ContributorView> getContributors() {
		return contributors;
	}
	
}
