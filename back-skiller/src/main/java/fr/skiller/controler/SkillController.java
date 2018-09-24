package fr.skiller.controler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.SkillHandler;
import fr.skiller.data.external.ProjectDTO;
import fr.skiller.data.external.SkillDTO;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;

@RestController
@RequestMapping("/skill")
public class SkillController {

	Logger logger = LoggerFactory.getLogger("backend-skiller");

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();

	@Autowired
	@Qualifier("mock.Skill")
	SkillHandler skillHandler;

	@RequestMapping(value = "/name/{projectName}", method = RequestMethod.GET)
	ResponseEntity<SkillDTO> read(@PathVariable("projectName") String skillTitle) {
		
		final ResponseEntity<SkillDTO> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		
		Optional<Skill> result = skillHandler.lookup(skillTitle);
		if (result.isPresent()) {
			responseEntity = new ResponseEntity<SkillDTO>(new SkillDTO(result.get()), new HttpHeaders(), HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<SkillDTO>(
					new SkillDTO(new Skill(), 404, "There is no skill for the name " + skillTitle), 
					headers, 
					HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a skill with the name " + skillTitle);
			}			
		}
		return responseEntity;
	}
	
	@RequestMapping(value = "/{idParam}", method = RequestMethod.GET)
	ResponseEntity<Skill> read(@PathVariable("idParam") int idParam) {

		final ResponseEntity<Skill> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		final Skill searchSkill = skillHandler.getSkills().get(idParam);
		if (searchSkill != null) {
			responseEntity = new ResponseEntity<Skill>(searchSkill, headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug("Skill read for id " + String.valueOf(idParam) + " returns " + responseEntity.getBody());
			}
		} else {
			headers.set("backend.return_code", "O");
			headers.set("backend.return_message", "There is no collaborator associated to the id " + idParam);
			responseEntity = new ResponseEntity<Skill>(new Skill(), headers, HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a skill for id " + String.valueOf(idParam));
			}
		}
		return responseEntity;
	}

	@GetMapping("/all")
	String readAll() {
		final String resultContent = g.toJson(skillHandler.getSkills().values());
		if (logger.isDebugEnabled()) {
			logger.debug("'/skill/all' is returning " + resultContent);
		}
		return resultContent;
	}
	
	@PostMapping("/save")
	ResponseEntity<Skill> add(@RequestBody Skill input) {

		final ResponseEntity<Skill> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		Map<Integer, Skill> skills = skillHandler.getSkills();

		if (input.id == 0) {
			input.id = skills.size() + 1;
			skills.put(input.id, input);
			headers.add("backend.return_code", "1");
			responseEntity = new ResponseEntity<Skill>(input, headers, HttpStatus.OK);
		} else {
			final Skill searchSkill = skills.get(input.id);
			if (searchSkill == null) {
				responseEntity = new ResponseEntity<Skill>(input, headers, HttpStatus.NOT_FOUND);
				headers.add("backend.return_code", "O");
				responseEntity.getHeaders().set("backend.return_message",
						"There is no skill associated to the id " + input.id);
				responseEntity.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
			} else {
				searchSkill.title = input.title;
				responseEntity = new ResponseEntity<Skill>(input, headers, HttpStatus.OK);
				headers.add("backend.return_code", "1");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /skill/save returns the body " + responseEntity.getBody());
		}
		return responseEntity;
	}
}
