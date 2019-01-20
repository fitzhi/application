package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Project class. 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Project {

	/**
	 * The project identifier
	 */
	public int id;
	
	/**
	 * The name of the project
	 */
	public String name;
	
	/**
	 * The connection settings model.
	 * 2 models exist : either the direct (URL/user/pass), or the indirect (URL/remote file)
	 */
	public int connection_settings;
	
	/**
	 * The repository URL of the project
	 */
	public String urlRepository;
	
	/**
	 * The user name required to access the version control repository.
	 */
	public String username;

	/**
	 * The user name required to access the version control repository.
	 */
	public String password;
	
	/**
	 * The filename containing the connection parameters to access the version control system.
	 */
	public String filename;
	
	/**
	 * List of skills required for this project.
	 */
	public List<Skill> skills = new ArrayList<Skill>();
	
	/**
	 * Empty constructor.
	 */
	public Project() { }
	
	/**
	 * @param id Project identifier
	 * @param name Name of the project
	 * @param urlRepository URL of the source repository
	 */
	public Project(int id, String name, final String urlRepository) {
		this.id = id;
		this.name = name;
		this.urlRepository = urlRepository;
	}

	/**
	 * @param id Project identifier
	 * @param name Name of the project
	 */
	public Project(int id, String name) {
		this(id, name, null);
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", connection_settings=" + connection_settings
				+ ", urlRepository=" + urlRepository + ", username=" + username + ", password=" + password
				+ ", filename=" + filename + ", skills=" + skills + "]";
	}


}
