package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_LOGIN_ALREADY_EXIST;
import static com.fitzhi.Error.CODE_STAFF_NOFOUND;
import static com.fitzhi.Error.MESSAGE_LOGIN_ALREADY_EXIST;
import static com.fitzhi.Error.MESSAGE_STAFF_NOFOUND;
import static com.fitzhi.Global.UNKNOWN;

import java.text.MessageFormat;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fitzhi.Error;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.PeopleCountExperienceMap;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ResumeSkill;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.internal.StaffActivitySkill;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	DataHandler dataSaver;
		
	/**
	 * Number of days of inactivity before inactivation of a staff member.
	 */
	@Value("${staffHandler.inactivity.delay}")
	private int inactivityDelay;	
	
	@PostConstruct
	public void postConstruct() {
		if (log.isInfoEnabled()) {
			log.info(String.format("Inactivity delay setup for Fitzhì : %d", inactivityDelay));
		}
	}
	
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
		} catch (final ApplicationException e) {
			// Without staff, this application is not viable
			throw new ApplicationRuntimeException(e);
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
	public Staff addExperiences(int idStaff, ResumeSkill[] skills) throws ApplicationException {
		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new ApplicationException(-1, "There is no staff for the ID " + idStaff);
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
			throw new ApplicationException(-1, 
					"There is no new skill to add for " + staff.getFirstName() + " " + staff.getLastName() +"!");
		}
		
		listOfNewSkills.forEach(skill -> 
			staff.getExperiences().add(
					new Experience(skill.getIdSkill(), FIRST_LEVEL)));
		
		return staff;
	}

		
	@Override
	public Staff lookup(String criteria)  {
		
		// First, we're processing the search with the natural String IN LOWER CASE
		Staff staff =  lookup(criteria, input -> (input != null) ? input.toLowerCase() : null);
		
		// If no one's found, we re-process the search with NORMALIZED AND LOWER CASE String
		if (staff == null) {
			staff =  lookup(criteria, input ->
					(input != null) ? Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "").toLowerCase() : null);
		}
		
		return staff;
	}
	
	@Override
	public boolean isEligible(Staff staff, String criteria) {
		if (!isEligible(staff, criteria, input -> (input != null) ? 
				input.replaceAll(" +", " ").replace("-", " ").trim().toLowerCase() : null)) {
			return isEligible(staff, criteria, 
					input -> (input != null) ? Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "").replaceAll(" +", " ").replace("-", " ").trim().toLowerCase() : null);
		}
		return true;
	}
	
	@Override
	public boolean isEligible(Staff staff, String criteria, StringTransform transform )  {
				
		String smartCriteria = transform.process(criteria);
		
		// Is the criteria equal to the login ?
		if (transform.process(smartCriteria).equals(transform.process(staff.getLogin()))) {
			return true;
		}
		
		
		String[] word = smartCriteria.split(" ");
		
		switch (word.length) {
		case 1:

			// If the criteria contains only one word, 
			//      which is NOT a login name,
			// we assume that this criteria might be the last name
			if (transform.process(word[0])
					.equals(transform.process(staff.getLastName()))) {
				return true;
			}
			
			
			// If the criteria contains only one word, 
			//      which is NOT a login name, and NOT a last name 
			// we assume that this criteria might be the last name
			if (transform.process(word[0])
					.equals(transform.process(staff.getFirstName()))) {
				return true;
			}
			
			return false;
		case 2:	
			// If the criteria contains only 2 words, we assume that this criteria is the last name and the first name
			if (	transform.process(word[0]).equals(transform.process(staff.getLastName()))
				&&	transform.process(word[1]).equals(transform.process(staff.getFirstName()))) {
				return true;
			}
			
			// The criteria may be in the form "firstName lastName" or "lastName firstName"
			if (	transform.process(word[0]).equals(transform.process(staff.getFirstName()))
				&&	transform.process(word[1]).equals(transform.process(staff.getLastName()))) {
					return true;
			}
			
			return false;
		default:
			//
			// If the criteria contains multiple words, we assume that this criteria is the full name of the contributor
			// Either with the firstName + " " + lastName, or the lastName + " " + firstName
			// We will rotate words inside the criteria 
			// in order to test any combination of criteria ("John William Doe Senior" -> "William Doe Senior John" --> ...)
			//
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
				if (transform.process(sb.toString()).equals(transform.process(staff.fullName()))) {
					return true;
				}
			}
			
			return false;
		}
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
		
		String[] word = criteria.trim().replaceAll(" +", " ").split(" ");
		
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
	public void involve(Project project, List<Contributor> contributors) throws ApplicationException {
		
		
		for (Contributor contributor : contributors) {
			if (contributor.getIdStaff() != UNKNOWN) {
				Staff staff = getStaff().get(contributor.getIdStaff());
				if (staff == null) {
					throw new ApplicationRuntimeException("SEVERE ERROR : No staff member corresponding to the id " + contributor.getIdStaff());
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
								contributor.getNumberOfFiles(),
								contributor.getStaffActivitySkill());
					synchronized (lockDataUpdated) {
						staff.addMission(mission);
						this.dataUpdated = true;
					}
				}
				
				// We update the skills for this contributor.
				this.inferSkillsFromMissions(contributor.getIdStaff());

				// We update his active or inactive state.
				this.processActiveStatus(staff);
			}
		};
		if (log.isDebugEnabled()) {
			log.debug(String.format("%d contributors retrieved : ", contributors.size()));
			contributors.stream().forEach(contributor -> {
				String fullname = this.getFullname(contributor.getIdStaff());
				log.debug(String.format("%d %s", contributor.getIdStaff(),
						(fullname != null) ? fullname : "unknown"));
			});
		}
		
	}

	@Override
	public void involve(Project project, Contributor contributor) throws ApplicationException {

		Staff staff = getStaff().get(contributor.getIdStaff());
		if (staff == null) {
			throw new ApplicationException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, contributor.getIdStaff()));
		}
		
		Optional<Mission> oMission = staff.getMissions().stream()
				.filter(mission -> mission.getIdProject() == project.getId())
				.findFirst();
		
		Mission mission = (oMission.isPresent()) ? oMission.get() : new Mission();
		mission.setIdProject(project.getId());
		mission.setName(project.getName());
		mission.setIdStaff(staff.getIdStaff());
		mission.setFirstCommit(contributor.getFirstCommit());
		mission.setLastCommit(contributor.getLastCommit());
		mission.setNumberOfCommits(contributor.getNumberOfCommitsSubmitted());
		mission.setNumberOfFiles(contributor.getNumberOfFiles());
		staff.addMission(mission);
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
			log.error ("SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, idStaff));
			return false;
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
	public boolean containsStaffMember(final int idStaff) {
		return getStaff().containsKey(idStaff);
	}

	@Override
	public void controlWorkforceMember(Staff input) throws ApplicationException {

		// This control is redundant with the control processed under the PUT verb in the StaffController
		// But we might create a staff member outside of the Rest controller
		if ( (input.getIdStaff() > 0) && !containsStaffMember(input.getIdStaff())) {
			throw new NotFoundException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, input.getIdStaff()));
		}

		//
		// The login is unique for each Fitxhì user.
		// If we find a staff member with the same login and a different identifier, we throw an exception.
		//
		Optional<Staff> emp = findStaffWithLogin(input.getLogin());
		if ( (emp.isPresent()) && (emp.get().getIdStaff() != input.getIdStaff()) && (emp.get().getLogin().equals(input.getLogin()))) {
			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"The staff member %d %s has the same login %s than %d %s" , 
						input.getIdStaff(), input.fullName(),
						input.getLogin(),
						emp.get().getIdStaff(), emp.get().fullName()));
			}
			throw new ApplicationException(CODE_LOGIN_ALREADY_EXIST, MessageFormat.format(MESSAGE_LOGIN_ALREADY_EXIST, input.getLogin(), emp.get().getFirstName(), emp.get().getLastName()));			
		}
	}

	@Override
	public Staff createWorkforceMember(final Staff staff) throws ApplicationException {
		controlWorkforceMember(staff);
		addNewStaff(staff);
		return staff;
	}

	@Override
	public Staff createEmptyStaff(String author) throws ApplicationException {
		String[] w = author.split(" ");
		Staff staff = new Staff();
		staff.setIdStaff(-1);
		if (w.length == 1) {
			staff.setFirstName("firstname");
			staff.setLastName(w[0]);
			staff.setNickName(w[0]);
			staff.setLogin(w[0]);
		} else {
			staff.setFirstName(w[0]);
			staff.setLastName(author.substring(w[0].length()+1));
			staff.setNickName(author);
			staff.setLogin(author);
		}
		addNewStaff(staff);
		return staff;
	}

	/**
	 * Add a new staff in the staff collection
	 * @param staff the new Staff member
	 */
	private void addNewStaff(Staff staff) {
		synchronized (lockDataUpdated) {
			Map<Integer, Staff> company = getStaff();
			if (staff.getIdStaff() <= 0) {	
				try {
					int max = company.keySet().stream()
						.mapToInt(v->v)
						.max()
						.orElseThrow(NoSuchElementException::new);
						staff.setIdStaff(max + 1);
				} catch (final NoSuchElementException e) {
					staff.setIdStaff(1);
				}
			}
			company.put(staff.getIdStaff(), staff);
			if (log.isInfoEnabled()) {
				log.info(String.format("Creation of staff member %d %s %s", 
					staff.getIdStaff(), staff.getFirstName(), staff.getLastName()));
			}
			this.dataUpdated = true;
		}
	}

	@Override
	public void updateWorkforceMember(Staff input) throws ApplicationException {

		controlWorkforceMember(input);

		Staff updStaff = getStaff(input.getIdStaff());
		if (updStaff == null) {
			throw new ApplicationRuntimeException(String.format("WTF : A staff with id %d should exist.", input.getIdStaff()));
		}
	
		
		updStaff.setFirstName(input.getFirstName());
		updStaff.setLastName(input.getLastName());
		updStaff.setNickName(input.getNickName());
		updStaff.setLogin(input.getLogin());
		updStaff.setEmail(input.getEmail());
		updStaff.setLevel(input.getLevel());
		updStaff.setForceActiveState(input.isForceActiveState());
		if ((input.isForceActiveState())) {
			updStaff.setActive(input.isActive());
			updStaff.setDateInactive((!input.isActive()) ? LocalDate.now() : null);
		}
		updStaff.setExternal(input.isExternal());
		
		synchronized (lockDataUpdated) {
			getStaff().put(updStaff.getIdStaff(), updStaff);
			this.dataUpdated = true;
		}
	}

	@Override
	public Staff removeStaff(int idStaff) {
		synchronized (lockDataUpdated) {
			Staff staff = getStaff().remove(idStaff);
			this.dataUpdated = true;
			return staff;
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
			throw new ApplicationRuntimeException(
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
			throw new ApplicationRuntimeException(
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
			throw new ApplicationRuntimeException(
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
			throw new ApplicationRuntimeException(
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
			throw new ApplicationRuntimeException(
				"SEVERE DATA CONSISTENCY ERROR " + MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, idStaff));
		}
		
		Optional<Mission> oMission = staff.getMissions()
			.stream()
			.filter(mission -> mission.getIdProject() == idProject)
			.findFirst();
			
		if (!oMission.isPresent()) {
			throw new ApplicationRuntimeException(
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

	
	@Override
	public boolean hasStaff(int idStaff) {
		return getStaff().containsKey(idStaff);
	}
	
	@Override
	public void savePassword(Staff staff, String password) {
		synchronized (lockDataUpdated) {
			staff.setPassword(password);
			this.dataUpdated = true;
		}
	}

	
	private List<StaffActivitySkill> activity(Staff staff) {
		return staff.getMissions().stream()
		.flatMap(mission -> mission.getStaffActivitySkill().values().stream())
		.collect(Collectors.toList());
		
	}
	
	@Override
	public void inferSkillsFromMissions(int idStaff) throws ApplicationException {

		Staff staff = getStaff().get(idStaff);
		if (staff == null) {
			throw new ApplicationException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}

		List<StaffActivitySkill> activity = activity(staff);

		//
		// Internal check
		//
		if (activity.stream().anyMatch(sas -> sas.getIdStaff() != idStaff)) {
			throw new ApplicationRuntimeException(
					String.format("INTERNAL ERROR : %d has to be the unique staff identifier in all his StaffActivitySkill", idStaff));
		}
		
		Map<Integer, Integer> skills = activity.stream().collect(
                Collectors.groupingBy(StaffActivitySkill::getIdSkill, Collectors.summingInt(StaffActivitySkill::getNumberOfChanges)));

		for (int idSkill : skills.keySet()) {
			if (staff.getExperience(idSkill) == null) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("Adding the skill %d to the developer %s", idSkill, staff.fullName()));
				}
				staff.getExperiences().add(new Experience(idSkill, 1));
			}
		}
	}

	@Override
	public void processActiveStatus(Staff staff) {
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"Processing the active status for %s", staff.fullName()));
		}
		
		// Nothing to process if the user chooses a manual activity state
		if (staff.isForceActiveState()) {
			return;
		}

		// No activity at all, we consider this staff member as a new one, so therefore active.
		if ((staff.getMissions() == null) || (staff.getMissions().isEmpty())) {
			staff.setActive(true);
			staff.setDateInactive(null);				
			return;
		}
		
		Optional<LocalDate> optionalLatestCommit = staff.getMissions()
				.stream()
					.map(Mission::getLastCommit)
					.filter(Objects::nonNull)
					.max(Comparator.comparing(LocalDate::toEpochDay));
		
		// No update possible, because there is no date of commit recorded in the missions.
		if (!optionalLatestCommit.isPresent()) {
			return;
		}
		
		synchronized (lockDataUpdated) {
			if (LocalDate.now().minusDays(inactivityDelay).isAfter(optionalLatestCommit.get())) {
				staff.setActive(false);
				staff.setDateInactive(optionalLatestCommit.get());
			} else {
				staff.setActive(true);
				staff.setDateInactive(null);				
			}
		}
	}

	@Override
	public void forceActiveStatus(Staff staff) {
		synchronized (lockDataUpdated) {
			if (staff.isActive()) {
				staff.setActive(false);
				staff.setDateInactive(LocalDate.now());
			} else {
				staff.setActive(true);
				staff.setDateInactive(null);
			}
		}
	}

	@Override
	public boolean isProjectReferenced(int idProject) {
		return this.getStaff().values().stream()
			.flatMap(staff -> staff.getMissions().stream())
			.filter(mission -> (mission.getIdProject() == idProject))
			.findAny()
			.isPresent(); 
	}

	
}
