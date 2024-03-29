package com.fitzhi.data.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitzhi.Global;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.service.ConnectionSettingsType;

import lombok.Data;

import static com.fitzhi.service.ConnectionSettingsType.*;

/**
 * <p>
 * This class represents a project in the application.
 * </p> 
 * <img style="width:400px" src="https://fitzhi.com/class-diagram-project.png" alt="Diagram of the class project"/>
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
	 * If this property is equal to {@code true}, this project has to be taken in account in the analysis.
	 */	
	private boolean active = true;
	
	/**
	 * The connection settings model.
	 * 2 models exist : either the direct (URL/user/pass), or the indirect (URL/remote file), or public
	 */
	private ConnectionSettingsType connectionSettings = NO_LOGIN;

	/**
	 * The branch name. By default, the master branch is used.
	 */
	private String branch;
	
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
	 * Map of ProjectSkill indexed by {@code idSkill} detected or declared for this project.
	 */
	private Map<Integer, ProjectSkill> skills = new HashMap<>();
	
	/**
	 * List of committer {@link com.fitzhi.data.internal.Ghost ghosts} identified.<br/>
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
	 * URL of the codefactor.io dashboard for your project.
	 */
	private String urlCodeFactorIO;

	/**
	 * URL of the Sonar server hosting the Sonar projects related to this project
	 */
	private String urlSonarServer;
	
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
	 * "ordered" list by weight of ecosystems detected on the repository by either this application, or Sonar
	 */
	private List<Integer> ecosystems = new ArrayList<>();
	
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
	private Project(int id, String name, ConnectionSettingsType connectionSettings, String urlRepository, String locationRepository,
			String username, String password, String filename, Map<Integer, ProjectSkill> skills, List<Ghost> ghosts, String connectionSettingsFile) {
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
	 * <p>Copy constructor of {@link Project}</p>
	 * <p>This construction exists only for one purpose.<br/>
	 * It is used by the {@link ShuffleService} to shuffle the projects information and prevent any unintentional serialization.
	 * </p>
	 * @param project the project instance to be copied.
	 */
	public Project(Project project) {
		this (project.id, 
				project.name, 
				project.connectionSettings, 
				project.urlRepository, 
				project.locationRepository,
				project.username, project.password, 
				project.connectionSettingsFile, 
				project.skills, 
				project.ghosts, 
				project.connectionSettingsFile);
	}
	
	/**
	 * @return {@code true} if the project is setup with a direct access to the version control repository 
	 * <i>(the Project self-contains connection parameters)</i>
	 */
	@JsonIgnore
	public boolean isNoUserPasswordAccess() {
		return (connectionSettings == PUBLIC_LOGIN);
	}
	
	/**
	 * @return {@code true} if the project is setup with a direct access to the version control repository 
	 * <i>(the Project self-contains connection parameters)</i>
	 */
	@JsonIgnore
	public boolean isUserPasswordAccess() {
		return (connectionSettings == DIRECT_LOGIN);
	}
	
	/**
	 * @return {@code true} if the project is setup with an indirect access to the version control repository 
	 * <i>(Application must retrieve the connection parameters on a property file)</i>
	 */
	@JsonIgnore
	public boolean isRemoteFileAccess() {
		return (connectionSettings == REMOTE_FILE_LOGIN);
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
			throw new ApplicationRuntimeException(String.format("SHOULD NOT PASS HERE (%s)", library.getExclusionDirectory()));
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
				throw new ApplicationRuntimeException("SHOULD NOT PASS HERE");
		}
	}

	/**
	 * @return true if this project has a local repository.
	 */
	public boolean hasLocalRepository() {
		return ((this.locationRepository != null) && (this.locationRepository.length() > 1));
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
	
	/**
	 * <strong>A project is empty if</strong>
	 * <ul>
	 * <li>its skills set is empty</li>
	 * <li>its Sonar projects set is empty</li>
	 * <li>its audit evaluations set is empty</li>
	 * <li>its ghosts list is empty</li>
	 * <li>its dependencies (libraries) list is empty</li>
	 * <li>the repository location is empty</li>
	 * </ul>
	 * <p>
	 * <strong>WARNING</strong> : To be absolutely and completely empty, the reference of this project {@code idProject} 
	 * has not to be involved in a mission for any staff member.
	 * </p>
	 * @return {@code true} if the project is empty, otherwise {@code false}
	 * @see StaffHandler#isProjectReferenced(int)
	 */
	@JsonIgnore
	public boolean isEmpty() {
		if (!skills.isEmpty()) {
			return false;
		}
		if (!sonarProjects.isEmpty()) {
			return false;
		}
		if (!audit.isEmpty()) {
			return false;
		}
		if (!ghosts.isEmpty()) {
			return false;
		}
		if (!libraries.isEmpty()) {
			return false;
		}
		if ((locationRepository != null) && (locationRepository.length() > 0)) {
			return false;
		}
		return true;
		
	}
}
