/**
 * 
 */
package fr.skiller.data;

import java.util.ArrayList;
import java.util.Arrays;

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
    public Project[] projects;
    
	public Collaborator(int id, String firstName, String lastName, String nickName, String email, String level) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.email = email;
		this.level = level;
		projects = new ArrayList<Project>().toArray(new Project[1]);
	}
	public Collaborator() { }
	
	@Override
	public String toString() {
		return "Collaborator [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", nickName="
				+ nickName + ", email=" + email + ", level=" + level + ", projects=" + Arrays.toString(projects) + "]";
	}
	
	
	
}
