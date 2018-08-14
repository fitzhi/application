/**
 * 
 */
package fr.skiller.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.skiller.data.Skill;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class Collaborator {
	public int id;
	public String firstName;
	public String lastName;
	public String nickName;
	public String email;
	public String level;
	public List<Project> projects;
	public Skill[] experience;

	public Collaborator(int id, String firstName, String lastName, String nickName, String email, String level) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.email = email;
		this.level = level;
		projects = new ArrayList<Project>();
		experience = new ArrayList<Skill>().toArray(new Skill[0]);
	}

	public Collaborator() {
	}

	@Override
	public String toString() {
		return "Collaborator [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", nickName="
				+ nickName + ", email=" + email + ", level=" + level + ", projects=" + Arrays.toString(projects.toArray(new Project[0]))
				+ ", experience=" + Arrays.toString(experience) + "]";
	}

}
