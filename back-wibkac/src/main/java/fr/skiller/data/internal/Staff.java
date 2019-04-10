/**
 * 
 */
package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Generated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.skiller.data.source.Contributor;

/**
 * A staff member in the company (most probably a developer).<br/>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Staff {
	
 	/**
 	 * The logger for the Staff class.
 	 */
	private Logger logger = LoggerFactory.getLogger(Staff.class.getCanonicalName());	
	
	private int idStaff;
	private String firstName;
	private String lastName;
	private String nickName;
	private String login;
	private String email;
	private String level;
	/**
	 * Staff member is still active or remove from the staff list.
	 */
	private boolean active = true;
	/**
	 * Date of the exit.
	 */
	private Date dateInactive;
	
	/**
	 * application filename & type (Word, PDF...)
	 */
	private String application;
	private int typeOfApplication;
	
	/**
	 * {@code true} if this staff member is an external developer, {@code false} otherwise 
	 */
	private boolean external = false;

	/**
	 * The list of missions where the collaborator has been involved.
	 */
	private List<Mission> missions;
	
	/**
	 * The collaborator's list of skills & levels.
	 */
	private List<Experience> experiences;

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
		missions = new ArrayList<>();
		experiences = new ArrayList<>();
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
		this.active = isActive;
		this.external = external;
		missions = new ArrayList<>();
		experiences = new ArrayList<>();
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
		Optional<Experience> oAsset = experiences.stream().filter(exp -> (exp.getId() == idSkill)).findFirst();
		return (oAsset.isPresent()) ? oAsset.get() : null;
	}
	
	/**
	 * Check if a developer has taken part in a project
	 * @param idProject the the given project
	 * @return {@code true} if the developer is involved in the given project, {@code false} or not
	 */
	public boolean isInvolvedInProject (final int idProject) {
		return (missions.stream().filter(project -> project.getIdProject() == idProject).count()  > 0);
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
		Optional<Mission> optMission = missions.stream().filter(mission -> mission.getIdProject() == idProject).findFirst();
		if (optMission.isPresent()) {
			Mission missionSelected = optMission.get();
			missionSelected.setFirstCommit(contributor.getFirstCommit());
			missionSelected.setLastCommit(contributor.getLastCommit());
			missionSelected.setNumberOfCommits(contributor.getNumberOfCommitsSubmitted());
			missionSelected.setNumberOfFiles(contributor.getNumberOfFiles());
		} else {
			if (logger.isErrorEnabled()) {
				logger.error(String.format("Cannot find a mission for the project id %d", idProject));
			}
		}
	}

	@Override
	@Generated ("eclipse")
	public String toString() {
		return "Staff [idStaff=" + idStaff + ", firstName=" + firstName + ", lastName=" + lastName + ", nickName="
				+ nickName + ", login=" + login + ", email=" + email + ", level=" + level + ", isActive=" + active
				+ ", dateInactive=" + dateInactive + ", application=" + application + ", typeOfApplication="
				+ typeOfApplication + ", external=" + external + ", missions=" + missions + ", experiences="
				+ experiences + "]";
	}

	/**
	 * @return the idStaff
	 */
	public int getIdStaff() {
		return idStaff;
	}

	/**
	 * @param idStaff the idStaff to set
	 */
	public void setIdStaff(int idStaff) {
		this.idStaff = idStaff;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * @param nickName the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.active = isActive;
	}

	/**
	 * @return the dateInactive
	 */
	public Date getDateInactive() {
		return dateInactive;
	}

	/**
	 * @param dateInactive the dateInactive to set
	 */
	public void setDateInactive(Date dateInactive) {
		this.dateInactive = dateInactive;
	}

	/**
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * @param application the application to set
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * @return the typeOfApplication
	 */
	public int getTypeOfApplication() {
		return typeOfApplication;
	}

	/**
	 * @param typeOfApplication the typeOfApplication to set
	 */
	public void setTypeOfApplication(int typeOfApplication) {
		this.typeOfApplication = typeOfApplication;
	}

	/**
	 * @return the external
	 */
	public boolean isExternal() {
		return external;
	}

	/**
	 * @param external the external to set
	 */
	public void setExternal(boolean external) {
		this.external = external;
	}

	/**
	 * @return the missions
	 */
	public List<Mission> getMissions() {
		return missions;
	}

	/**
	 * @param missions the missions to set
	 */
	public void setMissions(List<Mission> missions) {
		this.missions = missions;
	}

	/**
	 * @return the experiences
	 */
	public List<Experience> getExperiences() {
		return experiences;
	}

	/**
	 * @param experiences the experiences to set
	 */
	public void setExperiences(List<Experience> experiences) {
		this.experiences = experiences;
	}
	
}
