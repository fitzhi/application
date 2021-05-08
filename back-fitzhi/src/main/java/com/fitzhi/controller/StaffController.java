package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_STAFF_ACTIVE_ON_PROJECT;
import static com.fitzhi.Error.CODE_STAFF_NOFOUND;
import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static com.fitzhi.Error.MESSAGE_STAFF_ACTIVE_ON_PROJECT;
import static com.fitzhi.Error.MESSAGE_STAFF_NOFOUND;
import static com.fitzhi.Error.MESSAGE_PROJECT_NOFOUND;
import static com.fitzhi.Global.BACKEND_RETURN_CODE;
import static com.fitzhi.Global.BACKEND_RETURN_MESSAGE;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.Api;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.in.BodyParamResumeSkills;
import com.fitzhi.controller.in.BodyParamStaffProject;
import com.fitzhi.controller.in.BodyParamStaffSkill;
import com.fitzhi.data.external.BooleanDTO;
import com.fitzhi.data.external.ResumeDTO;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.PeopleCountExperienceMap;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Resume;
import com.fitzhi.data.internal.ResumeSkill;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.service.FileType;
import com.fitzhi.service.ResumeParserService;
import com.fitzhi.service.StorageService;

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
	description = "API endpoints to manage the projects declared in the application."
)
public class StaffController extends BaseRestController {

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
	@PostMapping("")
	public ResponseEntity<Void> create(UriComponentsBuilder builder, @RequestBody Staff staff)
			throws ApplicationException {

		// This is not a creation.
		if (staffHandler.containsStaffMember(staff.getIdStaff())) {
			return new ResponseEntity<Void>(null, headers(), HttpStatus.CONFLICT);
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
	 * Update the staff member identified by the given {@link Staff#getIdStaff() idStaff}
	 * </p>
	 * @param idStaff the staff identifier. The staff identifier is hosted in the URL in accordance with the Rest naming conventions
	 * @param staff the staff to update. This staff is hosted inside the body of the {@code PUT} Medhod.
	 * @return an empty content for an update request
	 */
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

	@GetMapping("/countGroupByExperiences/active")
	public Map<String, Long> countActive() {
		
		final PeopleCountExperienceMap peopleCountExperienceMap = staffHandler.countAllStaffGroupBySkillLevel(true);

		if (log.isDebugEnabled()) {
			log.debug(String.format("'/countGroupBySkills/active' is returning %d experiences", peopleCountExperienceMap.getData().size()));
		}
		
		return peopleCountExperienceMap.getData();
	}

	@GetMapping("/countGroupByExperiences/all")
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
	@GetMapping(value = "/{idStaff}")
	public ResponseEntity<Staff> read(@PathVariable("idStaff") int idStaff) {

		final ResponseEntity<Staff> responseEntity;

		
		HttpHeaders headers = new HttpHeaders();
		
		Staff searchCollab = staffHandler.getStaff().get(idStaff);
		if (searchCollab != null) {
			responseEntity = new ResponseEntity<>(searchCollab, headers, HttpStatus.OK);
			if (log.isDebugEnabled()) {
				Staff staff = responseEntity.getBody();
				log.debug(String.format(
						"looking for id %d in the Staff collection returns %s %s",
						idStaff, 
						(staff != null) ? staff.getFirstName() : "null", 
						(staff != null) ? staff.getLastName()  : "null"));
			}
		} else {
			headers.set(BACKEND_RETURN_CODE, "O");
			headers.set(BACKEND_RETURN_MESSAGE, "There is no collaborator associated to the id " + idStaff);
			responseEntity = new ResponseEntity<>(new Staff(), headers, HttpStatus.NOT_FOUND);
			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"Cannot find a staff member for id %d in the Staff collection",
						idStaff));
			}
		}
		return responseEntity;
	}

