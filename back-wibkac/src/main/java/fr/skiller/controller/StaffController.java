package fr.skiller.controller;

import static fr.skiller.Error.getStackTrace;
import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.Global;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.ShuffleService;
import fr.skiller.bean.SkillHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.ResumeDTO;
import fr.skiller.data.external.StaffDTO;
import fr.skiller.data.internal.Experience;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.PeopleCountExperienceMap;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Resume;
import fr.skiller.data.internal.ResumeSkill;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;
import fr.skiller.service.FileType;
import fr.skiller.service.ResumeParserService;
import fr.skiller.service.StorageService;


/**
 * Controller in charge of handling the staff member of the organization.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RestController
@RequestMapping("/staff")
public class StaffController {

	private final Logger logger = LoggerFactory.getLogger(StaffController.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

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
	public String readAll() {
		
		final Collection<Staff> staffTeam = staffHandler.getStaff().values();
		
		if (shuffleService.isShuffleMode()) {
			if (logger.isInfoEnabled()) {
				logger.info("The projects collection has been shuffled");
			}
			staffTeam.stream().forEach(staff -> {
				staff.setFirstName(shuffleService.shuffle(staff.getFirstName()));
				staff.setLastName(shuffleService.shuffle(staff.getLastName()));
				staff.getMissions().stream().forEach(mission -> mission.setName(shuffleService.shuffle(mission.getName())));
			});
		}
		return gson.toJson(staffTeam);
	}

	@GetMapping("/countGroupByExperiences/active")
	public String countActive() {
		final PeopleCountExperienceMap peopleCountExperienceMap = staffHandler.countAllStaffGroupBySkillLevel(true);

		final String resultContent = gson.toJson(peopleCountExperienceMap.getData());
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("'/countGroupBySkills/active' is returning %s", resultContent));
		}
		return resultContent;
	}

	@GetMapping("/countGroupByExperiences/all")
	public String countAll() {
		final PeopleCountExperienceMap peopleCountExperienceMap = staffHandler.countAllStaffGroupBySkillLevel(false);

		final String resultContent = gson.toJson(peopleCountExperienceMap.getData());
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("'/countGroupBySkills/all' is returning %s", resultContent));
		}
		return resultContent;
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
			if (logger.isDebugEnabled()) {
				Staff staff = responseEntity.getBody();
				logger.debug(String.format(
						"looking for id %d in the Staff collection returns %s %s",
						idStaff, staff.getFirstName(), staff.getLastName()));
			}
		} else {
			headers.set(BACKEND_RETURN_CODE, "O");
			headers.set(BACKEND_RETURN_MESSAGE, "There is no collaborator associated to the id " + idStaff);
			responseEntity = new ResponseEntity<>(new Staff(), headers, HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format(
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
			logger.error(getStackTrace(e));
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

		if (logger.isDebugEnabled()) {
			logger.debug (String.format("Add or Update the staff.id %d", input.getIdStaff()));
			logger.debug (String.format("Content %s ", input));
		}
		
		HttpHeaders headers = new HttpHeaders();
		
		if (input.getIdStaff() == -1) {
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
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Exception occurs for idStaff %d, message %s", input.getIdStaff(), e.errorMessage));
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
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("POST command on /staff/save returns the body %s", responseEntity.getBody()));
		}
		return responseEntity;
	}

	/**
	 * Internal Parameters class containing all possible parameters necessaries
	 * for add/remove a skill from a staff member.
	 * 
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	class ParamStaffSkill {
		int idStaff;
		int idSkill;
		int level;
		String formerSkillTitle;
		String newSkillTitle;

		@Override
		public String toString() {
			return "ParamSkillProject [idStaff=" + idStaff + ", idSkill=" + idSkill + ", level=" + level
					+ ", formerSkillTitle=" + formerSkillTitle + ", newSkillTitle=" + newSkillTitle + "]";
		}
	}

	/**
	 * Adding or changing the name of an experience assign to a developer.
	 * 
	 * @param param
	 *            the body of the post containing an instance of ParamStaffSkill
	 *            in JSON format
	 * @see StaffController.ParamStaffSkill
	 * @return
	 */
	@PostMapping("/experiences/save")
	public ResponseEntity<StaffDTO> saveExperience(@RequestBody String param) {

		
		HttpHeaders headers = new HttpHeaders();
		
		ParamStaffSkill p = gson.fromJson(param, ParamStaffSkill.class);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"POST command on /staff/skill/save with params id:%d ,skillName:%s, level %s", 
					p.idStaff, p.newSkillTitle, p.level));
		}
		final ResponseEntity<StaffDTO> responseEntity;

		final Staff staff = staffHandler.getStaff().get(p.idStaff);
		assert (staff != null);

		Optional<Skill> result;

		result = skillHandler.lookup(p.newSkillTitle);
		if (result.isPresent()) {

			/**
			 * If the user change the title of the skill, 1) we create a new
			 * entry into the projects list of the staff member 2) we remove the
			 * former entry assigned to the previous title.
			 * 
			 * Below, is the code for REMOVE.
			 */
			if ((p.formerSkillTitle != null) && (p.formerSkillTitle.length() > 0)
					&& (!p.formerSkillTitle.equals(p.newSkillTitle))) {
				Optional<Skill> formerSkill = skillHandler.lookup(p.formerSkillTitle);
				if (formerSkill.isPresent()) {
					final Experience formerExperienceToRemove = staff.getExperience(formerSkill.get().getId());
					staff.getExperiences().remove(formerExperienceToRemove);
				}
			}

			/*
			 * If the passed skill is already present in the staff member's
			 * skill list, we update the level if necessary, or we send back an
			 * warning. No Update made.
			 */
			final Experience asset = staff.getExperience(result.get().getId());
			if (asset != null) {
				if (asset.getLevel() != p.level) {
					asset.setLevel(p.level);
					return new ResponseEntity<>(new StaffDTO(staff), HttpStatus.OK);
				}
				return postErrorReturnBodyMessage(HttpStatus.BAD_REQUEST.value(), "The collaborator " + staff.fullName()
						+ " has already this level (" + p.level + ") of skill for " + p.newSkillTitle, staff);
			}

			/*
			 * We create a NEW asset for this staff member.
			 */
			staff.getExperiences().add(new Experience(result.get().getId(), p.level));
			responseEntity = new ResponseEntity<>(new StaffDTO(staff), headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Returning  staff %s", gson.toJson(staff)));
			}

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Cannot find a skill with the name %s", p.newSkillTitle));
			}
			return postErrorReturnBodyMessage(404, "There is no skill with the name " + p.newSkillTitle, staff);
		}
		return responseEntity;
	}

	/**
	 * Internal Parameters class containing all possible parameters necessaries
	 * for add/remove a project from a staff member.
	 * 
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	class ParamStaffProject {
		int idStaff;
		int idProject;
		String formerProjectName;
		String newProjectName;

		@Override
		public String toString() {
			return "ParamStaffProject [idStaff=" + idStaff + ", idProject=" + idProject + ", formerProjectName="
					+ formerProjectName + ", newProjectName=" + newProjectName + "]";
		}
	}

	/**
	 * Revoke an experience for a staff member.
	 */
	@PostMapping("/experiences/del")
	public ResponseEntity<StaffDTO> revokeSkill(@RequestBody String param) {
		ParamStaffSkill p = gson.fromJson(param, ParamStaffSkill.class);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"POST command on /staff/experiences/del with params idStaff:%d, idSkill:%d",
					p.idStaff, p.idSkill));
		}

		final Staff staff = staffHandler.getStaff().get(p.idStaff);
		if (staff == null) {
			return postErrorReturnBodyMessage(404, "There is no staff member for id" + p.idStaff);
		}

		Optional<Experience> oExperience = staff.getExperiences().stream().filter(exp -> (exp.getId() == p.idSkill)).findFirst();
		if (oExperience.isPresent()) {
			staff.getExperiences().remove(oExperience.get());
		}

		return new ResponseEntity<>(new StaffDTO(staff), new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping("/api/uploadCV")
	public ResponseEntity<ResumeDTO> uploadApplicationFile(
			@RequestParam("file") MultipartFile file, 
			@RequestParam("id") int id, 
			@RequestParam("type") int type) {

		
		HttpHeaders headers = new HttpHeaders();
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("uploading %s for staff identifer %d of type %s", filename, id, type));
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
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("No application file for %d", staff.getIdStaff()));
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
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s %s", staff.getApplication(), contentType));
		}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}
	
	class ResumeSkills {
		int idStaff;
		ResumeSkill[] skills;
	}
	
	@PostMapping("/api/experiences/resume/save")
	public ResponseEntity<StaffDTO> saveExperiences(@RequestBody String body) {

		ResumeSkills p = gson.fromJson(body, ResumeSkills.class);
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding %d skills for the staff ID %d", p.skills.length, p.idStaff));
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Adding the skills below for the staff identifier %d", p.idStaff));
			Arrays.asList(p.skills).stream().forEach(skill -> logger.trace(String.format("%s %s", skill.getIdSkill(), skill.getTitle())));
		}
		try {
			Staff staff = staffHandler.addExperiences(p.idStaff, p.skills);
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
	 * Add or change the name of a project assigned to a developer.
	 * 
	 * @param param
	 *            the body of the post containing an instance of
	 *            ParamStaffProject in JSON format
	 * @return
	 */
	@PostMapping("/project/save")
	public ResponseEntity<StaffDTO> saveProject(@RequestBody String param) {

		
		HttpHeaders headers = new HttpHeaders();
		
		try {
			ParamStaffProject p = gson.fromJson(param, ParamStaffProject.class);
			if (logger.isDebugEnabled()) {
				logger.debug(
						String.format("POST command on /staff/project/save with params id:%d, projectName:%s", 
								p.idStaff, p.newProjectName));
			}
			final ResponseEntity<StaffDTO> responseEntity;
	
			final Staff staff = staffHandler.getStaff().get(p.idStaff);
			assert (staff != null);
	
			Optional<Project> result = projectHandler.lookup(p.newProjectName);
			
			if (result.isPresent()) {
	
				/*
				 * If the passed project is already present in the staff member's
				 * project list, we send back a BAD_REQUEST to avoid duplicate
				 * entries
				 */
				Predicate<Mission> predicate = pr -> (pr.getIdProject() == result.get().getId());
				if (staff.getMissions().stream().anyMatch(predicate)) {
					responseEntity = new ResponseEntity<>(
							new StaffDTO(staff, 0,
									"The collaborator " + staff.fullName() + " is already involved in " + p.newProjectName),
							headers, HttpStatus.BAD_REQUEST);
					return responseEntity;
				}
	
				/**
				 * If the user change the name of the project, 1) we create a new
				 * entry into the projects list of the staff member 2) we remove the
				 * former entry of the previous name
				 */
				if ((p.formerProjectName != null) && (p.formerProjectName.length() > 0)) {
					Optional<Project> formerProject = projectHandler.lookup(p.formerProjectName);
					Optional<Mission> optMission = staff.getMissions()
						.stream()
						.filter(mission -> mission.getIdProject() == formerProject.get().getId())
						.findFirst();
					if (optMission.isPresent()) {
						staff.getMissions().remove(optMission.get());
					}
				}
	
				staff.getMissions().add(
						new Mission(
								staff.getIdStaff(), 
								result.get().getId(), 
								projectHandler.get(result.get().getId()).getName()));
				responseEntity = new ResponseEntity<>(new StaffDTO(staff), headers, HttpStatus.OK);
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("returning  staff %s", gson.toJson(staff)));
				}
	
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Cannot find a Project for the name %s", p.newProjectName));
				}
				return postErrorReturnBodyMessage(404, "There is no project with the name " + p.newProjectName, staff);
			}
			return responseEntity;
		} catch (SkillerException e) {
			logger.error(getStackTrace(e));
			return new ResponseEntity<>(
					new StaffDTO(new Staff(), e.errorCode,e.errorMessage), new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
		
	}

	/**
	 * Revoke the participation of staff member in a project.
	 */
	@PostMapping("/project/del")
	public ResponseEntity<StaffDTO> revokeProject(@RequestBody String param) {

		ParamStaffProject p = gson.fromJson(param, ParamStaffProject.class);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"POST command on /staff/project/del with params idStaff : %d,idProject : %d", 
					p.idStaff, p.idProject));
		}

		final Staff staff = staffHandler.getStaff().get(p.idStaff);
		if (staff == null) {
			return postErrorReturnBodyMessage(404, "There is no staff member for id" + p.idStaff);
		}

		Optional<Mission> oProject = staff.getMissions().stream().filter(pr -> (pr.getIdProject() == p.idProject)).findFirst();
		if (oProject.isPresent()) {
			staff.getMissions().remove(oProject.get());
		}

		return new ResponseEntity<>(new StaffDTO(staff), new HttpHeaders(), HttpStatus.OK);
	}
	
	ResponseEntity<StaffDTO> postErrorReturnBodyMessage(int code, String message) {
		return postErrorReturnBodyMessage(code, message, new Staff());
	}

	ResponseEntity<StaffDTO> postErrorReturnBodyMessage(int code, String message, Staff staffMember) {
		return new ResponseEntity<>(new StaffDTO(staffMember, code, message), new HttpHeaders(),
				HttpStatus.BAD_REQUEST);
	}
}
