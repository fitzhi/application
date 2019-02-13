package fr.skiller.controler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.webresources.EmptyResource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.Global;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.SkillHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.PseudoListDTO;
import fr.skiller.data.external.ResumeDTO;
import fr.skiller.data.external.StaffDTO;
import fr.skiller.data.internal.PeopleCountExperienceMap;
import fr.skiller.data.internal.Resume;
import fr.skiller.data.internal.ResumeSkill;
import fr.skiller.data.internal.Experience;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.internal.Mission;
import fr.skiller.exception.SkillerException;
import fr.skiller.service.ResumeParserService;
import fr.skiller.service.StorageService;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import static fr.skiller.Error.getStackTrace;

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

	@GetMapping("/all")
	String readAll() {
		return gson.toJson(staffHandler.getStaff().values());
	}

	@GetMapping("/countGroupByExperiences/active")
	String countActive() {
		final PeopleCountExperienceMap peopleCountExperienceMap = staffHandler.countAllStaff_GroupBy_Skill_Level(true);

		final String resultContent = gson.toJson(peopleCountExperienceMap.data);
		if (logger.isDebugEnabled()) {
			logger.debug("'/countGroupBySkills/active' is returning " + resultContent);
		}
		return resultContent;
	}

	@GetMapping("/countGroupByExperiences/all")
	String countAll() {
		final PeopleCountExperienceMap peopleCountExperienceMap = staffHandler.countAllStaff_GroupBy_Skill_Level(false);

		final String resultContent = gson.toJson(peopleCountExperienceMap.data);
		if (logger.isDebugEnabled()) {
			logger.debug("'/countGroupBySkills/all' is returning " + resultContent);
		}
		return resultContent;
	}

	/**
	 * @param idStaff
	 *            staff member's identifier
	 * @return the staff member identified by its id
	 */
	@RequestMapping(value = "/{idStaff}", method = RequestMethod.GET)
	ResponseEntity<Staff> read(@PathVariable("idStaff") int idStaff) {

		final ResponseEntity<Staff> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		Staff searchCollab = staffHandler.getStaff().get(idStaff);
		if (searchCollab != null) {
			responseEntity = new ResponseEntity<Staff>(searchCollab, headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				Staff staff = responseEntity.getBody();
				logger.debug("looking for id " + String.valueOf(idStaff) + " in the Staff collection returns " + staff.firstName + " " + staff.lastName);
			}
		} else {
			headers.set("backend.return_code", "O");
			headers.set("backend.return_message", "There is no collaborator associated to the id " + idStaff);
			responseEntity = new ResponseEntity<Staff>(new Staff(), headers, HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a staff member for id " + String.valueOf(idStaff) + " in th Staff collection");
			}
		}
		System.out.println("responseEntity " + responseEntity.getBody());
		return responseEntity;
	}

	/**
	 * @param idStaff
	 *            staff member's identifier
	 * @return the list of projects where the staff member is involved
	 */
	@RequestMapping(value = "/projects/{idStaff}", method = RequestMethod.GET)
	ResponseEntity<List<Mission>> readProjects(@PathVariable("idStaff") int idStaff) {

		try {
			ResponseEntity<Staff> responseEntityStaffMember = read(idStaff);
	
			
			// Adding the name of project.
			for (Mission mission : responseEntityStaffMember.getBody().missions) {
					mission.name = projectHandler.get(mission.idProject).name;
			}
			
			ResponseEntity<List<Mission>> response = new ResponseEntity<List<Mission>>(
					responseEntityStaffMember.getBody().missions, responseEntityStaffMember.getHeaders(),
					responseEntityStaffMember.getStatusCode());
			return response;
			
		} catch (final SkillerException e) {
			logger.error(getStackTrace(e));
			return new ResponseEntity<List<Mission>>(
					new ArrayList<Mission>(), new HttpHeaders(),
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param idStaff
	 *            staff member's identifier
	 * @return the given developer's experience as list of skills.
	 */
	@RequestMapping(value = "/experiences/{idStaff}", method = RequestMethod.GET)
	ResponseEntity<List<Experience>> readExperiences(@PathVariable("idStaff") int idStaff) {

		ResponseEntity<Staff> responseEntityStaffMember = read(idStaff);

		ResponseEntity<List<Experience>> response = new ResponseEntity<List<Experience>>(
				responseEntityStaffMember.getBody().experiences, responseEntityStaffMember.getHeaders(),
				responseEntityStaffMember.getStatusCode());
		return response;
	}

	@PostMapping("/save")
	ResponseEntity<Staff> add(@RequestBody Staff input) {

		final ResponseEntity<Staff> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		if (logger.isDebugEnabled()) {
			logger.debug ("Add or Update the staff.id " + input.idStaff);
			logger.debug ("Content " + input);
		}
		if (input.idStaff == -1) {
			staffHandler.addNewStaffMember(input);
			headers.add("backend.return_code", "1");
			responseEntity = new ResponseEntity<Staff>(input, headers, HttpStatus.OK);
		} else {
			Staff updatedStaff = staffHandler.getStaff().get(input.idStaff);
			if (updatedStaff == null) {
				responseEntity = new ResponseEntity<Staff>(input, headers, HttpStatus.NOT_FOUND);
				headers.add("backend.return_code", "O");
				headers.add("backend.return_message", "There is no collaborator associated to the id " + input.idStaff);
			} else {
				if ((!input.isActive) && (updatedStaff.isActive)) {
					input.dateInactive = Global.now();
				}
				try {
					staffHandler.saveStaffMember(input);
				} catch (SkillerException e) {
					return new ResponseEntity<Staff>(
							new Staff(), new HttpHeaders(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
				responseEntity = new ResponseEntity<Staff>(input, headers, HttpStatus.OK);
				headers.add("backend.return_code", "1");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /staff/save returns the body " + responseEntity.getBody());
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
		public int idStaff;
		public int idSkill;
		public int level;
		public String formerSkillTitle;
		public String newSkillTitle;

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
	ResponseEntity<StaffDTO> saveExperience(@RequestBody String param) {

		ParamStaffSkill p = gson.fromJson(param, ParamStaffSkill.class);
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /staff/skill/save with params id:" + String.valueOf(p.idStaff) + ",skillName:"
					+ p.newSkillTitle + ", level " + p.level);
		}
		final ResponseEntity<StaffDTO> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

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
					final Experience formerExperienceToRemove = staff.getExperience(formerSkill.get().id);
					staff.experiences.remove(formerExperienceToRemove);
				}
			}

			/*
			 * If the passed skill is already present in the staff member's
			 * skill list, we update the level if necessary, or we send back an
			 * warning. No Update made.
			 */
			final Experience asset = staff.getExperience(result.get().id);
			if (asset != null) {
				if (asset.level != p.level) {
					asset.level = p.level;
					return new ResponseEntity<StaffDTO>(new StaffDTO(staff), HttpStatus.OK);
				}
				return postErrorReturnBodyMessage(HttpStatus.BAD_REQUEST.value(), "The collaborator " + staff.fullName()
						+ " has already this level (" + p.level + ") of skill for " + p.newSkillTitle, staff);
			}

			/*
			 * We create a NEW asset for this staff member.
			 */
			staff.experiences.add(new Experience(result.get().id, result.get().title, p.level));
			responseEntity = new ResponseEntity<StaffDTO>(new StaffDTO(staff), headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug("returning  staff " + gson.toJson(staff));
			}

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a skill with the name " + p.newSkillTitle);
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
		public int idStaff;
		public int idProject;
		public String formerProjectName;
		public String newProjectName;

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
	ResponseEntity<StaffDTO> revokeSkill(@RequestBody String param) {
		ParamStaffSkill p = gson.fromJson(param, ParamStaffSkill.class);
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /staff/experiences/del with params idStaff:" + String.valueOf(p.idStaff)
					+ ",idSkill:" + String.valueOf(p.idSkill));
		}

		final Staff staff = staffHandler.getStaff().get(p.idStaff);
		assert (staff != null);
		if (staff == null) {
			return postErrorReturnBodyMessage(404, "There is no staff member for id" + p.idStaff);
		}

		Optional<Experience> oExperience = staff.experiences.stream().filter(exp -> (exp.id == p.idSkill)).findFirst();
		if (oExperience.isPresent()) {
			staff.experiences.remove(oExperience.get());
		}

		return new ResponseEntity<StaffDTO>(new StaffDTO(staff), new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping("/api/uploadCV")
	ResponseEntity<ResumeDTO> uploadApplicationFile(
			@RequestParam("file") MultipartFile file, 
			@RequestParam("id") int id, 
			@RequestParam("type") int fileType) {

		final HttpHeaders headers = new HttpHeaders();
		String filename = StringUtils.cleanPath(file.getOriginalFilename());

		if (logger.isDebugEnabled()) {
			logger.debug("uploading " + filename + " for staff ID " + id + " of type " + fileType);
		}
		
		storageService.store(file);

		final Staff staff = staffHandler.getStaff().get(id);
		assert (staff != null);

		staff.updateApplication(filename, fileType);
		
		try {
			final Resume exp = resumeParserService.extract(filename, fileType);
			ResumeDTO resumeDTO = new ResumeDTO();
			exp.data().forEach(item -> resumeDTO.experience.add(
					new ResumeSkill(item.idSkill, 
									skillHandler.getSkills().get(item.idSkill).title, 
									item.count)));
			/**
			 * We put the most often repeated keywords at the beginning of the list.
			 */
			Collections.sort(resumeDTO.experience);
			return new ResponseEntity<ResumeDTO>(resumeDTO, headers, HttpStatus.OK);
		} catch (SkillerException e) {
			return new ResponseEntity<ResumeDTO>(
					new ResumeDTO(-1, e.getMessage()), 
					headers, 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "{id}/application", method = RequestMethod.GET)
	public ResponseEntity<Resource> downloadApplicationFile(
		    @PathVariable("id") int id, 
		    HttpServletRequest request) {

		final Staff staff = staffHandler.getStaff().get(id);
		assert (staff != null);

		if ((staff.application == null) || (staff.application.length() == 0)) {
			if (logger.isDebugEnabled()) {
				logger.debug("No application file for  " + staff.idStaff);
			}
	        return ResponseEntity.notFound().build();
		}
		
		// Load file as Resource
		Resource resource = storageService.loadAsResource(staff.application);

        // Try to determine file's content type
		final String contentType;
		switch (staff.typeOfApplication) {
		case StorageService.FILE_TYPE_TXT:
			contentType = "text/html;charset=UTF-8";
			break;
		case StorageService.FILE_TYPE_DOC:
			contentType ="application/msword";
			break;
		case StorageService.FILE_TYPE_DOCX:
			contentType ="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			break;
		case StorageService.FILE_TYPE_PDF:
			contentType ="application/pdf";
			break;
		default:
			contentType = "application/octet-stream";
			break;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(staff.application + " " + contentType);
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
	ResponseEntity<StaffDTO> saveExperiences(@RequestBody String body) {

		ResumeSkills p = gson.fromJson(body, ResumeSkills.class);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Adding " + p.skills.length + " skills for the staff ID " + p.idStaff);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Adding the skills below for the staff ID " + p.idStaff);
			Arrays.asList(p.skills).stream().forEach(skill -> logger.trace(skill.idSkill + " " + skill.title));
		}
		try {
			Staff staff = staffHandler.addExperiences(p.idStaff, p.skills);
			return new ResponseEntity<StaffDTO>(new StaffDTO(staff, 1, 
					staff.firstName + " " + staff.lastName + " has " + staff.experiences.size() + " skills now!"), 
					HttpStatus.OK);
		} catch (final SkillerException se) {
			return new ResponseEntity<StaffDTO>(
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
	ResponseEntity<StaffDTO> saveProject(@RequestBody String param) {

		try {
			ParamStaffProject p = gson.fromJson(param, ParamStaffProject.class);
			if (logger.isDebugEnabled()) {
				logger.debug("POST command on /staff/project/save with params id:" + String.valueOf(p.idStaff)
						+ ",projectName:" + p.newProjectName);
			}
			final ResponseEntity<StaffDTO> responseEntity;
			final HttpHeaders headers = new HttpHeaders();
	
			final Staff staff = staffHandler.getStaff().get(p.idStaff);
			assert (staff != null);
	
			Optional<Project> result = projectHandler.lookup(p.newProjectName);
			
			if (result.isPresent()) {
	
				/*
				 * If the passed project is already present in the staff member's
				 * project list, we send back a BAD_REQUEST to avoid duplicate
				 * entries
				 */
				Predicate<Mission> predicate = pr -> (pr.idProject == result.get().id);
				if (staff.missions.stream().anyMatch(predicate)) {
					responseEntity = new ResponseEntity<StaffDTO>(
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
					if (result.isPresent()) {
						Optional<Mission> optMission = staff.missions
							.stream()
							.filter(mission -> mission.idProject == formerProject.get().id)
							.findFirst();
						if (optMission.isPresent()) {
							staff.missions.remove(optMission.get());
						}
					}
				}
	
				staff.missions.add(new Mission(result.get().id, projectHandler.get(result.get().id).name));
				responseEntity = new ResponseEntity<StaffDTO>(new StaffDTO(staff), headers, HttpStatus.OK);
				if (logger.isDebugEnabled()) {
					logger.debug("returning  staff " + gson.toJson(staff));
				}
	
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Cannot find a Project with the name " + p.newProjectName);
				}
				return postErrorReturnBodyMessage(404, "There is no project with the name " + p.newProjectName, staff);
			}
			return responseEntity;
		} catch (SkillerException e) {
			logger.error(getStackTrace(e));
			return new ResponseEntity<StaffDTO>(
					new StaffDTO(new Staff(), e.errorCode,e.errorMessage), new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
		
	}

	/**
	 * Revoke the participation of staff member in a project.
	 */
	@PostMapping("/project/del")
	ResponseEntity<StaffDTO> revokeProject(@RequestBody String param) {

		ParamStaffProject p = gson.fromJson(param, ParamStaffProject.class);
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /staff/project/del with params idStaff:" + String.valueOf(p.idStaff)
					+ ",idProject:" + String.valueOf(p.idProject));
		}

		final Staff staff = staffHandler.getStaff().get(p.idStaff);
		assert (staff != null);
		if (staff == null) {
			return postErrorReturnBodyMessage(404, "There is no staff member for id" + p.idStaff);
		}

		Optional<Mission> oProject = staff.missions.stream().filter(pr -> (pr.idProject == p.idProject)).findFirst();
		if (oProject.isPresent()) {
			staff.missions.remove(oProject.get());
		}

		return new ResponseEntity<StaffDTO>(new StaffDTO(staff), new HttpHeaders(), HttpStatus.OK);
	}
	
	ResponseEntity<StaffDTO> postErrorReturnBodyMessage(int code, String message) {
		return postErrorReturnBodyMessage(code, message, new Staff());
	}

	ResponseEntity<StaffDTO> postErrorReturnBodyMessage(int code, String message, Staff staffMember) {
		return new ResponseEntity<StaffDTO>(new StaffDTO(staffMember, code, message), new HttpHeaders(),
				HttpStatus.BAD_REQUEST);
	}
}