	/**
	 * @param idStaff
	 *            staff member's identifier
	 * @return the list of projects where the staff member is involved
	 */
	@GetMapping(value = "/projects/{idStaff}")
	public ResponseEntity<List<Mission>> readProjects(@PathVariable("idStaff") int idStaff) throws ApplicationException {

		ResponseEntity<Staff> responseEntityStaffMember = read(idStaff);
		final Staff staff = responseEntityStaffMember.getBody();
		if (staff == null) {
			throw new ApplicationException(
				CODE_STAFF_NOFOUND, 
				MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}

		// Adding the name of project.
		for (Mission mission : staff.getMissions()) {
				mission.setName(projectHandler.get(mission.getIdProject()).getName());
		}
		
		ResponseEntity<List<Mission>> re = new ResponseEntity<>(
				staff.getMissions(), 
				responseEntityStaffMember.getHeaders(),
				responseEntityStaffMember.getStatusCode());
		return re;
			
	}

	/**
	 * @param idStaff
	 *            staff member's identifier
	 * @return the given developer's experience as list of skills.
	 */
	@GetMapping(value = "/experiences/{idStaff}")
	public ResponseEntity<List<Experience>> readExperiences(@PathVariable("idStaff") int idStaff) {

		ResponseEntity<Staff> responseEntityStaffMember = read(idStaff);
		final Staff staff = responseEntityStaffMember.getBody();
		if (staff == null) {
			throw new RuntimeException("getBody() Should not be null");
		}

		return new ResponseEntity<>(
				staff.getExperiences(), 
				responseEntityStaffMember.getHeaders(),
				responseEntityStaffMember.getStatusCode());
	}

	/**
	 * Switch the 'active' status of a developer<br/>
	 * <ul>
	 * <li>
	 * If the given developer is active, it will become inactive.
	 * </li>
	 * <li>
	 * If inactive, it will be switched to active.
	 * </li>
	 * </ul>
	 * @param idStaff the identifier of the staff member to activate, or deactivate.
	 * @return {@code true} ALWAYS. Either the application return {@code true}, or an exception is thrown.
	 * @throws ApplicationException thrown if any exception occurs during the treatment, most probably if there is no staff member for the given id.
	 * @see #processActiveStatus(int)
	 */
	@GetMapping("/forceActiveStatus/{idStaff}")
	public ResponseEntity<Boolean> switchActiveState(@PathVariable("idStaff") int idStaff) throws ApplicationException {
		
		final Staff staff = staffHandler.getStaff(idStaff);
		if (staff == null) {
			throw new ApplicationException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}
	
		this.staffHandler.forceActiveStatus(staff);
		
		return new ResponseEntity<>(true, new HttpHeaders(), HttpStatus.OK);
	}

	/**
	 * <p>
	 * Update the active status of a developer.
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
	@GetMapping("/processActiveStatus/{idStaff}")
	public ResponseEntity<Staff> processActiveStatus(@PathVariable("idStaff") int idStaff) throws ApplicationException {
		
		final Staff staff = staffHandler.getStaff(idStaff);
		if (staff == null) {
			throw new ApplicationException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}
		
		// We process the active status, therefore we property forceActiveStatus should be set to False.
		staff.setForceActiveState(false);
		
		this.staffHandler.processActiveStatus(staff);
		
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<>(staff, headers, HttpStatus.OK);
	}

	
	/**
	 * Delete the staff member corresponding to the identifier.
	 * @param idStaff the Staff member identifier candidate for deletion
	 * @return an empty HTTP response after the deletion.
	 */
	@DeleteMapping(value = "/{idStaff}")
	public ResponseEntity<Object> removeStaff(@PathVariable("idStaff") int idStaff) throws NotFoundException, ApplicationException {
		Staff staff = staffHandler.getStaff(idStaff);
		if (staff == null) {
			throw new NotFoundException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}
		
		staffHandler.removeStaff(idStaff);

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	/**
	 * We do not allow to remove all staff members
	 * @return an empty HTTP Response because this method is not allowed.
	 */
	@DeleteMapping()
	public ResponseEntity<Object> removeAllStaff() throws ApplicationException {		
		return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * <p>
	 * Add an {@link Experience experience} to a staff member.
	 * </p>
	 * @param param
	 *            the body of the post containing an instance of {@link StaffController.ParamStaffSkill}
	 *            in JSON format
	 * @return
	 */
	@PostMapping("{idStaff}/experience")
	public ResponseEntity<Boolean> updateExperience(
		@PathVariable("idStaff") int idStaff,
		@RequestBody Experience experience) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/add with params id:%d, idSkill:%d, level:%d", 
					idStaff, experience.getId(), experience.getLevel()));
		}

		final Staff staff = staffHandler.getStaff(idStaff);
		if (staff == null) {
			throw new ApplicationException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}
		
