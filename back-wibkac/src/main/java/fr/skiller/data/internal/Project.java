package fr.skiller.data.internal;

import static fr.skiller.Global.DIRECT_ACCESS;
import static fr.skiller.Global.REMOTE_FILE_ACCESS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Generated;

import fr.skiller.Global;
import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.ShuffleService;
import lombok.Data;

/**
 * <p>
 * This class represents a project in the application.
 * </p> 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Project implements Serializable {

	/**
	 * serialVersionUID for serialization
	 */
	private static final long serialVersionUID = 4167131827487544764L;

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
	private String connectionSettingsFile;
	
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
	 * List of Sonar projects associated to this project.
	 */
	private List<SonarProject> sonarProjects = new ArrayList<>();
			
	/**
	 * Map of {@link AuditTopic} associated to this project.
	 */
	private Map<Integer, AuditTopic> audit = new HashMap<>();
	
	/**
	 * Global audit evaluation
	 */
	private int auditEvaluation;
	
	/**
	 * Staff evaluation, representing the percentage of active developers able to maintain the project.
	 */
	private int staffEvaluation = -1;
	
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
			String username, String password, String filename, List<Skill> skills, List<Ghost> ghosts, String connectionSettingsFile) {
		super();
		this.id = id;
		this.name = name;
		this.connectionSettings = connectionSettings;
		this.urlRepository = urlRepository;
		this.locationRepository = locationRepository;
		this.username = username;
		this.password = password;
		this.connectionSettingsFile = connectionSettingsFile;
		this.skills = skills;
		this.ghosts = ghosts;
	}
	
	/**
	 * <p>Copy constructor of {@link Project}</code></p>
	 * <p>This construction exists only for one purpose.<br/>
	 * It is used by the {@link ShuffleService} to shuffle the projects information and prevent any unintentional serialization.
	 * </p>
	 * @param project the project instance to be copied.
	 */
	public Project(Project project) {
		this (project.id, project.name, project.connectionSettings, 
				project.urlRepository, 
				project.locationRepository,
				project.username, project.password, 
				project.connectionSettingsFile, 
				project.skills, project.ghosts, project.connectionSettingsFile);
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
	 * <p>
	 * Integrate a new library detected or declared to the list of exclusion paths.
	 * </p>
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
