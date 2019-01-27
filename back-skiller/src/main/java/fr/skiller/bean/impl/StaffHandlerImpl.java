package fr.skiller.bean.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.skiller.Error.CODE_STAFF_NOFOUND;
import static fr.skiller.Error.MESSAGE_STAFF_NOFOUND;
import static fr.skiller.Global.UNKNOWN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.core.net.SyslogOutputStream;
import fr.skiller.Error;
import fr.skiller.bean.DataSaver;
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

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Component
public class StaffHandlerImpl extends AbstractDataSaverLifeCycleImpl implements StaffHandler {

	/**
	 * First level of experience, and the default value for all new skill. 
	 */
	private final static int FIRST_LEVEL = 1;

	/**
	 * The logger.
	 */
	private Logger logger = LoggerFactory.getLogger(StaffHandlerImpl.class.getName());
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	/**
	 * The Project collection.
	 */
	private Map<Integer, Staff> staff;

	/**
	 * Handler for working the project collection.
	 */
	@Autowired
	ProjectHandler projectHandler;
	
	/**
	 * Bean in charge of saving/loading data.
	 */
	@Autowired
	DataSaver dataSaver;
		
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
		try {
			this.staff = dataSaver.loadStaff();
		} catch (final SkillerException e) {
			// Without staff, this application is not viable
			throw new RuntimeException(e);
		}
		return staff;

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
			
			// If the criteria contains only one word, we assume FIRST that this criteria is the login id
			ids = getStaff().values().stream()
				.filter(staff -> word[0].equals(staff.login))
				.collect(Collectors.toList());
			
			// If the criteria contains only one word which is not a login name, 
			// we assume that this criteria is the last name
			if (ids.size() == 0) {
				ids = getStaff().values().stream()
						.filter(staff -> word[0].equals(staff.lastName))
						.collect(Collectors.toList());				
			}
			
			// If the criteria contains only one word which is not a login name, 
			// we assume that this criteria is the first name
			if (ids.size() == 0) {
				ids = getStaff().values().stream()
						.filter(staff -> word[0].equals(staff.firstName))
						.collect(Collectors.toList());				
			}
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
			// If the criteria contains multiple words, we assume that this criteria is the full name of the contributor
			// Either with the firstName + " " + lastName, or the lastName + " " + firstName
			ids = getStaff().values().stream()
			.filter(staff -> criteria.trim().toLowerCase().equals(staff.fullName().toLowerCase()))
			.collect(Collectors.toList());
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

		// ids.size() == 0 at this point.
		return null;
	}
	
	
	
	
	@Override
	public List<Contributor> takeAccount(Project project, CommitRepository repository) throws SkillerException {
		
		List<Contributor> contributors = repository.contributors();
		contributors.stream().forEach(contributor -> {
			if (contributor.idStaff != UNKNOWN) {
				Staff staff = getStaff().get(contributor.idStaff);
				if (staff == null) {
					throw new RuntimeException("SEVERE ERROR : No staff member corresponding to the id " + contributor.idStaff);
				}
				if (staff.isInvolvedInProject(project.id)) {
					
					synchronized (lockDataUpdated) {
						// Update the statistics of the current developer inside the project
						staff.updateMission(project.id, contributor);
						this.dataUpdated = true;
					}
				} else {
					// Involve this developer inside a new project 
					Mission mission;
					try {
						mission = new Mission(
								project.id, 
								projectHandler.get(project.id).name,
								contributor.firstCommit, 
								contributor.lastCommit, 
								contributor.numberOfCommitsSubmitted, 
								contributor.numberOfFiles);
					} catch (SkillerException e) {
						// No exception excepted at the point
						throw new RuntimeException(e);
					}
					synchronized (lockDataUpdated) {
						staff.addMission(mission);
						this.dataUpdated = true;
					}
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

	@Override
	public Staff addNewStaffMember(final Staff staff)  {
		synchronized (lockDataUpdated) {
			Map<Integer, Staff> company = getStaff();
			staff.idStaff = company.size() + 1;
			company.put(staff.idStaff, staff);
			this.dataUpdated = true;
		}
		return staff;
	}

	@Override
	public boolean containsStaffMember(final int idStaff) {
		return getStaff().containsKey(idStaff);
	}

	@Override
	public void saveStaffMember(Staff staff) throws SkillerException {
		if (staff.idStaff == 0) {
			throw new SkillerException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, staff.idStaff));
		}
		synchronized (lockDataUpdated) {
			getStaff().put(staff.idStaff, staff);
			this.dataUpdated = true;
		}
	}

}
