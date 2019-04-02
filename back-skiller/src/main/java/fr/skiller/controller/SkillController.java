package fr.skiller.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.bean.SkillHandler;
import fr.skiller.data.external.SkillDTO;
import fr.skiller.data.internal.Skill;
import fr.skiller.exception.SkillerException;

import static fr.skiller.Error.getStackTrace;
import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;

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

	@GetMapping(value = "/name/{projectName}")
	public ResponseEntity<SkillDTO> read(@PathVariable("projectName") String skillTitle) {
		
		final ResponseEntity<SkillDTO> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		
		Optional<Skill> result = skillHandler.lookup(skillTitle);
		if (result.isPresent()) {
			responseEntity = new ResponseEntity<>(new SkillDTO(result.get()), new HttpHeaders(), HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<>(
					new SkillDTO(new Skill(), 404, "There is no skill for the name " + skillTitle), 
					headers, 
					HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Cannot find a skill with the name %s", skillTitle));
			}			
		}
		return responseEntity;
	}
	
	@GetMapping(value = "/{idParam}")
	public ResponseEntity<Skill> read(@PathVariable("idParam") int idParam) {

		final ResponseEntity<Skill> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		final Skill searchSkill = skillHandler.getSkills().get(idParam);
		if (searchSkill != null) {
			responseEntity = new ResponseEntity<>(searchSkill, headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format(
					"Skill read for id %d returns %s", idParam, responseEntity.getBody()));
			}
		} else {
			headers.set(BACKEND_RETURN_CODE, "O");
			headers.set(BACKEND_RETURN_MESSAGE, "There is no collaborator associated to the id " + idParam);
			responseEntity = new ResponseEntity<>(new Skill(), headers, HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Cannot find a skill for id %d", idParam));
			}
		}
		return responseEntity;
	}

	@GetMapping("/all")
	public String readAll() {
		final String resultContent = g.toJson(skillHandler.getSkills().values());
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("'/skill/all' is returning %s", resultContent));
		}
		return resultContent;
	}
	
	/**
	 * Either save an existing skill, or create a new one.
	 * @param skill the skill sent by the front application in JSON format
	 * @return the (new) skill updated 
	 */
	@PostMapping("/save")
	public ResponseEntity<Skill> add(@RequestBody Skill skill) {

		final ResponseEntity<Skill> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		if (skill.getId() == 0) {
			skillHandler.addNewSkill(skill);
			headers.add(BACKEND_RETURN_CODE, "1");
			responseEntity = new ResponseEntity<>(skill, headers, HttpStatus.OK);
		} else {
			if (!skillHandler.containsSkill(skill.getId())) {
				responseEntity = new ResponseEntity<>(skill, headers, HttpStatus.NOT_FOUND);
				headers.add(BACKEND_RETURN_CODE, "O");
				responseEntity.getHeaders().set(BACKEND_RETURN_MESSAGE,
						"There is no skill associated to the id " + skill.getId());
				responseEntity.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
			} else {
				try {
					skillHandler.saveSkill(skill);
				} catch (SkillerException e) {
					logger.error(getStackTrace(e));
					return new ResponseEntity<>(new Skill(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
				}
				responseEntity = new ResponseEntity<>(skill, headers, HttpStatus.OK);
				headers.add("backend.return_code", "1");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("POST command on /skill/save returns the body %s", responseEntity.getBody()));
		}
		return responseEntity;
	}
}
