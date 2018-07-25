/**
 * 
 */
package fr.skiller.data;

import java.util.ArrayList;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class Collabotator {
    int id;
	String firtname; 
	String lastName;
	String nickName;
	String email;
	String level;
    Project[] projects;
    
	public Collabotator(int id, String firtname, String lastName, String nickName, String email, String level) {
		super();
		this.id = id;
		this.firtname = firtname;
		this.lastName = lastName;
		this.nickName = nickName;
		this.email = email;
		this.level = level;
		projects = new ArrayList<Project>().toArray(new Project[1]);
	}
    
}
