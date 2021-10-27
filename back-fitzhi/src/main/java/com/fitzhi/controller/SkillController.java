package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_SKILL_NOFOUND;
import static com.fitzhi.Error.CODE_YEAR_MONTH_INVALID;
import static com.fitzhi.Error.MESSAGE_SKILL_NOFOUND;
import static com.fitzhi.Error.MESSAGE_YEAR_MONTH_INVALID;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.controller.util.YearMonthParser;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/skill")
@Api(
	tags="Skill controller API",
	description = "API endpoints to manage the skills declared inside the application."
)
public class SkillController {

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
	@ApiOperation(
		value = "Create a new skill inside the application",
		notes = "The creation of 2 skills with the same title is not allowed"
	)
	@PostMapping("")
	public ResponseEntity<Void> create(UriComponentsBuilder builder, @RequestBody Skill skill) {

		if (skillHandler.containsSkill(skill.getId())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		
		Skill newSkill = skillHandler.addNewSkill(skill);
		if (log.isDebugEnabled()) {
			log.debug(String.format("The skill %s obtains the id %d", newSkill.getTitle(), newSkill.getId()));
		}

		UriComponents uriComponents = builder.path("/api/skill/{id}").buildAndExpand(newSkill.getId());

		return ResponseEntity.created(uriComponents.toUri()).build();

	}

	/**
	 * Update an existing skill.
	 *
	 * @param idSkill the skill identifier. The skill identifier is present in the REST URL in accordance with the Rest naming conventions
	 * @param skill the skill sent by the Angular application in JSON format
	 * @return an empty content response with a {@code code 200} or {@code code 404} error if the skill identifier does not exist.
	 * @throws NotFoundException if there is no skill for the given identifier 
	 */
	@ApiOperation(
		value ="Update an existing skill."
	)
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

	/**
	 * We do not allow to remove all skills in the application.
	 * 
	 * @return an empty HTTP Response because this method is not allowed.
	 */
	@ApiOperation(value = "This method is not allowed.", notes = "People cannot remove an existing skill.")
	@DeleteMapping("/{idSkill}")
	public ResponseEntity<Object> removeSkill(@PathVariable("idSkill") int idSkill) throws ApplicationException {		
		return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * We do not allow to remove one SINGLE skill in the application.
	 * 
	 * @return an empty HTTP Response because this method is not allowed.
	 */
	@ApiOperation(value = "This method is not allowed.", notes = "People cannot clean the skills collection.")
	@DeleteMapping()
	public ResponseEntity<Object> removeAllSkills() throws ApplicationException {		
		return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ResponseBody
	@ApiOperation(value = "Retrieve a skill with its name.")
	@GetMapping(value = "/name/{projectName}")
	public Skill lookup(@PathVariable("projectName") String skillTitle) throws ApplicationException {	
		
		Optional<Skill> result = skillHandler.lookup(skillTitle);
		if (!result.isPresent()) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Cannot find a skill with the name %s", skillTitle));
			}			
			throw new NotFoundException(CODE_SKILL_NOFOUND, 
						MessageFormat.format(MESSAGE_SKILL_NOFOUND, skillTitle));
		}
		return result.get();
	}
	
	@ResponseBody
	@ApiOperation(value = "Retrieve a skill with its identifier.")
	@GetMapping(value = "/{idSkill}")
	public Skill read(@PathVariable("idSkill") int idSkill) throws ApplicationException {
		final Skill skill = skillHandler.getSkill(idSkill);
		return skill;
	}

	@ResponseBody
	@ApiOperation(value = "Load and return all skills declared in the application.")
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
	@ResponseBody
	@ApiOperation(
		value = "Load and return all types of detectors",
		notes = "A skill can be detected in the repository by multiple ways. " + 
				"It might be a 'Filename filter pattern', a 'Dependency detection in the package.json file', or a 'Dependency detection in the pom.xml file', ..."
	)
	@GetMapping("/detection-templates")
	public Map<Integer, String> detectionTemplate() throws ApplicationException {
		Map<Integer, String> mapDetectionTemplates = this.skillHandler.detectorTypes();
		return mapDetectionTemplates;
	}

	@ResponseBody
	@ApiOperation(value = "Load and return the constellation (if any), registered for the given month.")
	@GetMapping("/constellation/{year}/{month}")
	public Constellation loadConstellation(@PathVariable("year") int year, @PathVariable("month") int month) throws ApplicationException {

		if (!YearMonthParser.isValid(year, month)) {
			throw new ApplicationException(
				CODE_YEAR_MONTH_INVALID,
				MessageFormat.format(MESSAGE_YEAR_MONTH_INVALID, year, month));
		}
		
		LocalDate date = LocalDate.of(year, month, 1);
		Constellation constellation = skillHandler.loadConstellation(date);
		if (log.isDebugEnabled()) {
			log.debug(String.format("'/constellation' is returning %d skills in its constellation for %d/%d.", 0, month, year));
		}
		return constellation;
	}

}