		Experience formerExperience = staff.getExperience(experience.getId());
		if (formerExperience == null) {
			this.staffHandler.addExperience(idStaff, new Experience(experience.getId(), experience.getLevel()));
		} else {
			this.staffHandler.removeExperience(idStaff, formerExperience);
			this.staffHandler.addExperience(idStaff, new Experience(experience.getId(), experience.getLevel()));
		}
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);
	}
	
	/**
	 * Add an experience to a staff member
	 * @param param
	 *            the body of the post containing an instance of {@link StaffController.ParamStaffSkill}
	 *            in JSON format
	 * @return
	 */
	@DeleteMapping(value = "{idStaff}/experience/{idSkill}")
	public ResponseEntity<Boolean> removeExperience(
		@PathVariable("idStaff") int idStaff,
		@PathVariable("idSkill") int idSkill) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/remove with params idStaff:%d, idSkill:%d", 
					idStaff, idSkill));
		}

		final Staff staff = staffHandler.getStaff(idStaff);
		if (staff == null) {
			throw new ApplicationException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		} 
		
		Experience experience = staff.getExperience(idSkill);
		if (experience != null) {
			this.staffHandler.removeExperience(idStaff, experience);
		}
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);		
	}
	
	/**
	 * Upload the application of a staff member on a server.
	 * @param file the application
	 * @param id the staff identifier
	 * @param type the type of file (WORD, PDF...)
	 * @throws ApplicationException thrown if any problem occurs
	 * @return the resume parsed from the uploaded file.
	 */
	@PostMapping("/api/uploadCV")
	public ResponseEntity<ResumeDTO> uploadApplicationFile(
			@RequestParam("file") MultipartFile file, 
			@RequestParam("id") int id, 
			@RequestParam("type") int type) throws ApplicationException {

		
		HttpHeaders headers = new HttpHeaders();
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());

		if (log.isDebugEnabled()) {
			log.debug(String.format("Uploading %s for staff identifer %d of type %s", filename, id, type));
		}

		FileType typeOfApplication = FileType.valueOf(type);
		
		final Staff staff = staffHandler.getStaff().get(id);
		assert (staff != null);

		storageService.store(file, buildFileName(staff, filename));

		staff.updateApplication(filename, typeOfApplication);
		
		final Resume exp = resumeParserService.extract(buildFileName(staff, filename), typeOfApplication);
		ResumeDTO resumeDTO = new ResumeDTO();
		exp.data().forEach(item -> resumeDTO.experience.add(
				new ResumeSkill(item.getIdSkill(), 
								skillHandler.getSkills().get(item.getIdSkill()).getTitle(), 
								item.getCount())));
		/**
		 * We put the most often repeated keywords at the beginning of the list.
		 */
		Collections.sort(resumeDTO.experience);
		return new ResponseEntity<>(resumeDTO, headers, HttpStatus.OK);
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
		
	@PostMapping("/api/experiences/resume/save")
	public ResponseEntity<Staff> saveExperiences(@RequestBody BodyParamResumeSkills param) 
		throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("Adding %d skills for the staff ID %d", 
				param.getSkills().length, param.getIdStaff()));
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("Adding the skills below for the staff identifier %d", param.getIdStaff()));
			Arrays.asList(param.getSkills()).stream().forEach(skill -> log.trace(String.format("%s %s", skill.getIdSkill(), skill.getTitle())));
		}

		Staff staff = staffHandler.addExperiences(param.getIdStaff(), param.getSkills());
		return new ResponseEntity<>(staff, headers(), HttpStatus.OK);

	}	
	
	/**
	 * <p>Add a project assigned to a developer.</p>
	 * 
	 * @param param
	 *            the body of the post containing an instance of
	 *            ParamStaffProject in JSON format
	 * @return
	 */
	@PostMapping("/project/add")
	public ResponseEntity<BooleanDTO> addProject(@RequestBody BodyParamStaffProject param) throws ApplicationException {

		HttpHeaders headers = new HttpHeaders();
		
		if (log.isDebugEnabled()) {
			log.debug(
					String.format("POST command on /staff/project/add with params idStaff: %d, idProject: %d", 
							param.getIdStaff(), param.getIdProject()));
		}
		final ResponseEntity<BooleanDTO> responseEntity;

		final Staff staff = staffHandler.getStaff().get(param.getIdStaff());
		assert (staff != null);
		
		final Project project = projectHandler.get(param.getIdProject());
		assert (project != null);

		/*
		 * If the passed project is already present in the staff member's
		 * project list, we send back a BAD_REQUEST to avoid duplicate
		 * entries
		 */
		Predicate<Mission> predicate = pr -> (pr.getIdProject() == param.getIdProject());
		if (staff.getMissions().stream().anyMatch(predicate)) {
			responseEntity = new ResponseEntity<>(
					new BooleanDTO(-1, "The collaborator " + staff.fullName() + " is already involved in " + project.getName()),
					headers, HttpStatus.INTERNAL_SERVER_ERROR);
			return responseEntity;
		}

		staffHandler.addMission(param.getIdStaff(), param.getIdProject(), project.getName());
		responseEntity = new ResponseEntity<>(new BooleanDTO(), headers, HttpStatus.OK);
		
		return responseEntity;
	}

	/**
	 * <p>
	 * Revoke the participation of staff member from a project.
	 * </p>
	 * <p>
	 * <i>Associated HTTP verb is {@code 'delete'}
	 * </p>
	 * @param idProject the given Project identifier
	 * @param idStaff the given Staff identifier
	 */
	@DeleteMapping("/{idStaff}/project/{idProject}")
	public ResponseEntity<Boolean> revokeProject(
		@PathVariable("idProject") int idProject, 
		@PathVariable("idStaff") int idStaff) throws ApplicationException, NotFoundException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("DELETE verb on /staff/%d/project/%d", idStaff, idProject));
		}

		final Staff staff = staffHandler.getStaff(idStaff);
		if (staff == null) {
			throw new NotFoundException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}

		final Project project = projectHandler.get(idProject);
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

		return new ResponseEntity<>(Boolean.TRUE, new HttpHeaders(), HttpStatus.OK);
	}
}
