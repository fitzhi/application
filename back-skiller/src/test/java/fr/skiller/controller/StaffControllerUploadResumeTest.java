/**
 * 
 */
package fr.skiller.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.SkillHandler;
import fr.skiller.data.external.ResumeDTO;
import fr.skiller.data.internal.ResumeSkillIdentifier;
import fr.skiller.data.internal.Skill;
import fr.skiller.service.StorageService;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StaffControllerUploadResumeTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SkillHandler skillHandler;
	
	@LocalServerPort
	private int port;
	
	@Before 
	public void before()  {
		if (!skillHandler.containsSkill(1)) {
			skillHandler.addNewSkill(new Skill(1, "Java"));
		}
		if (!skillHandler.containsSkill(2)) {
			skillHandler.addNewSkill(new Skill(2, ".NET"));
		}
		if (!skillHandler.containsSkill(3)) {
			skillHandler.addNewSkill(new Skill(3, "C#"));
		}
		if (!skillHandler.containsSkill(4)) {
			skillHandler.addNewSkill(new Skill(4, "Spring"));
		}
		if (!skillHandler.containsSkill(5)) {
			skillHandler.addNewSkill(new Skill(5, "Python"));
		}
	}

	@Test
	public void shouldUploadFile() throws IOException {
		ClassPathResource resource = new ClassPathResource( "/applications_files/ET_201709.doc");
		
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("file", resource);
		map.add("id", 1);
		map.add("type", StorageService.FILE_TYPE_DOC);
		ResponseEntity<ResumeDTO> response = this.restTemplate.postForEntity("/staff/api/uploadCV", map,
				ResumeDTO.class);

		assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
		List<ResumeSkillIdentifier> resultList = new ArrayList<>();
		
		response.getBody().experience.stream()
			.filter(item -> getIdSkill("C#") == item.getIdSkill() )
			.forEach(resultList::add);
		assertThat(!resultList.isEmpty()).as("C# is present in the CV").isTrue();
		
		resultList.clear();
		response.getBody().experience.stream()
		.filter(item -> getIdSkill("Java") == item.getIdSkill())
		.forEach(resultList::add);
		assertThat(!resultList.isEmpty()).as("Java is present in the CV").isTrue();

		resultList.clear();
		response.getBody().experience.stream()
		.filter(item -> getIdSkill("Spring") == item.getIdSkill())
		.forEach(resultList::add);
		assertThat(!resultList.isEmpty()).as("Spring is present in the CV").isTrue();

		resultList.clear();
		response.getBody().experience.stream()
		.filter(item -> getIdSkill("Python") == item.getIdSkill())
		.forEach(resultList::add);
		assertThat(resultList.isEmpty()).as("Python is NOT present in the CV").isTrue();
	}
	
	private int getIdSkill (final String title) {
		Optional<Skill> optSkill = skillHandler.getSkills()
				.values().stream()
				.filter(skill -> title.equals(skill.getTitle())).findFirst();
		if (optSkill.isPresent()) {
			return optSkill.get().getId();
		} else {
			throw new SkillerRuntimeException("Should not pass here for " + title + " !");
		}
	}
}