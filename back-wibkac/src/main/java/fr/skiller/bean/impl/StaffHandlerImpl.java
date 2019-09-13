package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_LOGIN_ALREADY_EXIST;
import static fr.skiller.Error.CODE_STAFF_NOFOUND;
import static fr.skiller.Error.MESSAGE_LOGIN_ALREADY_EXIST;
import static fr.skiller.Error.MESSAGE_STAFF_NOFOUND;
import static fr.skiller.Global.UNKNOWN;

import java.text.MessageFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.Error;
import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.DataSaver;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Experience;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.PeopleCountExperienceMap;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.ResumeSkill;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Main implementation of the {@link StaffHandler}.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Component
public class StaffHandlerImpl extends AbstractDataSaverLifeCycleImpl implements StaffHandler {

	/**
	 * First level of experience, and the default value for all new skill. 
	 */
	private static final int FIRST_LEVEL = 1;

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	/**
	 * The Project collection.
	 */
	private Map<Integer, Staff> theStaff;

	/**
	 * Bean in charge of saving/loading data.
	 */
	@Autowired
	DataSaver dataSaver;
		
	@Override
	public void init() {
		theStaff = null;
	}

	/**
	 * @return the Project collection.
	 */
	@Override
	public Map<Integer, Staff> getStaff() {

		if (this.theStaff != null) {
			return this.theStaff;
		}
		try {
			this.theStaff = dataSaver.loadStaff();
		} catch (final SkillerException e) {
			// Without staff, this application is not viable
			throw new SkillerRuntimeException(e);
		}
		return theStaff;

	}

	@Override
	public Staff put(int idStaff, Staff staff) {
		return this.getStaff().put(idStaff, staff);
	}

