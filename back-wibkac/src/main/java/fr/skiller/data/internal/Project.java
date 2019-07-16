package fr.skiller.data.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

/**
 * Project class. 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Project  {

	/**
	 * The project identifier
	 */
	private int id;
	
	/**
	 * The name of the project
	 */
	private String name;
	
	/**
	 * The connection settings model.
	 * 2 models exist : either the direct (URL/user/pass), or the indirect (URL/remote file)
	 */
	private int connectionSettings;
	
	/**
	 * The repository URL of the project
	 */
	private String urlRepository;
	
	/**
	 * The directory where the repository has been cloned.
	 */
	private String locationRepository;
	
	/**
	 * The user name required to access the version control repository.
	 */
	private String username;

	/**
	 * The user name required to access the version control repository.
	 */
	private String password;
	
	/**
	 * The filename containing the connection parameters to access the version control system.
	 */
	private String filename;
	
	/**
	 * List of skills required for this project.
	 */
	private List<Skill> skills = new ArrayList<>();
	
	/**
	 * List of committer {@link fr.skiller.data.internal.Ghost ghosts} identified.<br/>
	 * These ghosts are known by their pseudos <br/>
	 * Either they are missing from the staff collection, or they are technical.
	 */
	private List<Ghost> ghosts = new ArrayList<>();
	
	/**
	 * List of path containing external dependencies.
	 * They will be excluded from the crawl.
	 */
	private List<String> dependencies = new ArrayList<>();
	
	/**
	 * Constant representing one the 2 models of connection settings.
	 * This one if for the direct access : url repository / user / password
	 */
	private static final int DIRECT_ACCESS = 1;
	
	/**
	 * Constant representing one the 2 models of connection settings.
	 * This one if for the inderect access : url repository / remote filename with connection parameters.
	 */
	private static final int REMOTE_FILE_ACCESS = 2;
	
	
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

	@Generated ("eclipse")
	private Project(int id, String name, int connectionSettings, String urlRepository, String locationRepository,
			String username, String password, String filename, List<Skill> skills, List<Ghost> ghosts) {
		super();
		this.id = id;
		this.name = name;
		this.connectionSettings = connectionSettings;
		this.urlRepository = urlRepository;
		this.locationRepository = locationRepository;
		this.username = username;
		this.password = password;
		this.filename = filename;
		this.skills = skills;
		this.ghosts = ghosts;
	}
	
	/**
	 * Copy constructor of <code>Project</code>
	 * @param project the project instance to be copied.
	 */
	public Project(Project project) {
		this (project.id, project.name, project.connectionSettings, project.urlRepository, project.locationRepository,
				project.username, project.password, project.filename, project.skills, project.ghosts);
	}
	
	/**
	 * @return {@code true} if the project is setup with a direct access to the version control repository 
	 * <i>(the Project self-contains connection parameters)</i>
	 */
	public boolean isDirectAccess() {
		return (connectionSettings == DIRECT_ACCESS);
	}
	
	/**
	 * @return {@code true} if the project is setup with an indirect access to the version control repository 
	 * <i>(Application must retrieve the connection parameters on a property file)</i>
	 */
	public boolean isIndirectAccess() {
		return (connectionSettings == REMOTE_FILE_ACCESS);
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", connection_settings=" + connectionSettings
				+ ", urlRepository=" + urlRepository + ", username=" + username + ", filename=" + filename + ", skills="
				+ skills + ", ghosts=" + ghosts + "]";
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the connection_settings
	 */
	public int getConnectionSettings() {
		return connectionSettings;
	}

	/**
	 * @param connectionSettings the connection_settings to set
	 */
	public void setConnectionSettings(int connectionSettings) {
		this.connectionSettings = connectionSettings;
	}

	/**
	 * @return the urlRepository
	 */
	public String getUrlRepository() {
		return urlRepository;
	}

	/**
	 * @param urlRepository the urlRepository to set
	 */
	public void setUrlRepository(String urlRepository) {
		this.urlRepository = urlRepository;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the skills
	 */
	public List<Skill> getSkills() {
		return skills;
	}

	/**
	 * @param skills the skills to set
	 */
	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	/**
	 * @return the ghosts
	 */
	public List<Ghost> getGhosts() {
		return ghosts;
	}

	/**
	 * @param ghosts the ghosts to set
	 */
	public void setGhosts(List<Ghost> ghosts) {
		this.ghosts = ghosts;
	}

	/**
	 * @return the location repository <br/><i>e.g. This is where the Git clone() occurs.</i>
	 */
	public String getLocationRepository() {
		return locationRepository;
	}
	
	/**
	 * @param locationRepository the location where the repository occurs.
	 */
	public void setLocationRepository(String locationRepository) {
		this.locationRepository = locationRepository;
	}

	/**
	 * @return the dependencies list
	 */
	public List<String> getDependencies() {
		return dependencies;
	}

	/**
	 * Initialize the dependencies list.
	 */
	public void initDependencies() {
		dependencies.clear();
	}

}
