package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_STAFF_ACTIVE_ON_PROJECT;
import static com.fitzhi.Error.CODE_STAFF_NOFOUND;
import static com.fitzhi.Error.MESSAGE_STAFF_ACTIVE_ON_PROJECT;
import static com.fitzhi.Error.MESSAGE_STAFF_NOFOUND;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fitzhi.Error;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.in.BodyParamResumeSkills;
import com.fitzhi.controller.in.BodyParamStaffProject;
import com.fitzhi.controller.in.BodyParamStaffSkill;
import com.fitzhi.data.external.BooleanDTO;
import com.fitzhi.data.external.ResumeDTO;
import com.fitzhi.data.external.StaffDTO;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.PeopleCountExperienceMap;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Resume;
import com.fitzhi.data.internal.ResumeSkill;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.SkillerException;
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

	@GetMapping("/all")
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
						idStaff, staff.getFirstName(), staff.getLastName()));
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
	public ResponseEntity<List<Mission>> readProjects(@PathVariable("idStaff") int idStaff) throws SkillerException {

		ResponseEntity<Staff> responseEntityStaffMember = read(idStaff);

		// Adding the name of project.
		for (Mission mission : responseEntityStaffMember.getBody().getMissions()) {
				mission.setName(projectHandler.get(mission.getIdProject()).getName());
		}
		
		ResponseEntity<List<Mission>> re = new ResponseEntity<>(
				responseEntityStaffMember.getBody().getMissions(), 
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

		return new ResponseEntity<>(
				responseEntityStaffMember.getBody().getExperiences(), 
				responseEntityStaffMember.getHeaders(),
				responseEntityStaffMember.getStatusCode());
	}

	/**
	 * <b>Switch</b> the active status of a developer<br/>
	 * <ul>
	 * <li>
	 * If the developer is active, it will become inactive.
	 * </li>
	 * <li>
	 * If inactive, it will be switched to active.
	 * </li>
	 * </ul>
	 * @param idStaff the identifier of the staff member to activate, or deactivate.
	 * @return {@code true} ALWAYS. Either the application return {@code true}, or an exception is thrown.
	 * @throws SkillerException thrown if any exception occurs during the treatment, most probably if there is no staff member for the given id.
	 */
	@GetMapping("/switchActiveState/{idStaff}")
	public ResponseEntity<Boolean> switchActiveState(@PathVariable("idStaff") int idStaff) throws SkillerException {
		
		final Staff staff = staffHandler.getStaff(idStaff);
		if (staff == null) {
			throw new SkillerException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}
	
		this.staffHandler.forceSwitchActiveState(staff);
		
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<>(true, headers, HttpStatus.OK);
	}
	
	@PostMapping("/save")
	public ResponseEntity<Staff> save(@RequestBody Staff input) throws SkillerException {

		if (log.isDebugEnabled()) {
			log.debug (String.format("Add or Update the staff.id %d", input.getIdStaff()));
			log.debug (String.format("Content %s ", input));
		}
		
		HttpHeaders headers = new HttpHeaders();
		
		if (input.getIdStaff() == -1) {
			
			// We create an already inactive user. 
			// That kind of user is supposed to be created from the ghost table form
			if ( !input.isActive() && (input.getDateInactive() == null)) {
				input.setDateInactive(LocalDate.now());
			}
			staffHandler.addNewStaffMember(input);
			headers.set("backend.return_code", "1");
			return new ResponseEntity<>(input, headers, HttpStatus.OK);
		} 
		
		if (!staffHandler.hasStaff(input.getIdStaff())) {
			throw new SkillerException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, input.getIdStaff()));
		} 
			
		staffHandler.saveStaffMember(input);
		
		headers.set(BACKEND_RETURN_CODE, "1");
		return new ResponseEntity<>(input, headers, HttpStatus.OK);
	}


	/**
	 * Add an experience to a staff member
	 * @param param
	 *            the body of the post containing an instance of {@link StaffController.ParamStaffSkill}
	 *            in JSON format
	 * @return
	 */
	@PostMapping("/experiences/add")
	public ResponseEntity<Boolean> addExperience(@RequestBody BodyParamStaffSkill param) throws SkillerException {

		HttpHeaders headers = new HttpHeaders();
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/add with params id:%d, idSkill:%d, level:%d", 
					param.getIdStaff(), param.getIdSkill(), param.getLevel()));
		}

		final Staff staff = staffHandler.getStaff(param.getIdStaff());
		if (staff == null) {
			throw new SkillerException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, param.getIdStaff()));
		}
		
		
		Experience experience = staff.getExperience(param.getIdSkill());
		if (experience == null) {
			this.staffHandler.addExperience(param.getIdStaff(), new Experience(param.getIdSkill(), param.getLevel()));
		} else {
			this.staffHandler.removeExperience(param.getIdStaff(), experience);
			this.staffHandler.addExperience(param.getIdStaff(), new Experience(param.getIdSkill(), param.getLevel()));
		}
		return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		
	}
	
	/**
	 * Add an experience to a staff member
	 * @param param
	 *            the body of the post containing an instance of {@link StaffController.ParamStaffSkill}
	 *            in JSON format
	 * @return
	 */
	@PostMapping("/experiences/remove")
	public ResponseEntity<Boolean> removeExperience(@RequestBody BodyParamStaffSkill param) throws SkillerException {

		HttpHeaders headers = new HttpHeaders();
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/remove with params id:%d, idSkill:%d, level:%d", 
					param.getIdStaff(), param.getIdSkill(), param.getLevel()));
		}

		final Staff staff = staffHandler.getStaff(param.getIdStaff());
		if (staff == null) {
			throw new SkillerException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, param.getIdStaff()));
		} 
		
		Experience experience = staff.getExperience(param.getIdSkill());
		if (experience != null) {
			this.staffHandler.removeExperience(param.getIdStaff(), experience);
		}
		return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		
	}
	
	/**
	 * Adding or changing the level of an experience assign to a developer.
	 * 
	 * @param param
	 *            the body of the post containing an instance of ParamStaffSkill
	 *            in JSON format
	 * @see StaffController.ParamStaffSkill
	 * @throws SkillerException thrown if any problem occurs
	 * @return
	 */
	@PostMapping("/experiences/update")
	public ResponseEntity<Boolean> saveExperience(@RequestBody BodyParamStaffSkill param) throws SkillerException {
		
		HttpHeaders headers = new HttpHeaders();		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/update with params id:%d, idSkill:%d, level:%d", 
					param.getIdStaff(), param.getIdSkill(), param.getLevel()));
		}

		final Staff staff = staffHandler.getStaff().get(param.getIdStaff());
		if (staff == null) {
			throw new SkillerException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, param.getIdStaff()));
		} 
		
		
		Experience experience = staff.getExperience(param.getIdSkill());
		if (experience != null) {
			this.staffHandler.updateExperience(param.getIdStaff(), new Experience(param.getIdSkill(), param.getLevel()));
		}
		return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
	}
	
	/**
	 * Upload the application of a staff member on a server.
	 * @param file the application
	 * @param id the staff identifier
	 * @param type the type of file (WORD, PDF...)
	 * @throws SkillerException thrown if any problem occurs
	 * @return the resume parsed from the uploaded file.
	 */
	@PostMapping("/api/uploadCV")
	public ResponseEntity<ResumeDTO> uploadApplicationFile(
			@RequestParam("file") MultipartFile file, 
			@RequestParam("id") int id, 
			@RequestParam("type") int type) throws SkillerException {

		
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
	public ResponseEntity<StaffDTO> saveExperiences(@RequestBody BodyParamResumeSkills param) throws SkillerException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("Adding %d skills for the staff ID %d", param.getSkills().length, param.getIdStaff()));
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("Adding the skills below for the staff identifier %d", param.getIdStaff()));
			Arrays.asList(param.getSkills()).stream().forEach(skill -> log.trace(String.format("%s %s", skill.getIdSkill(), skill.getTitle())));
		}

		Staff staff = staffHandler.addExperiences(param.getIdStaff(), param.getSkills());
		return new ResponseEntity<>(new StaffDTO(staff, 1, 
				staff.getFirstName() + " " + staff.getLastName() + " has " + staff.getExperiences().size() + " skills now!"), 
				HttpStatus.OK);

	}	
	
	/**
	 * Add a project assigned to a developer.
	 * 
	 * @param param
	 *            the body of the post containing an instance of
	 *            ParamStaffProject in JSON format
	 * @return
	 */
	@PostMapping("/project/add")
	public ResponseEntity<BooleanDTO> addProject(@RequestBody BodyParamStaffProject param) throws SkillerException {

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
	 * Revoke the participation of staff member into a project.
	 */
	@PostMapping("/project/del")
	public ResponseEntity<BooleanDTO> revokeProject(@RequestBody BodyParamStaffProject param) {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /staff/project/del with params idStaff : %d,idProject : %d", 
				param.getIdStaff(), param.getIdProject()));
		}

		final Staff staff = staffHandler.getStaff().get(param.getIdStaff());
		if (staff == null) {
			return new ResponseEntity<>(
					new BooleanDTO(CODE_STAFF_NOFOUND, 
					MessageFormat.format(MESSAGE_STAFF_NOFOUND, param.getIdStaff())), 
					new HttpHeaders(), 
					HttpStatus.NOT_FOUND);
		}

		Optional<Mission> oProject = staff.getMissions().stream().filter(pr -> (pr.getIdProject() == param.getIdProject())).findFirst();
		if (oProject.isPresent()) {
			
			Mission mission = oProject.get();
			if (mission.getNumberOfCommits() > 0) {
				return new ResponseEntity<>(
						new BooleanDTO(CODE_STAFF_ACTIVE_ON_PROJECT, 
						MessageFormat.format(MESSAGE_STAFF_ACTIVE_ON_PROJECT, 
								mission.getNumberOfCommits())), 
						new HttpHeaders(), 
						HttpStatus.NOT_FOUND);
			}
			staff.getMissions().remove(oProject.get());
		}

		return new ResponseEntity<>(new BooleanDTO(), new HttpHeaders(), HttpStatus.OK);
	}
	
	ResponseEntity<StaffDTO> postErrorReturnBodyMessage(int code, String message) {
		return postErrorReturnBodyMessage(code, message, new Staff());
	}

	ResponseEntity<StaffDTO> postErrorReturnBodyMessage(int code, String message, Staff staffMember) {
		return new ResponseEntity<>(new StaffDTO(staffMember, code, message), new HttpHeaders(),
				HttpStatus.BAD_REQUEST);
	}
}
