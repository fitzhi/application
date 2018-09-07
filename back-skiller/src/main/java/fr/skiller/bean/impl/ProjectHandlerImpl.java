/**
 * 
 */
package fr.skiller.bean.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.data.internal.Project;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Component("mock.Project")
public class ProjectHandlerImpl implements ProjectHandler {
	
	/**
	 * The Project collection.
	 */
	private HashMap<Integer, Project> projects;

	/**
	 * @return the Project collection.
	 */
	public Map<Integer, Project> getProjects() {
		if (this.projects != null) {
			return this.projects;
		}
		this.projects = new HashMap<Integer, Project>();
		this.projects.put(1, new Project(1, "VEGEO"));
		this.projects.put(2, new Project(2, "INFOTER"));
		return projects;
	}

	@Override
	public Project get(final int idProject) {
		return getProjects().get(idProject);
	}

	@Override
	public Optional<Project> lookup(final String projectName) {
		return getProjects().values().stream()
				.filter( (Project project) -> project.name.equals(projectName))
				.findFirst();
	}

	@Override
	public void init() {
		this.projects = null;
	}

}
