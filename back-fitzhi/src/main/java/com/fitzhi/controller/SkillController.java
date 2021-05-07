package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_SKILL_NOFOUND;
import static com.fitzhi.Error.MESSAGE_SKILL_NOFOUND;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/skill")
@Api(
	tags="Skill controller API",
	description = "API endpoints to manage the skills declared inside the application."
)
public class SkillController extends BaseRestController {

	@Autowired
	SkillHandler skillHandler;

	/**
	 * <p>
	 * This method <b>CREATES a NEW SKILL</b> and returns a valid location to load the skill.
	 * </p>
	 * @param builder the {@code Spring} URI builder
	 * @param skill the skill sent by the front application in JSON format
	 * @return a ResponseEntity with just the location containing the URI of the newly
	 *         created skill
	 */
	@PostMapping("")
	public ResponseEntity<Object> create(UriComponentsBuilder builder, @RequestBody Skill skill) {

		if (skillHandler.containsSkill(skill.getId())) {
			return new ResponseEntity<Object>(null, headers(), HttpStatus.CONFLICT);
		}
		
		Skill newSkill = skillHandler.addNewSkill(skill);
		if (log.isDebugEnabled()) {
			log.debug(String.format("The skill %s obtains the id %d", newSkill.getTitle(), newSkill.getId()));
		}

		UriComponents uriComponents = builder.path("/api/skill/{id}").buildAndExpand(newSkill.getId());

		return ResponseEntity.created(uriComponents.toUri()).build();

	}

	/**
	 * Update an existing skill, or create a new one.
	 *
	 * @param idProject the skill identifier. The skill identifier is present in the REST URL in accordance with the Rest naming conventions
	 * @param skill the skill sent by the Angular application in JSON format
	 * @return an empty content response with a {@code code 200} or {@code code 404} error if the skill identifier does not exist.
	 * @throws NotFoundException if there is no skill for the given identifier 
	 */
	@PutMapping("/{idSkill}")
	public ResponseEntity<Void> update(@PathVariable("idSkill") int idSkill, @RequestBody Skill skill) throws NotFoundException, ApplicationException {

		if (idSkill != skill.getId()) {
			throw new ApplicationRuntimeException("WTF : SHOULD NOT PASS HERE!");
		}

		if (!skillHandler.containsSkill(idSkill)) {
			throw new NotFoundException(CODE_SKILL_NOFOUND, MessageFormat.format(MESSAGE_SKILL_NOFOUND, idSkill));
		}

		skillHandler.saveSkill(skill);

		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/name/{projectName}")
	public ResponseEntity<Skill> lookup(@PathVariable("projectName") String skillTitle) throws ApplicationException {	
		Optional<Skill> result = skillHandler.lookup(skillTitle);
		if (!result.isPresent()) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Cannot find a skill with the name %s", skillTitle));
			}			
			throw new NotFoundException(CODE_SKILL_NOFOUND, 
						MessageFormat.format(MESSAGE_SKILL_NOFOUND, skillTitle));
		}
		return new ResponseEntity<>(result.get(), headers(), HttpStatus.OK);
	}
	

	@GetMapping(value = "/{idParam}")
	public ResponseEntity<Skill> read(@PathVariable("idParam") int idParam) throws ApplicationException {
		final Skill skill = skillHandler.getSkill(idParam);
		System.out.println(skill);
		return new ResponseEntity<>(skill, headers(), HttpStatus.OK);
	}

	@GetMapping("")
	public Collection<Skill> readAll() {
		Collection<Skill> skills = skillHandler.getSkills().values();
		if (log.isDebugEnabled()) {
			log.debug(String.format("'/skill' is returning %d skills", skills.size()));
		}
		return skills;
	}
		
	/**
	 * @return the map of skills detection templates
	 * @throws ApplicationException thrown if any problem occcurs, most probably an {@link IOException}
	 */
	@GetMapping("/detection-templates")
	public ResponseEntity<Map<Integer, String>> detectionTemplate() throws ApplicationException {
		Map<Integer, String> mapDetectionTemplates = this.skillHandler.detectorTypes();
		return new ResponseEntity<Map<Integer, String>>(mapDetectionTemplates, headers(), HttpStatus.OK);
	}
}
