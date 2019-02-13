package fr.skiller.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.bean.SkillHandler;
import fr.skiller.data.external.SkillDTO;
import fr.skiller.data.internal.Skill;
import fr.skiller.exception.SkillerException;

import static fr.skiller.Error.getStackTrace;

@RestController
@RequestMapping("/skill")
public class SkillController {

	private final Logger logger = LoggerFactory.getLogger(SkillController.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();

	@Autowired
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
	
	/**
	 * Either save an existing skill, or create a new one.
	 * @param skill the skill sent by the front application in JSON format
	 * @return the (new) skill updated 
	 */
	@PostMapping("/save")
	ResponseEntity<Skill> add(@RequestBody Skill skill) {

		final ResponseEntity<Skill> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		Map<Integer, Skill> skills = skillHandler.getSkills();

		if (skill.id == 0) {
			skillHandler.addNewSkill(skill);
			headers.add("backend.return_code", "1");
			responseEntity = new ResponseEntity<Skill>(skill, headers, HttpStatus.OK);
		} else {
			if (!skillHandler.containsSkill(skill.id)) {
				responseEntity = new ResponseEntity<Skill>(skill, headers, HttpStatus.NOT_FOUND);
				headers.add("backend.return_code", "O");
				responseEntity.getHeaders().set("backend.return_message",
						"There is no skill associated to the id " + skill.id);
				responseEntity.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
			} else {
				try {
					skillHandler.saveSkill(skill);
				} catch (SkillerException e) {
					logger.error(getStackTrace(e));
					return new ResponseEntity<Skill>(new Skill(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
				}
				responseEntity = new ResponseEntity<Skill>(skill, headers, HttpStatus.OK);
				headers.add("backend.return_code", "1");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /skill/save returns the body " + responseEntity.getBody());
		}
		return responseEntity;
	}
}
