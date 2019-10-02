package fr.skiller.controller;

import static fr.skiller.Error.CODE_STAFF_ACTIVE_ON_PROJECT;
import static fr.skiller.Error.CODE_STAFF_NOFOUND;
import static fr.skiller.Error.MESSAGE_STAFF_ACTIVE_ON_PROJECT;
import static fr.skiller.Error.MESSAGE_STAFF_NOFOUND;
import static fr.skiller.Error.getStackTrace;
import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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

import fr.skiller.Error;
import fr.skiller.Global;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.ShuffleService;
import fr.skiller.bean.SkillHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.controller.in.BodyParamResumeSkills;
import fr.skiller.controller.in.BodyParamStaffProject;
import fr.skiller.controller.in.BodyParamStaffSkill;
import fr.skiller.data.external.BooleanDTO;
import fr.skiller.data.external.ResumeDTO;
import fr.skiller.data.external.StaffDTO;
import fr.skiller.data.internal.Experience;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.PeopleCountExperienceMap;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Resume;
import fr.skiller.data.internal.ResumeSkill;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;
import fr.skiller.service.FileType;
import fr.skiller.service.ResumeParserService;
import fr.skiller.service.StorageService;
import lombok.extern.slf4j.Slf4j;


/**
 * Controller in charge of handling the staff member of the organization.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@RestController
@RequestMapping("/staff")
public class StaffController {

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	StaffHandler staffHandler;

	@Autowired
	SkillHandler skillHandler;

	@Autowired
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
	public ResponseEntity<List<Mission>> readProjects(@PathVariable("idStaff") int idStaff) {

		try {
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
			
		} catch (final SkillerException e) {
			log.error(getStackTrace(e));
			return new ResponseEntity<>(
					new ArrayList<Mission>(), 
					new HttpHeaders(),
					HttpStatus.BAD_REQUEST);
		}
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

	@PostMapping("/save")
	public ResponseEntity<Staff> save(@RequestBody Staff input) {

		final ResponseEntity<Staff> responseEntity;

		if (log.isDebugEnabled()) {
			log.debug (String.format("Add or Update the staff.id %d", input.getIdStaff()));
			log.debug (String.format("Content %s ", input));
		}
		
		HttpHeaders headers = new HttpHeaders();
		
		if (input.getIdStaff() == -1) {
			
			// We create an already inactive user. 
			// That kind of user is supposed to be created from the ghost table form
			if ( !input.isActive() && (input.getDateInactive() == null)) {
				input.setDateInactive(Global.now());
			}
			
			staffHandler.addNewStaffMember(input);
			headers.set("backend.return_code", "1");
			responseEntity = new ResponseEntity<>(input, headers, HttpStatus.OK);
		} else {
			Staff updatedStaff = staffHandler.getStaff().get(input.getIdStaff());
			if (updatedStaff == null) {
				headers.set(BACKEND_RETURN_CODE, "1");
				headers.set(BACKEND_RETURN_MESSAGE, "There is no collaborator associated to the id " + input.getIdStaff());
				responseEntity = new ResponseEntity<>(input, headers, HttpStatus.NOT_FOUND);
			} else {
				if ((!input.isActive()) && (updatedStaff.isActive())) {
					input.setDateInactive(Global.now());
				}
				try {
					staffHandler.saveStaffMember(input);
				} catch (SkillerException e) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("Exception occurs for idStaff %d, message %s", input.getIdStaff(), e.errorMessage));
					}
					headers.set(BACKEND_RETURN_CODE, String.valueOf(e.errorCode));
					headers.set(BACKEND_RETURN_MESSAGE, e.errorMessage);
					return new ResponseEntity<>(
							new Staff(), headers,
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
				responseEntity = new ResponseEntity<>(input, headers, HttpStatus.OK);
				headers.set(BACKEND_RETURN_CODE, "1");
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("POST command on /staff/save returns the body %s", responseEntity.getBody()));
		}
		return responseEntity;
	}


	/**
	 * Add an experience to a staff member
	 * @param param
	 *            the body of the post containing an instance of {@link StaffController.ParamStaffSkill}
	 *            in JSON format
	 * @return
	 */
	@PostMapping("/experiences/add")
	public ResponseEntity<Boolean> addExperience(@RequestBody BodyParamStaffSkill param) {

		HttpHeaders headers = new HttpHeaders();
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/add with params id:%d, idSkill:%d, level:%d", 
					param.getIdStaff(), param.getIdSkill(), param.getLevel()));
		}

		final Staff staff = staffHandler.getStaff().get(param.getIdStaff());
		if (staff == null) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_STAFF_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_STAFF_NOFOUND, param.getIdStaff()));
			return new ResponseEntity<>(
					Boolean.FALSE, headers,
					HttpStatus.INTERNAL_SERVER_ERROR);			
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
	public ResponseEntity<Boolean> removeExperience(@RequestBody BodyParamStaffSkill param) {

		HttpHeaders headers = new HttpHeaders();
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/remove with params id:%d, idSkill:%d, level:%d", 
					param.getIdStaff(), param.getIdSkill(), param.getLevel()));
		}

		final Staff staff = staffHandler.getStaff().get(param.getIdStaff());
		if (staff == null) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_STAFF_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, String.format(Error.MESSAGE_STAFF_NOFOUND, param.getIdStaff()));
			return new ResponseEntity<>(
					Boolean.FALSE, headers,
					HttpStatus.INTERNAL_SERVER_ERROR);			
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
	 * @return
	 */
	@PostMapping("/experiences/update")
	public ResponseEntity<Boolean> saveExperience(@RequestBody BodyParamStaffSkill param) {
		
		HttpHeaders headers = new HttpHeaders();		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /staff/experiences/update with params id:%d, idSkill:%d, level:%d", 
					param.getIdStaff(), param.getIdSkill(), param.getLevel()));
		}

		final Staff staff = staffHandler.getStaff().get(param.getIdStaff());
		if (staff == null) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_STAFF_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, String.format(Error.MESSAGE_STAFF_NOFOUND, param.getIdStaff()));
			return new ResponseEntity<>(
					Boolean.FALSE, headers,
					HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
		Experience experience = staff.getExperience(param.getIdSkill());
		if (experience != null) {
			this.staffHandler.updateExperience(param.getIdStaff(), new Experience(param.getIdSkill(), param.getLevel()));
		}
		return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
	}
	

	@PostMapping("/api/uploadCV")
	public ResponseEntity<ResumeDTO> uploadApplicationFile(
			@RequestParam("file") MultipartFile file, 
			@RequestParam("id") int id, 
			@RequestParam("type") int type) {

		
		HttpHeaders headers = new HttpHeaders();
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());

		if (log.isDebugEnabled()) {
			log.debug(String.format("uploading %s for staff identifer %d of type %s", filename, id, type));
		}

		FileType typeOfApplication = FileType.valueOf(type);
		
		storageService.store(file);

		final Staff staff = staffHandler.getStaff().get(id);
		assert (staff != null);

		staff.updateApplication(filename, typeOfApplication);
		
		try {
			final Resume exp = resumeParserService.extract(filename, typeOfApplication);
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
		} catch (SkillerException e) {
			return new ResponseEntity<>(
					new ResumeDTO(-1, e.getMessage()), 
					headers, 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

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
		Resource resource = storageService.loadAsResource(staff.getApplication());

        // Try to determine file's content type
		final String contentType;
		FileType type = FileType.valueOf(staff.getTypeOfApplication());
		switch (type) {
		case FILE_TYPE_TXT:
			contentType = "text/html;charset=UTF-8";
			break;
		case FILE_TYPE_DOC:
			contentType ="application/msword";
			break;
		case FILE_TYPE_DOCX:
			contentType ="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			break;
		case FILE_TYPE_PDF:
			contentType ="application/pdf";
			break;
		default:
			contentType = "application/octet-stream";
			break;
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("%s %s", staff.getApplication(), contentType));
		}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}
		
	@PostMapping("/api/experiences/resume/save")
	public ResponseEntity<StaffDTO> saveExperiences(@RequestBody BodyParamResumeSkills param) {

		if (log.isDebugEnabled()) {
			log.debug(String.format("Adding %d skills for the staff ID %d", param.getSkills().length, param.getIdStaff()));
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("Adding the skills below for the staff identifier %d", param.getIdStaff()));
			Arrays.asList(param.getSkills()).stream().forEach(skill -> log.trace(String.format("%s %s", skill.getIdSkill(), skill.getTitle())));
		}
		try {
			Staff staff = staffHandler.addExperiences(param.getIdStaff(), param.getSkills());
			return new ResponseEntity<>(new StaffDTO(staff, 1, 
					staff.getFirstName() + " " + staff.getLastName() + " has " + staff.getExperiences().size() + " skills now!"), 
					HttpStatus.OK);
		} catch (final SkillerException se) {
			return new ResponseEntity<>(
					new StaffDTO(new Staff(), se.errorCode, se.errorMessage), 
					new HttpHeaders(), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

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
	public ResponseEntity<BooleanDTO> addProject(@RequestBody BodyParamStaffProject param) {

		HttpHeaders headers = new HttpHeaders();
		try {
		
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
		} catch (final SkillerException se) {
			return new ResponseEntity<>(
					new BooleanDTO(se.errorCode, se.errorMessage), 
					new HttpHeaders(), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
