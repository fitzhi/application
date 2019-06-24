/**
 * 
 */
package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Generated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fr.skiller.data.source.Contributor;
import fr.skiller.service.FileType;

/**
 * A staff member in the company (most probably a developer).<br/>
 * This object represents also the login/pass associated to each collaborator
 * inside the company.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Staff implements UserDetails {

	private static final String ROLE_USER = "ROLE_USER";

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3688638416803353536L;
	
	private int idStaff;
	private String firstName;
	private String lastName;
	private String nickName;
	private String login;
	private String email;
	private String level;
	private String password;
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
	 * {@code true} if this staff member is an external developer, {@code false}
	 * otherwise
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
	* List of authorities for this user
	**/
	private List<GrantedAuthority> authorities = new ArrayList<>();
	
	/**
	 * Empty construction.
	 */
	public Staff() {
    	authorities.add(new SimpleGrantedAuthority(ROLE_USER));
	}


	/**
	 * Creation of a empty staff member just on a his login/password
	 * 
	 * @param idStaff  the staff identified of the newly created staff member
	 * @param login    the new <u>UNIQUE</u> login
	 * @param password the associated encrypted password
	 */
	public Staff(final int idStaff, String login, String password) {
		this.idStaff = idStaff;
		this.login = login;
		this.password = password;
		missions = new ArrayList<>();
		experiences = new ArrayList<>();
    	authorities.add(new SimpleGrantedAuthority(ROLE_USER));
	}

	/**
	 * Construction of an <b>ACTIVE</b> staff member.
	 * 
	 * @param idStaff   staff identifier
	 * @param firstName first name of this staff member
	 * @param lastName  last name of this staff member
	 * @param nickName  nickName of this staff member
	 * @param login     login of this staff member
	 * @param email     email of this staff member
	 * @param level     the level of this staff member
	 */
	public Staff(final int idStaff, String firstName, String lastName, String nickName, String login, String email,
			String level) {
		this.idStaff = idStaff;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.login = login;
		this.email = email;
		this.level = level;
		missions = new ArrayList<>();
		experiences = new ArrayList<>();
    	authorities.add(new SimpleGrantedAuthority(ROLE_USER));
	}

	/**
	 * Construction of a staff member.
	 * 
	 * @param idStaff   staff identifier
	 * @param firstName first name of this staff member
	 * @param lastName  last name of this staff member
	 * @param nickName  nickName of this staff member
	 * @param login     login of this staff member
	 * @param email     email of this staff member
	 * @param level     the level of this staff member
	 * @param isActive  {@code true} if the staff member is active, {@code false}
	 *                  otherwise
	 * @param external  {@code true} if the staff member is external to the company,
	 *                  {@code false} otherwise
	 */
	public Staff(final int idStaff, String firstName, String lastName, String nickName, String login, String email,
			String level, final boolean isActive, final boolean external) {
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
    	authorities.add(new SimpleGrantedAuthority(ROLE_USER));
	}

	/**
	 * Update the application uploaded and its type.
	 * 
	 * @param application       filename uploaded
	 * @param typeOfApplication type of application
	 */
	public void updateApplication(String application, final FileType typeOfApplication) {
		this.application = application;
		this.typeOfApplication = typeOfApplication.getValue();
	}

	/**
	 * @return the complete name of the staff member in a string format.
	 */
	public String fullName() {
		return ((firstName == null ? "" : firstName) + " " + lastName).trim();
	}

	/**
	 * Retrieve the experience of the staff member corresponding from its skill.
	 * 
	 * @param idSkill skill identifier
	 * @return the asset or null, if none exists for this skill
	 */
	public Experience getExperience(final int idSkill) {
		Optional<Experience> oAsset = experiences.stream().filter(exp -> (exp.getId() == idSkill)).findFirst();
		return (oAsset.isPresent()) ? oAsset.get() : null;
	}

	/**
	 * Check if a developer has taken part in a project
	 * 
	 * @param idProject the the given project
	 * @return {@code true} if the developer is involved in the given project,
	 *         {@code false} or not
	 */
	public boolean isInvolvedInProject(final int idProject) {
		return (missions.stream().filter(project -> project.getIdProject() == idProject).count() > 0);
	}

	/**
	 * Add a new mission for this developer.
	 */
	public void addMission(final Mission mission) {
		missions.add(mission);
	}

	/**
	 * Update an existing mission based with last statistics loaded from the
	 * repository.
	 * 
	 * @param idProject   project involved in the mission
	 * @param contributor source contributor updated
	 */
	public void updateMission(final int idProject, final Contributor contributor) {
		Optional<Mission> optMission = missions.stream().filter(mission -> mission.getIdProject() == idProject)
				.findFirst();
		if (optMission.isPresent()) {
			Mission missionSelected = optMission.get();
			missionSelected.setFirstCommit(contributor.getFirstCommit());
			missionSelected.setLastCommit(contributor.getLastCommit());
			missionSelected.setNumberOfCommits(contributor.getNumberOfCommitsSubmitted());
			missionSelected.setNumberOfFiles(contributor.getNumberOfFiles());
		} else {
			Logger logger = LoggerFactory.getLogger(Staff.class.getCanonicalName());
			if (logger.isErrorEnabled()) {
				logger.error(String.format("Cannot find a mission for the project id %d", idProject));
			}
		}
	}

	/**
	 * @return <code>true</code> if the staff member contains only the user
	 *         connection properties, i.e. the login/password
	 */
	public boolean isEmpty() {
		return ((this.firstName == null) && (this.lastName == null) && (this.nickName == null) && (this.level == null)
				&& (this.email == null));
	}

	/**
	 * @return <code>true</code> if the passed password is correct.
	 */
	public boolean isValidPassword(String password) {
		return password.equals(this.password);
	}

	@Override
	@Generated("eclipse")
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

	/**
	 * set the new password for this staff member.
	 * 
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.isActive();
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.isActive();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.isActive();
	}

	@Override
	public boolean isEnabled() {
		return this.isActive();
	}
}
