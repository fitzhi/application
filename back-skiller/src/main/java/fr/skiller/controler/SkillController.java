package fr.skiller.controler;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.data.Collaborator;
import fr.skiller.data.Skill;

@RestController
@RequestMapping("/skill")
public class SkillController {

	Logger logger = LoggerFactory.getLogger("backend-skiller");

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();

	/**
	 * The staff collection.
	 */
	private HashMap<Integer, Skill> skill;

	/**
	 * @return the skill collection.
	 */
	private Map<Integer, Skill> getSkill() {
		if (this.skill != null) {
			return this.skill;
		}
		this.skill = new HashMap<Integer, Skill>();
		return skill;
	}

	@RequestMapping(value = "/{idParam}", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:4200")
	ResponseEntity<Skill> read(@PathVariable("idParam") int idParam) {

		final ResponseEntity<Skill> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		final Skill searchSkill = getSkill().get(idParam);
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
	@CrossOrigin(origins = "http://localhost:4200")
	String readAll() {
		return g.toJson(getSkill());
	}

	@PostMapping("/save")
	@CrossOrigin(origins = "http://localhost:4200")
	ResponseEntity<Skill> add(@RequestBody Skill input) {

		final ResponseEntity<Skill> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		Map<Integer, Skill> skills = getSkill();

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
