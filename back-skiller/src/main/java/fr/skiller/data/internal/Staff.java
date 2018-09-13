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
	public int id;
	public String firstName;
	public String lastName;
	public String nickName;
	public String email;
	public String level;
	/**
	 * Staff member is still active or remove from the staff list.
	 */
	public int active = 0;
	
	public List<Project> projects;
	public List<Experience> experiences;

	public Staff(int id, String firstName, String lastName, String nickName, String email, String level) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.email = email;
		this.level = level;
		projects = new ArrayList<Project>();
		experiences = new ArrayList<Experience>();
	}

	public Staff(int id, String firstName, String lastName, String nickName, String email, String level, int active) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.email = email;
		this.level = level;
		this.active = active;
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
		return "Collaborator [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", nickName="
				+ nickName + ", email=" + email + ", level=" + level + ", projects=" + projects + ", experience="
				+ experiences + "]";
	}

}
