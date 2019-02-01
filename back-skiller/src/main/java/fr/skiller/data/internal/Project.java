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
	 * List of committer {@link fr.skiller.data.internal.Ghost ghosts} identified.<br/>
	 * These ghosts are known by their pseudos <br/>
	 * Either they are missing from the staff collection, or they are technical.
	 */
	public List<Ghost> ghosts = new ArrayList<Ghost>();
	
	/**
	 * Constant representing one the 2 models of connection settings.
	 * This one if for the direct access : url repository / user / password
	 */
	private static int DIRECT_ACCESS = 1;
	
	/**
	 * Constant representing one the 2 models of connection settings.
	 * This one if for the inderect access : url repository / remote filename with connection parameters.
	 */
	private static int REMOTE_FILE_ACCESS = 2;
	
	
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

	/**
	 * @return {@code true} if the project is setup with a direct access to the version control repository 
	 * <i>(the Project self-contains connection parameters)</i>
	 */
	public boolean isDirectAccess() {
		return (connection_settings == DIRECT_ACCESS);
	}
	
	/**
	 * @return {@code true} if the project is setup with an indirect access to the version control repository 
	 * <i>(Application must retrieve the connection parameters on a property file)</i>
	 */
	public boolean isIndirectAccess() {
		return (connection_settings == REMOTE_FILE_ACCESS);
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", connection_settings=" + connection_settings
				+ ", urlRepository=" + urlRepository + ", username=" + username + ", filename=" + filename + ", skills="
				+ skills + ", ghosts=" + ghosts + "]";
	}
	
}
