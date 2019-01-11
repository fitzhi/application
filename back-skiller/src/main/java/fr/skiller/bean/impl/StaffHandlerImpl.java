package fr.skiller.bean.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.skiller.Global.UNKNOWN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.Error;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.PeopleCountExperienceMap;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.ResumeSkill;
import fr.skiller.data.internal.Experience;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;

import com.google.gson.reflect.TypeToken;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Component
public class StaffHandlerImpl implements StaffHandler {

	/**
	 * First level of experience, and the default value for all new skill. 
	 */
	private final static int FIRST_LEVEL = 1;
	
	Logger logger = LoggerFactory.getLogger(StaffHandlerImpl.class.getName());
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	/**
	 * The Project collection.
	 */
	private HashMap<Integer, Staff> staff;

	@Autowired
	ProjectHandler projectHandler;
	
	
	@Override
	public void init() {
		staff = null;
	}

	/**
	 * @return the Project collection.
	 */
	@Override
	public Map<Integer, Staff> getStaff() {

		if (this.staff != null) {
			return this.staff;
		}

		File resourcesDirectory = new File("src/main/resources");
		String STAFF_JSON_FILE_PATH = resourcesDirectory.getAbsolutePath() + "/staff.json";
		BufferedReader br = null;
		this.staff = new HashMap<Integer, Staff>();
		try {
			StringBuilder sbContent = new StringBuilder();
			br = new BufferedReader(new FileReader(STAFF_JSON_FILE_PATH));
			String str;
			while ((str = br.readLine()) != null) {
				sbContent.append(str);
			}
			Type listType = new TypeToken<ArrayList<Staff>>() {}.getType();
			List<Staff> staffsRead = gson.fromJson(sbContent.toString(), listType);
			for (Staff staffRead : staffsRead) {
				this.staff.put(staffRead.idStaff, staffRead);
			}
			return this.staff;
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			return this.staff;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final Exception e) {
					logger.error("Incredible : " +e.getMessage());
				}
			}
		}
	}

	@Override
	public Staff put(int idStaff, Staff staff) {
		return this.getStaff().put(idStaff, staff);
	}

	@Override
	public PeopleCountExperienceMap countAllStaff_GroupBy_Skill_Level(boolean isActiveOnly) {
		
		/**
		 * We build the complete list of experiences within the company.
		 */
		List<Experience> completeExperiences = new ArrayList<Experience>();
		getStaff().values().stream()
				.filter(staff -> (isActiveOnly ? staff.isActive : true))
				.forEach(staff -> completeExperiences.addAll(staff.experiences));;
		
		
		Map<String, Long> result = completeExperiences.stream()
			      .collect(Collectors.groupingBy(exp -> exp.key(), Collectors.counting()));

		if (logger.isDebugEnabled()) {
			logger.debug("'/countGroupBySkills' number of agregators " + result.keySet().size());
			
			for (String key : result.keySet()) {
				logger.debug(key + " : " + result.get(key));
			}
		}
		
		PeopleCountExperienceMap count = new PeopleCountExperienceMap();
		count.putAll(result);
		return count;
	}

	@Override
	public Staff addExperiences(int idStaff, ResumeSkill[] skills) throws SkillerException {
		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new SkillerException(-1, "There is no staff for the ID " + idStaff);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Working with the staff member " 
					+ (staff.firstName == null ? "" : staff.firstName) 
					+ "  " + staff.lastName); 
		}
		Set<Integer> currentExperience = staff.experiences.stream()
				.map(exp -> exp.id)
				.collect(Collectors.toSet());
		final List<ResumeSkill> listOfSkills = Arrays.asList(skills)
				.stream()
				.map(e -> (ResumeSkill) e)
				.collect(Collectors.toList()); 
		final List<ResumeSkill> listOfNewSkills = listOfSkills.stream()
			.filter(entry -> !currentExperience.contains(entry.idSkill)).collect(Collectors.toList());
		if (logger.isDebugEnabled()) {
			logger.debug("Adding " + listOfNewSkills.size() + " new skills");
		}
		if (listOfNewSkills.isEmpty()) {
			throw new SkillerException(-1, 
					"There is no new skill to add for " + staff.firstName + " " + staff.lastName +"!");
		}
		
		listOfNewSkills.forEach(skill -> 
			staff.experiences.add(
					new Experience(skill.idSkill, skill.title, FIRST_LEVEL)));
		
		return staff;
	}

	@Override
	public Staff lookup(String criteria)  {

		if ((criteria == null) || (criteria.length() == 0)) {
			return null;
		}
		
		String[] word = criteria.split(" ");
		
		List<Staff> ids;
		switch (word.length) {
		case 1:
			// If the criteria contains only one word, we assume that this criteria is the login id
			ids = getStaff().values().stream()
				.filter(staff -> word[0].equals(staff.login))
				.collect(Collectors.toList());
			break;
		case 2:
			// If the criteria contains only 2 words, we assume that this criteria is the first name and the last name
			ids = getStaff().values().stream()
			.filter(staff -> word[0].toLowerCase().equals(staff.lastName.toLowerCase()))
			.filter(staff -> word[1].toLowerCase().equals(staff.firstName.toLowerCase()))
			.collect(Collectors.toList());
			// The criteria may be in the form "firstName lastName" or "lastName firstName"
			if (ids.size() == 0) {
				ids = getStaff().values().stream()
						.filter(staff -> word[0].toLowerCase().equals(staff.firstName.toLowerCase()))
						.filter(staff -> word[1].toLowerCase().equals(staff.lastName.toLowerCase()))
						.collect(Collectors.toList());				
			}
			break;
		default:
			throw new RuntimeException("Not implemented yet for teh criteria " + criteria);
		}

		if (ids.size() == 1) {
			return ids.get(0);
		}
		if (ids.size() > 1) {
			logger.warn("Multiple ids for this criteria " + criteria);
			logger.warn("Ids listed below :");
			ids.stream().forEach(staff -> logger.warn(staff.idStaff + " " + staff.firstName + " " + staff.lastName));
			logger.warn("By default, we assumed to return the first one...");
			return ids.get(0);
		}
		
		// Staff was not found.
		// has to be the case : (ids.size() == 0)
		if (logger.isWarnEnabled()) {
			logger.warn("No staff found under the criteria " + criteria);
		}
		return null;
	}
	
	@Override
	public List<Contributor> takeAccount(Project project, CommitRepository repository) {
		
		List<Contributor> contributors = repository.contributors();
		contributors.stream().forEach(contributor -> {
			if (contributor.idStaff != UNKNOWN) {
				Staff staff = getStaff().get(contributor.idStaff);
				if (staff == null) {
					throw new RuntimeException("SEVERE ERROR : No staff member corresponding to the id " + contributor.idStaff);
				}
				if (staff.isInvolvedInProject(project.id)) {
					// Update the statistics of the current developer inside the project
					Mission missionSelected = staff.missions.stream().filter(mission -> mission.idProject == project.id).findFirst().get();
					missionSelected.firstCommit = contributor.firstCommit;
					missionSelected.lastCommit = contributor.lastCommit;
					missionSelected.numberOfCommits = contributor.numberOfCommitsSubmitted;
					missionSelected.numberOfFiles = contributor.numberOfFiles;
					missionSelected.name = projectHandler.get(project.id).name;
				} else {
					// Involve this developer inside a new project 
					Mission mission = new Mission(
							project.id, 
							projectHandler.get(project.id).name,
							contributor.firstCommit, 
							contributor.lastCommit, 
							contributor.numberOfCommitsSubmitted, 
							contributor.numberOfFiles);
					staff.addMission(mission);
				}
				
			}			
		});
		return contributors;
	}
	
	@Override
	public boolean isActive(int idStaff)  {
		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new RuntimeException("SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, idStaff));
		}
		return staff.isActive;
	}

	@Override
	public String getFullname(int idStaff) {
		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			return null;
		}
		return ((staff.firstName != null) ? staff.firstName : "") + " " + ((staff.lastName != null) ? staff.lastName : "");
	}
}
