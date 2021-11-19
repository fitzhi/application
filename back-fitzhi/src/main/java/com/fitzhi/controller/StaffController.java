package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static com.fitzhi.Error.CODE_STAFF_ACTIVE_ON_PROJECT;
import static com.fitzhi.Error.CODE_STAFF_NOFOUND;
import static com.fitzhi.Error.MESSAGE_PROJECT_NOFOUND;
import static com.fitzhi.Error.MESSAGE_STAFF_ACTIVE_ON_PROJECT;
import static com.fitzhi.Error.MESSAGE_STAFF_NOFOUND;
import static com.fitzhi.Error.CODE_YEAR_MONTH_INVALID;
import static com.fitzhi.Error.MESSAGE_YEAR_MONTH_INVALID;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.util.YearMonthParser;
import com.fitzhi.data.external.StaffResume;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.PeopleCountExperienceMap;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Resume;
import com.fitzhi.data.internal.ResumeSkill;
import com.fitzhi.data.internal.ResumeSkillIdentifier;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.service.FileType;
import com.fitzhi.service.ResumeParserService;
import com.fitzhi.service.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;


/**
 * Controller in charge of handling the staff member of the organization.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/staff")
@Api(
	tags="Staff controller API",
	description = "API endpoints to manage the developers declared in the application. By convention we consider all employees as developers."	
)
public class StaffController {

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	StaffHandler staffHandler;

	@Autowired
	SkillHandler skillHandler;

	/**
	 * Storage service dedicated to upload/download the staff applications.
	 */
	@Autowired
	@Qualifier("Application")
    StorageService storageService;

	@Autowired
    ResumeParserService resumeParserService;

	/**
	 * Are we in shuffle mode ?
	 */
	@Autowired
	ShuffleService shuffleService;

	/**
	 * <p>
	 * This method creates a new staff member.
	 * </p>
	 * @param builder the {@code Spring} URI builder
	 * @param staff the staff to be created
	 * @return a ResponseEntity with just the location containing the URI of the newly
	 *         created staff
	 */
	@ApiOperation(
		value="Create a new developer in th staff.", 
		notes = "If the ID is filled and registered, a CONFLICT response error will be returned."
	)
	@PostMapping("")
	public ResponseEntity<Void> create(UriComponentsBuilder builder, 
			@ApiParam(value="An unregistered developer. The ID should be equal to -1")
			@RequestBody Staff staff)
			throws ApplicationException {

		// This is not a creation.
		if (staffHandler.containsStaffMember(staff.getIdStaff())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		if (log.isDebugEnabled()) {
			log.debug (String.format("Adding the staff member %s %s", staff.getFirstName(), staff.getLastName()));
			log.debug (String.format("staff.toString() : %s ", staff.toString()));
		}

		// We create an already inactive user. 
		// That kind of user is supposed to be created from the ghosts table form
		if ( !staff.isActive() && (staff.getDateInactive() == null)) {
			staff.setDateInactive(LocalDate.now());
		}

		Staff newStaff = staffHandler.createWorkforceMember(staff);

		UriComponents uriComponents = builder.path("/api/staff/{id}").buildAndExpand(newStaff.getIdStaff());

		return ResponseEntity.created(uriComponents.toUri()).build();
	}

	/**
	 * <p>
	 * Update the staff member identified by the given Staff identifier.
	 * </p>
	 * @param idStaff the staff identifier. The staff identifier is hosted in the URL in accordance with the Rest naming conventions
	 * @param staff the staff to update. This staff is hosted inside the body of the {@code PUT} Medhod.
	 */
	@ApiOperation(value = "Update an existing developer.")
	@PutMapping("/{idStaff}")
	public ResponseEntity<Void> updateStaff(@PathVariable("idStaff") int idStaff, @RequestBody Staff staff)
			throws NotFoundException, ApplicationException {

		if (idStaff != staff.getIdStaff()) {
			throw new ApplicationRuntimeException("WTF : SHOULD NOT PASS HERE : idStaff in URL is distinct from idStaff in Staff object");
		}

		if (!staffHandler.containsStaffMember(idStaff)) {
			throw new NotFoundException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}

		if (log.isDebugEnabled()) {
			log.debug (String.format("Updating the staff %d %s %s", staff.getIdStaff(), staff.getFirstName(), staff.getLastName()));
			log.debug (String.format("staff.toString() : %s ", staff.toString()));
		}

		staffHandler.updateWorkforceMember(staff);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Read all staff members from the workforce.
	 * @return the complete workforce of the company
	 */
	@ResponseBody
	@ApiOperation(value = "Read and return all developers declared in the staff.")
	@GetMapping("")
	public Collection<Staff> readAll() {
		
		final Collection<Staff> staffTeam = staffHandler.getStaff().values();
		
		if (shuffleService.isShuffleMode()) {
			if (log.isInfoEnabled()) {
				log.info("The projects collection has been shuffled");
			}
			staffTeam.stream().forEach(staff -> {
				staff.setFirstName(shuffleService.shuffle(staff.getFirstName()));
				staff.setLastName(shuffleService.shuffle(staff.getLastName()));
				staff.getMissions().stream().forEach(mission -> mission.setName(shuffleService.shuffle(mission.getName())));
			});
		}
		return staffTeam;
	}

	@ResponseBody
	@ApiOperation(value = "Aggregate and count by skills all ACTIVE developers")
	@GetMapping("/countGroupByExperiences/active")
	public Map<String, Long> countActive() {
		
		final PeopleCountExperienceMap peopleCountExperienceMap = staffHandler.countAllStaffGroupBySkillLevel(true);

		if (log.isDebugEnabled()) {
			log.debug(String.format("'/countGroupBySkills/active' is returning %d experiences", peopleCountExperienceMap.getData().size()));
		}
		
		return peopleCountExperienceMap.getData();
	}

	@ResponseBody
	@GetMapping("/countGroupByExperiences/all")
	@ApiOperation(value = "Aggregate and count by skills ALL developers")
	public Map<String, Long> countAll() {
		
		final PeopleCountExperienceMap peopleCountExperienceMap = staffHandler.countAllStaffGroupBySkillLevel(false);

		if (log.isDebugEnabled()) {
			log.debug(String.format("'/countGroupBySkills/all' is returning %d experiences", peopleCountExperienceMap.getData().size()));
		}
		
		return peopleCountExperienceMap.getData();
	}

	/**
	 * @param idStaff
	 *            staff member's identifier
	 * @return the staff member identified by its id
	 */
	@ApiOperation(value = "Retrieve a developer by his identifier")
	@ResponseBody
	@GetMapping(value = "/{idStaff}")
	public Staff read(@PathVariable("idStaff") int idStaff) throws ApplicationException {
		return staffHandler.getStaff(idStaff);
	}

	/**
	 * @param idStaff staff member's identifier
	 * @return the list of projects where the staff member is involved
	 */
	@ResponseBody
	@ApiOperation(value = "Retrieve the projects where a developer has been involved.")
	@GetMapping(value = "/{idStaff}/project")
	public List<Mission> readProjects(@PathVariable("idStaff") int idStaff) 
		throws ApplicationException {

		final Staff staff = staffHandler.getStaff(idStaff);

		// Adding the name of project.
		for (Mission mission : staff.getMissions()) {
				mission.setName(projectHandler.lookup(mission.getIdProject()).getName());
		}
		
		return staff.getMissions();
	}

	/**
	 * @param idStaff The Staff member's identifier
	 * @return the given developer's experience as list of skills.
	 * @throws ApplicationException thrown if any problem occurs, such as the staff identifier does not exist.
	 */
	@ResponseBody
	@ApiOperation(
		value = "Retrieve the experiences collection of a developer.", 
		notes = "An experience is a skill with a level. An experience belongs to an application."
	)
	@GetMapping(value = "/{idStaff}/experience")
	public List<Experience> readExperiences(@PathVariable("idStaff") int idStaff) throws ApplicationException {
		Staff staff = staffHandler.getStaff(idStaff);
		return staff.getExperiences();
	}

	@ResponseBody
	@ApiOperation(
		value = "Switch the 'active' status of a developer",
		notes = "If the given developer is active, it will become inactive. If inactive, it will be switched to active.")
	@PostMapping("/{idStaff}/switchActiveStatus")
	public boolean switchActiveState(@PathVariable("idStaff") int idStaff) throws ApplicationException {
		final Staff staff = staffHandler.getStaff(idStaff);
		this.staffHandler.forceActiveStatus(staff);
		return true;
	}

	/**
	 * <p>
	 * Update the active status for a developer.
	 * </p>
	 * <p>
	 * This URL is invoked when the end-user decides that the 'active' state should be automatically processed.<br/>
	 * Therefore, the application scan the activity of the given staff member to check their latest commit with the global parameter {@code staffHandler.inactivity.delay}
	 * </p>
	 * @param idStaff the identifier of the staff member to activate, or deactivate.
	 * @return the updated staff
	 * @throws ApplicationException thrown if any exception occurs during the treatment, most probably if there is no staff member for the given id.
	 * @see #switchActiveState(int)
	 */
	@ApiOperation(
		value = "Update the active status for a developer.",
		notes = 
			"This endpoint is invoked to set that the 'active' state should be forced by the application."
		 + 	"When forced, the application will scan the activity of this developer in order to compare his/her latest commit with the global parameter 'inactivity.delay'."
	)
	@ResponseBody
	@PostMapping("/{idStaff}/processActiveStatus")
	public Staff processActiveStatus(@PathVariable("idStaff") int idStaff) throws ApplicationException {
		
		final Staff staff = staffHandler.getStaff(idStaff);
		
		// We process the active status, therefore we property forceActiveStatus should be set to False.
		staff.setForceActiveState(false);
		
		this.staffHandler.processActiveStatus(staff);
		
		return staff;
	}

	
	/**
	 * Delete the staff member corresponding to the identifier.
	 * @param idStaff the Staff member identifier candidate for deletion
	 */
	@ResponseBody
	@ApiOperation(value = "Remove a developer from the company staff.")
	@DeleteMapping(value = "/{idStaff}")
	public void removeStaff(@PathVariable("idStaff") int idStaff) throws NotFoundException, ApplicationException {
		// We test first the existence of a Staff member for this Staff identifier
		staffHandler.getStaff(idStaff);
		// Then We remove
		staffHandler.removeStaff(idStaff);
	}
	
	/**
	 * We do not allow to remove all staff members
	 * @return an empty HTTP Response because this method is not allowed.
	 */
	@ApiOperation(value = "This method is not allowed.")
	@DeleteMapping()
	public ResponseEntity<Object> removeAllStaff() throws ApplicationException {		
		return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * <p>
	 * Add an {@link Experience experience} to a staff member.
	 * </p>
	 * @param idStaff the Staff identifier
	 * @param experience the experience to be updated
	 * 
	 * @return {@code true} if the update succeeds, {@code false} otherwise
	 */
	@ResponseBody
	@ApiOperation(value = "Add an experience to a developer.", notes = "An experience is a skill and a level.")
	@PostMapping("{idStaff}/experience")
	public boolean updateExperience(
		@PathVariable("idStaff") int idStaff,
		@RequestBody Experience experience) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/add with params id:%d, idSkill:%d, level:%d", 
					idStaff, experience.getId(), experience.getLevel()));
		}

		final Staff staff = staffHandler.getStaff(idStaff);
		
		Experience formerExperience = staff.getExperience(experience.getId());
		if (formerExperience == null) {
			this.staffHandler.addExperience(idStaff, new Experience(experience.getId(), experience.getLevel()));
		} else {
			this.staffHandler.removeExperience(idStaff, formerExperience);
			this.staffHandler.addExperience(idStaff, new Experience(experience.getId(), experience.getLevel()));
		}
		return true;
	}
	
	/**
	 * Add an experience to a staff member.
	 * @param idStaff the staff identifier
	 * @param idSkill the skill identifier
	 * @return {@code true} if the removal succeeds, {@code false} otherwise
	 */
	@ResponseBody
	@ApiOperation(value = "Remove a skill from a developer.")
	@DeleteMapping(value = "{idStaff}/experience/{idSkill}")
	public boolean removeExperience(
		@PathVariable("idStaff") int idStaff,
		@PathVariable("idSkill") int idSkill) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/remove with params idStaff:%d, idSkill:%d", 
					idStaff, idSkill));
		}

		final Staff staff = staffHandler.getStaff(idStaff);
		
		Experience experience = staff.getExperience(idSkill);
		if (experience != null) {
			this.staffHandler.removeExperience(idStaff, experience);
		}
		return true;		
	}
	
	/**
	 * Upload the application of a staff member on a server.
	 * 
	 * <p>
	 * The verb is {@code POST} because we upload a file on the file system and we consider that an upload is a modification.
	 * </p>
	 *
	 * @param idStaff the staff identifier whose application will be uploaded
	 * @param file the application
	 * @param type the type of file (WORD, PDF...)
	 * @throws ApplicationException thrown if any problem occurs
	 * 
	 * @return the resume parsed from the uploaded file.
	 */
	@ResponseBody
	@ApiOperation(value = "Upload an application file and return an array of experiences detected in this file.")
	@PostMapping("/{idStaff}/uploadCV")
	public StaffResume uploadApplicationFile(
			@PathVariable("idStaff") int idStaff, 
			@RequestParam("file") MultipartFile file, 
			@RequestParam("type") int type) throws ApplicationException {
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());

		if (log.isDebugEnabled()) {
			log.debug(String.format("Uploading %s for staff identifer %d of type %s", 
				filename, idStaff, type));
		}

		FileType typeOfApplication = FileType.valueOf(type);
		
		final Staff staff = staffHandler.getStaff(idStaff);

		storageService.store(file, buildFileName(staff, filename));

		staff.updateApplication(filename, typeOfApplication);
		
		final Resume resume = resumeParserService.extract(buildFileName(staff, filename), typeOfApplication);
		
		final List<ResumeSkill> experiences = genResume(resume);

		return new StaffResume(experiences);
	}

	private List<ResumeSkill> genResume(Resume resume) throws ApplicationException {
		List<ResumeSkill> experiences = new ArrayList<ResumeSkill>();
		for (ResumeSkillIdentifier rsi : resume.getExperiences()) {
			experiences.add(new ResumeSkill(
				rsi.getIdSkill(), 
				skillHandler.getSkill(rsi.getIdSkill()).getTitle(),
				rsi.getCount()));
		}
		//
		// We put the most often repeated keywords at the beginning of the list.
		//
		Collections.sort(experiences);
		return experiences;
	}

	/**
	 * <p>
	 * Build the local filename associated to the staff.<br/>
	 * We build a local filename to avoid duplicated entries in the upload directory.
	 * </p>
	 * @param staff the given staff
	 * @param filename the local filename on the user desktop
	 * @return a <u>unique</u> filename to store the application.
	 */
	private String buildFileName(Staff staff, String filename) {
		return staff.getIdStaff() + "-" + filename;
	}
	
	
	/**
	 * Download the staff member application. 
	 * @param id the staff identifier
	 * @param request type type of request
	 * @return the file resource
	 */
	@ApiOperation(value = "Download the application for the given developer, if any.")
	@GetMapping(value = "{id}/application")
	public ResponseEntity<Resource> downloadApplicationFile(
		    @PathVariable("id") int id, 
		    HttpServletRequest request) {

		final Staff staff = staffHandler.getStaff().get(id);
		assert (staff != null);

		if ((staff.getApplication() == null) || (staff.getApplication().length() == 0)) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("No application file for %d", staff.getIdStaff()));
			}
	        return ResponseEntity.notFound().build();
		}
		
		// Load file as Resource
		Resource resource = storageService.loadAsResource(buildFileName(staff, staff.getApplication()));

		String contentType = storageService.getContentType(FileType.valueOf(staff.getTypeOfApplication()));
		if (log.isDebugEnabled()) {
			log.debug(String.format("%s %s", staff.getApplication(), contentType));
		}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}
		
	@ResponseBody
	@ApiOperation("Save the given resume for the staff member.")
	@PutMapping("/{idStaff}/resume")
	public Staff saveResume(
		@PathVariable("idStaff") int idStaff, 
		@RequestBody ResumeSkill[] skills) 
		throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("Adding %d skills for the staff ID %d", skills.length, idStaff));
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("Adding the skills below for the staff identifier %d", idStaff));
			Arrays.asList(skills).stream().forEach(skill -> log.trace(String.format("%s %s", skill.getIdSkill(), skill.getTitle())));
		}

		return staffHandler.addExperiences(idStaff, skills);
	}	
	

	/**
	 * <p>
	 * Involve a developer in a project.
	 * </p>
	 * 
	 * @param idStaff the staff identifier
	 * @param idProject the project identifier
	 * @return {@code true} if the operation is successful, {@code false} otherwiose
	 */
	@ResponseBody
	@ApiOperation(value = "Involve a developer in a project.")
	@PutMapping("/{idStaff}/project/{idProject}")
	public boolean addProject(
		@PathVariable("idStaff") int idStaff, 
		@PathVariable("idProject") int idProject) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("POST command on /api/staff/%d/project/%d", idStaff, idProject));
		}

		final Staff staff = staffHandler.getStaff(idStaff);
		
		final Project project = projectHandler.getProject(idProject);

		//
		// If the passed project is already present in the staff member's
		// project list, we send back a FALSE, to avoid duplicate entries
		//
		Predicate<Mission> predicate = pr -> (pr.getIdProject() == idProject);
		if (staff.getMissions().stream().anyMatch(predicate)) {
			return false;
		}

		staffHandler.addMission(idStaff, idProject, project.getName());
		
		return true;
	}

	/**
	 * <p>
	 * Revoke the participation of staff member from a project.
	 * </p>
	 * <p>
	 * <em>Associated HTTP verb is {@code 'delete'}</em>
	 * </p>
	 * @param idProject the given Project identifier
	 * @param idStaff the given Staff identifier
	 */
	@ResponseBody
	@ApiOperation(value = "Revoke the participation of a developer from a project.")
	@DeleteMapping("/{idStaff}/project/{idProject}")
	public boolean revokeProject(
		@PathVariable("idProject") int idProject, 
		@PathVariable("idStaff") int idStaff) throws ApplicationException, NotFoundException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("DELETE verb on /staff/%d/project/%d", idStaff, idProject));
		}

		final Staff staff = staffHandler.getStaff(idStaff);

		final Project project = projectHandler.lookup(idProject);
		if (project == null) {
			throw new NotFoundException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}

		Optional<Mission> oMission = staff.getMissions().stream().filter(pr -> (pr.getIdProject() == idProject)).findFirst();
		if (oMission.isPresent()) {
			Mission mission = oMission.get();
			if (mission.getNumberOfCommits() > 0) {
				throw new ApplicationException(
					CODE_STAFF_ACTIVE_ON_PROJECT, 
					MessageFormat.format(MESSAGE_STAFF_ACTIVE_ON_PROJECT, mission.getNumberOfCommits()));
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format("Remove project %d from staff %d", idProject, idStaff));
			}
			staffHandler.removeMission(idStaff, idProject);
		} else {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Project %d is not registered for staff %d", idProject, idStaff));
			}
		}

		return true;
	}

	@ResponseBody
	@ApiOperation(value = "Load and return the constellations, if any, registered for the given month.")
	@GetMapping("/constellation/{year}/{month}")
	public Collection<Constellation> loadConstellation(@PathVariable("year") int year, @PathVariable("month") int month) throws ApplicationException, NotFoundException {

		if (!YearMonthParser.isValid(year, month)) {
			throw new ApplicationException(
				CODE_YEAR_MONTH_INVALID,
				MessageFormat.format(MESSAGE_YEAR_MONTH_INVALID, year, month));
		}
		
		LocalDate date = LocalDate.of(year, month, 1);
		Collection<Constellation> constellations = staffHandler.loadConstellations(date);
		if (log.isDebugEnabled()) {
			log.debug(String.format("'/constellation' is returning %d skills in its constellation for %d/%d.", 0, month, year));
		}
		return constellations;
	}


}
