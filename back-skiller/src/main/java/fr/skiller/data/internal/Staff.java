/**
 * 
 */
package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
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
	
	public List<Project> projects;
	public List<Experience> experiences;

	public Staff(int idStaff, final String firstName, final String lastName, final String nickName, final String login, final String email, final String level) {
		super();
		this.idStaff = idStaff;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.login = login;
		this.email = email;
		this.level = level;
		projects = new ArrayList<Project>();
		experiences = new ArrayList<Experience>();
	}

	public Staff(int idStaff, final String firstName, final String lastName, final String nickName, final String login, final String email, final String level, final boolean isActive) {
		super();
		this.idStaff = idStaff;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.login = login;
		this.email = email;
		this.level = level;
		this.isActive = isActive;
		projects = new ArrayList<Project>();
		experiences = new ArrayList<Experience>();
	}
	
	/**
	 * @return the complete name of the staff member in a string format.
	 */
	public String fullName() {
		return (firstName==null?"":firstName) + " " + lastName;
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
	 * Empty construction.
	 */
	public Staff() {
	}

	@Override
	public String toString() {
		return "Staff [id=" + idStaff + ", firstName=" + firstName + ", lastName=" + lastName + ", nickName=" + nickName
				+ ", login=" + login + ", email=" + email + ", level=" + level + ", isActive=" + isActive + ", projects="
				+ projects + ", experiences=" + experiences + "]";
	}

}
