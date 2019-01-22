/**
 * 
 */
package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import fr.skiller.data.source.Contributor;

/**
 * A staff member in the company (most probably a developer).<br/>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Staff {
	public int idStaff;
	public String firstName;
	public String lastName;
	public String nickName;
	public String login;
	public String email;
	public String level;
	/**
	 * Staff member is still active or remove from the staff list.
	 */
	public boolean isActive = true;
	/**
	 * Date of the exit.
	 */
	public Date dateInactive;
	
	/**
	 * application filename & type (Word, PDF...)
	 */
	public String application;
	public int typeOfApplication;
	
	/**
	 * {@code true} if this staff member is an external developer, {@code false} otherwise 
	 */
	public boolean external = false;

	/**
	 * The list of missions where the collaborator has been involved.
	 */
	public List<Mission> missions;
	
	/**
	 * The collaborator's list of skills.
	 */
	public List<Experience> experiences;

	/**
	 * Empty construction.
	 */
	public Staff() {
	}

	/**
	 * Construction of an <b>ACTIVE</b> staff member.
	 * @param idStaff staff identifier
	 * @param firstName first name of this staff member
	 * @param lastName last name of this staff member
	 * @param nickName nickName of this staff member
	 * @param login login of this staff member
	 * @param email email of this staff member
	 * @param level the level of this staff member
	 */
	public Staff(int idStaff, final String firstName, final String lastName, final String nickName, final String login, final String email, final String level) {
		this.idStaff = idStaff;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.login = login;
		this.email = email;
		this.level = level;
		missions = new ArrayList<Mission>();
		experiences = new ArrayList<Experience>();
	}

	/**
	 * Construction of a staff member.
	 * @param idStaff staff identifier
	 * @param firstName first name of this staff member
	 * @param lastName last name of this staff member
	 * @param nickName nickName of this staff member
	 * @param login login of this staff member
	 * @param email email of this staff member
	 * @param level the level of this staff member
	 * @param isActive {@code true} if the staff member is active, {@code false} otherwise
	 * @param external {@code true} if the staff member is extern to the company, {@code false} otherwise
	 */
	public Staff(int idStaff, final String firstName, final String lastName, final String nickName, final String login, final String email, final String level, final boolean isActive, final boolean external) {
		super();
		this.idStaff = idStaff;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.login = login;
		this.email = email;
		this.level = level;
		this.isActive = isActive;
		this.external = external;
		missions = new ArrayList<Mission>();
		experiences = new ArrayList<Experience>();
	}
	
	/**
	 * Update the application uploaded and its type. 
	 * @param application filename uploaded
	 * @param typeOfApplication type of application
	 */
	public void updateApplication (final String application, final int typeOfApplication) {
		this.application = application;
		this.typeOfApplication = typeOfApplication;
	}
	
	/**
	 * @return the complete name of the staff member in a string format.
	 */
	public String fullName() {
		return ((firstName==null?"":firstName) + " " + lastName).trim();
	}
	
	/**
	 * Retrieve the experience of the staff member corresponding from its skill.
	 * @param idSkill skill identifier
	 * @return the asset or null, if none exists for this skill
	 */
	public Experience getExperience(final int idSkill) {
		Optional<Experience> oAsset = experiences.stream().filter(exp -> (exp.id == idSkill)).findFirst();
		return (oAsset.isPresent()) ? oAsset.get() : null;
	}
	
	/**
	 * Check if a developer has taken part in a project
	 * @param idProject the the given project
	 * @return {@code true} if the developer is involved in the given project, {@code false} or not
	 */
	public boolean isInvolvedInProject (final int idProject) {
		return (missions.stream().filter(project -> project.idProject == idProject).count()  > 0);
	}
	
	/**
	 * Add a new mission for this developer.
	 */
	public void addMission(final Mission mission) {
		missions.add(mission);
	}

	/**
	 * Update an existing mission based with last statistics loaded from the repository.
	 * @param idProject project involved in the mission
	 * @param contributor source contributor updated
	 */
	public void updateMission(final int idProject, final Contributor contributor) {
		Mission missionSelected = missions.stream().filter(mission -> mission.idProject == idProject).findFirst().get();
		missionSelected.firstCommit = contributor.firstCommit;
		missionSelected.lastCommit = contributor.lastCommit;
		missionSelected.numberOfCommits = contributor.numberOfCommitsSubmitted;
		missionSelected.numberOfFiles = contributor.numberOfFiles;
	}

	@Override
	public String toString() {
		return "Staff [idStaff=" + idStaff + ", firstName=" + firstName + ", lastName=" + lastName + ", nickName="
				+ nickName + ", login=" + login + ", email=" + email + ", level=" + level + ", isActive=" + isActive
				+ ", dateInactive=" + dateInactive + ", application=" + application + ", typeOfApplication="
				+ typeOfApplication + ", external=" + external + ", missions=" + missions + ", experiences="
				+ experiences + "]";
	}	

}
