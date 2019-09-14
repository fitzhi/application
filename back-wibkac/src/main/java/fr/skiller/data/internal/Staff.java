/**
 * 
 */
package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fr.skiller.data.source.Contributor;
import fr.skiller.security.CustomGrantedAuthority;
import fr.skiller.service.FileType;
import lombok.Data;

/**
 * A staff member in the company (most probably a developer).<br/>
 * This object represents also the login/pass associated to each collaborator
 * inside the company.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Staff implements UserDetails {

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
	private List<CustomGrantedAuthority> authorities = new ArrayList<>();
	
	/**
	 * Empty construction.
	 */
	public Staff() {
    	authorities.add(new CustomGrantedAuthority(ROLE_USER));
		missions = new ArrayList<>();
		experiences = new ArrayList<>();
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
    	authorities.add(new CustomGrantedAuthority(ROLE_USER));
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
    	authorities.add(new CustomGrantedAuthority(ROLE_USER));
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
    	authorities.add(new CustomGrantedAuthority(ROLE_USER));
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
		Optional<Mission> optMission = missions.stream()
				.filter(mission -> mission.getIdProject() == idProject)
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