	@Override
	public PeopleCountExperienceMap countAllStaffGroupBySkillLevel(boolean isActiveOnly) {
		
		/**
		 * We build the complete list of experiences within the company.
		 */
		List<Experience> completeExperiences = new ArrayList<>();
		getStaff().values().stream()
				.filter(staff -> (!isActiveOnly || staff.isActive()))
				.forEach(staff -> completeExperiences.addAll(staff.getExperiences()));
		
		
		Map<String, Long> result = completeExperiences.stream()
			      .collect(Collectors.groupingBy(Experience::key, Collectors.counting()));

		if (log.isDebugEnabled()) {
			log.debug(String.format("'/countGroupBySkills' number of agregators %d",result.keySet().size()));
			
			for (Map.Entry<String, Long> entry : result.entrySet()) {
				log.debug(String.format("%s : %d", entry.getKey(), entry.getValue()));
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
		if (log.isDebugEnabled()) {
			log.debug("Working with the staff member " 
					+ (staff.getFirstName() == null ? "" : staff.getFirstName()) 
					+ "  " + staff.getLastName()); 
		}
		Set<Integer> currentExperience = staff.getExperiences().stream()
				.map(Experience::getId)
				.collect(Collectors.toSet());
		final List<ResumeSkill> listOfSkills = Arrays.asList(skills)
				.stream()
				.map(e -> (ResumeSkill) e)
				.collect(Collectors.toList()); 
		final List<ResumeSkill> listOfNewSkills = listOfSkills.stream()
			.filter(entry -> !currentExperience.contains(entry.getIdSkill())).collect(Collectors.toList());
		if (log.isDebugEnabled()) {
			log.debug(String.format("Adding %d new skills", listOfNewSkills.size()));
		}
		if (listOfNewSkills.isEmpty()) {
			throw new SkillerException(-1, 
					"There is no new skill to add for " + staff.getFirstName() + " " + staff.getLastName() +"!");
		}
		
		listOfNewSkills.forEach(skill -> 
			staff.getExperiences().add(
					new Experience(skill.getIdSkill(), FIRST_LEVEL)));
		
		return staff;
	}

	
	interface StringTransform {
		/**
		 * Transform a string into a new transformed one.
		 * @param input the input stream
		 * @return the transformed string
		 */
		String process(String input);
	}
	
	@Override
	public Staff lookup(String criteria)  {
		
		// First, we're processing the search with the natural String IN LOWER CASE
		Staff staff =  lookup(criteria, input -> (input != null) ? input.toLowerCase() : null);
		
		// If no one's found, we re-process the search with NORMALIZED AND LOWER CASE String
		if (staff == null) {
			staff =  lookup(criteria, input ->
					(input != null) ? Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "") : null);
		}
		
		return staff;
	}
	
	/**
	 * Lookup across the staff collection for a given criteria
	 * @param criteria the given criteria which might contain one or multiple words
	 * @param transform an interface of transformation for string to improve the search (such as rendering all string in lower case) 
	 * @return the staff found or {@code null} if none is found
	 */
	
	private Staff lookup(String criteria, StringTransform transform )  {

		if ((criteria == null) || (criteria.length() == 0)) {
			return null;
		}
		
		String[] word = criteria.split(" ");
		
		List<Staff> ids = null;
		switch (word.length) {
		case 1:
			// If the criteria contains only one word, we assume FIRST that this criteria is the login id
			ids = getStaff().values().stream()
				.filter(staff -> transform.process(word[0]).equals(transform.process(staff.getLogin())))
				.collect(Collectors.toList());
			
			// If the criteria contains only one word which is not a login name, 
			// we assume that this criteria is the last name
			if (ids.isEmpty()) {
				ids = getStaff().values().stream()
						.filter(staff -> transform.process(word[0]).equals(transform.process(staff.getLastName())))
						.collect(Collectors.toList());				
			}
			
			// If the criteria contains only one word which is not a login name, 
			// we assume that this criteria is the first name
			if (ids.isEmpty()) {
				ids = getStaff().values().stream()
						.filter(staff -> transform.process(word[0]).equals(transform.process(staff.getFirstName())))
						.collect(Collectors.toList());				
			}
			break;
		case 2:			
			// If the criteria contains only 2 words, we assume that this criteria is the first name and the last name
			ids = getStaff().values().stream()
			.filter(staff -> transform.process(word[0]).equals(transform.process(staff.getLastName())))
			.filter(staff -> transform.process(word[1]).equals(transform.process(staff.getFirstName())))
			.collect(Collectors.toList());
			
			// The criteria may be in the form "firstName lastName" or "lastName firstName"
			if (ids.isEmpty()) {
				ids = getStaff().values().stream()
						.filter(staff -> transform.process(word[0]).equals(transform.process(staff.getFirstName())))
						.filter(staff -> transform.process(word[1]).equals(transform.process(staff.getLastName())))
						.collect(Collectors.toList());				
			}
			break;
		default:
			// If the criteria contains multiple words, we assume that this criteria is the full name of the contributor
			// Either with the firstName + " " + lastName, or the lastName + " " + firstName
			// We will rotate words inside the criteria 
			// in order to test any combinations of criteria ("John William Doe Senior" -> "William Doe Senior John" --> ...)
			if (log.isDebugEnabled()) {
				log.debug(String.format("Rotation of words within the criteria %s and trying a lookup", criteria));
			}
			for (int i=0; i<word.length; i++) {
				
				List<String> rotatedCriteria = new ArrayList<>();
				for (int j=0; j<word.length; j++) {
					rotatedCriteria.add(word[(j+i)%word.length]);
				}
				
				StringBuilder sb = new StringBuilder();
				rotatedCriteria.stream().forEach(e -> sb.append(e).append(" "));
				
				ids = getStaff().values().stream()
						.filter(staff -> transform.process(sb.toString().trim()).equals(transform.process(staff.fullName())))
						.collect(Collectors.toList());
				
				if (!ids.isEmpty()) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("          ---> %s OK ! :-)", sb.toString()));
					}
					break;
				} else {
					if (log.isDebugEnabled()) {
						log.debug(String.format("          ---> %s KO", sb.toString()));
					}					
				}
			}
		}

		if (ids == null) {
			return null;
		}
		if (ids.size() == 1) {
			return ids.get(0);
		}
		if (ids.size() > 1) {
			if (log.isWarnEnabled()) {
				log.warn(String.format("Multiple ids for this criteria %s", criteria));
				log.warn("Ids listed below :");
				ids.stream().forEach(staff -> log.warn(String.format("%d %s %s", staff.getIdStaff(), staff.getFirstName(), staff.getLastName())));
				log.warn("By default, we assumed to return the first one...");
			}
			return ids.get(0);
		}

		// ids.size() == 0 at this point.
		return null;
	}
	
	@Override
	public void involve(Project project, List<Contributor> contributors) throws SkillerException {
		
		contributors.stream().forEach(contributor -> {
			if (contributor.getIdStaff() != UNKNOWN) {
				Staff staff = getStaff().get(contributor.getIdStaff());
				if (staff == null) {
					throw new SkillerRuntimeException("SEVERE ERROR : No staff member corresponding to the id " + contributor.getIdStaff());
				}
				if (staff.isInvolvedInProject(project.getId())) {
					synchronized (lockDataUpdated) {
						// Update the statistics of the current developer inside the project
						staff.updateMission(project.getId(), contributor);
						this.dataUpdated = true;
					}
				} else {
					// Involve this developer inside a new project 
					Mission mission = new Mission(
								contributor.getIdStaff(),
								project.getId(), 
								project.getName(),
								contributor.getFirstCommit(), 
								contributor.getLastCommit(), 
								contributor.getNumberOfCommitsSubmitted(), 
								contributor.getNumberOfFiles());
					synchronized (lockDataUpdated) {
						staff.addMission(mission);
						this.dataUpdated = true;
					}
				}
			}			
		});
	}

	@Override
	public List<Contributor> getContributors(int idProject) {
		List<Mission> missions = getStaff().values()
			.stream()
			.map(Staff::getMissions)
			.flatMap(Collection::stream)
			.filter(mission -> mission.getIdProject() == idProject)
			.collect(Collectors.toList());
		
		if (log.isDebugEnabled()) {
			log.debug("Contributors retrieved ");
			missions.stream().forEach(Mission::getIdStaff);
		}
		final List<Contributor> contributors = new ArrayList<>();
		
		missions.forEach(mission -> contributors.add(
				new Contributor(
						mission.getIdStaff(), 
						mission.getFirstCommit(), 
						mission.getLastCommit(), 
						mission.getNumberOfCommits(),
						mission.getNumberOfFiles())));
		
		return contributors;
	}

	@Override
	public boolean isActive(int idStaff)  {
		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new SkillerRuntimeException("SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, idStaff));
		}
		return staff.isActive();
	}

	@Override
	public String getFullname(int idStaff) {
		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			return null;
		}
		return ((staff.getFirstName() != null) ? staff.getFirstName() : "") + " " + ((staff.getLastName() != null) ? staff.getLastName() : "");
	}

	@Override
	public Staff addNewStaffMember(final Staff staff)  {
		synchronized (lockDataUpdated) {
			Map<Integer, Staff> company = getStaff();
			staff.setIdStaff(company.size() + 1);
			company.put(staff.getIdStaff(), staff);
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

		if (staff.getIdStaff() == 0) {
			throw new SkillerException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, staff.getIdStaff()));
		}
		
		// The login is unique for each Wibkac user
		Optional<Staff> emp = findStaffWithLogin(staff.getLogin());
		if ( (emp.isPresent()) && (emp.get().getIdStaff() != staff.getIdStaff()) && (emp.get().getLogin().equals(staff.getLogin()))) {
			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"the employee %d %s gets the same login %s as %d %s" , 
						staff.getIdStaff(), staff.fullName(),
						staff.getLogin(),
						emp.get().getIdStaff(), emp.get().fullName()));
			}
			throw new SkillerException(CODE_LOGIN_ALREADY_EXIST, MessageFormat.format(MESSAGE_LOGIN_ALREADY_EXIST, staff.getLogin(), emp.get().getFirstName(), emp.get().getLastName()));			
		}
		
		synchronized (lockDataUpdated) {
			getStaff().put(staff.getIdStaff(), staff);
			this.dataUpdated = true;
		}
	}

	@Override
	public Optional<Staff> findStaffWithLogin(String login) {
		Objects.requireNonNull(login);
		return getStaff()
				.values()
				.stream()
				.filter(e->login.equals(e.getLogin()))
				.findFirst();
	}

	@Override
	public void addExperience(int idStaff, Experience experience) {
		
		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new SkillerRuntimeException(
					"SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, idStaff));
		}
		
		Experience exp = staff.getExperience(experience.getId());
		
		synchronized (lockDataUpdated) {
			if (exp == null) {
				staff.getExperiences().add(experience);
			} else {
				exp.setLevel(experience.getLevel());
			}
			this.dataUpdated = true;
		}
	}

	@Override
	public void removeExperience(int idStaff, Experience experience) {
		
		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new SkillerRuntimeException(
					"SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, idStaff));
		}
		
		Experience exp = staff.getExperience(experience.getId());
		if (exp == null) return;
		
		synchronized (lockDataUpdated) {
			staff.getExperiences().remove(experience);
			this.dataUpdated = true;
		}
	}

	@Override
	public void updateExperience(int idStaff, Experience experience) {
		
		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new SkillerRuntimeException(
				"SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, idStaff));
		}
		
		Experience exp = staff.getExperience(experience.getId());
		if (exp == null) return;
		
		synchronized (lockDataUpdated) {
			exp.setLevel(experience.getLevel());
			this.dataUpdated = true;
		}
	}

	@Override
	public void addMission(int idStaff, int idProject, String projectName) {
		
		final Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new SkillerRuntimeException(
				"SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, idStaff));
		}
		
		synchronized (lockDataUpdated) {
			staff.getMissions().add(new Mission (idStaff, idProject, projectName));
			this.dataUpdated = true;
		}
	}

	@Override
	public void delMission(int idStaff, int idProject) {

		final Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new SkillerRuntimeException(
				"SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, idStaff));
		}
		
		Optional<Mission> oMission = staff.getMissions()
			.stream()
			.filter(mission -> mission.getIdProject() == idProject)
			.findFirst();
			
		if (!oMission.isPresent()) {
			throw new SkillerRuntimeException(
					"SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_MISSION_NOFOUND, idStaff, idProject));
		}
		synchronized (lockDataUpdated) {
			staff.getMissions().remove(oMission.get());
			this.dataUpdated = true;
		}
	}

	@Override
	public Staff getStaff(int idStaff) {
		return getStaff().get(idStaff);
	}
	
	
}
