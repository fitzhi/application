package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Generated;

import fr.skiller.Global;
import fr.skiller.SkillerRuntimeException;
import lombok.Data;

/**
 * Project class. 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Project  {

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
	private List<Library> libraries = new ArrayList<>();
	
	/**
	 * List of Sonar entries declared for this project.
	 */
	private List<SonarProject> sonarProjects = new ArrayList<>();
	
	/**
	 * Constant representing one the 2 models of connection settings.
	 * This one if for the direct access : url repository / user / password
	 */
	private static final int DIRECT_ACCESS = 1;
	
	/**
	 * Constant representing one the 2 models of connection settings.
	 * This one if for the indirect access : url repository / remote filename with connection parameters.
	 */
	private static final int REMOTE_FILE_ACCESS = 2;
	
	/**
	 * Level of risk evaluated for the project.
	 */
	private int risk = -1;
	
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

	/**
	 * Integrate a new library detected or declared to the list of exclusion paths.
	 * @param library the passed library
	 */
	public void add(Library library) {
		
		List<Library> libs = this.libraries
			.stream()
			.filter(lib -> lib.getExclusionDirectory().equals(library.getExclusionDirectory()))
			.collect(Collectors.toList());
		
		if (libs.size() > 1) {
			throw new SkillerRuntimeException(String.format("SHOULD NOT PASS HERE (%s)", library.getExclusionDirectory()));
		}
		
		if (libs.isEmpty()) {
			this.libraries.add(library);
			return;
		}
		
		Library previous = libs.get(0);
		
		switch (previous.getType()) {
			case Global.LIBRARY_DETECTED: 
			// If this library was intentionally removed from the audit, we do not try to enforce the add 
			case Global.LIBRARY_REMOVED: 
				break;	
				// The library was previously declared, now it is detected.
			case Global.LIBRARY_DECLARED:
				previous.setType(Global.LIBRARY_DETECTED);
				break;
			default:
				throw new SkillerRuntimeException("SHOULD NOT PASS HERE");
		}
	}

	
	/**
	 * @return a not null list of {@link SonarProject}
	 */
	public List<SonarProject> getSonarProjects() {
		if (this.sonarProjects == null) {
			this.sonarProjects = new ArrayList<SonarProject>();
		}
		return this.sonarProjects;
	}
}
