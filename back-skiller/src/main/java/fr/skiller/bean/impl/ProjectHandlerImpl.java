/**
 * 
 */
package fr.skiller.bean.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Project;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.Contributor;
import fr.skiller.data.internal.Mission;
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
	 * The staff Handler.
	 */
	@Autowired
	public StaffHandler staffHandler;
	
	/**
	 * @return the Project collection.
	 */
	@Override
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

	@Override
	public List<Contributor> contributors(int idProject) {
		List<Contributor> contributors = new ArrayList<Contributor>();
		staffHandler.getStaff().values().forEach(staff -> {
			Optional<Mission> optMission  = 
						staff.missions.stream()
						.filter(mission -> mission.idProject == idProject)
						.findFirst();
			if (optMission.isPresent()) {
				Mission mission = optMission.get();
				contributors.add(
						new Contributor(
								staff.idStaff, 
								mission.firstCommit, 
								mission.lastCommit, 
								mission.numberOfCommits, 
								mission.numberOfFiles));
			}
		});
		return contributors;
	}
}
