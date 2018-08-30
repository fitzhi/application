package fr.skiller.bean;

import java.util.Map;
import java.util.Optional;

import fr.skiller.data.internal.Project;

public interface ProjectHandler {

	Map<Integer, Project> getProjects();
	
	Optional<Project> lookup(final String projectName);
	
}
