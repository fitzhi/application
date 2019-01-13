/**
 * 
 */
package fr.skiller.controller;

import static org.assertj.core.api.Assertions.assertThat;

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

import fr.skiller.bean.SkillHandler;
import fr.skiller.data.external.ResumeDTO;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.ResumeSkillIdentifier;
import fr.skiller.data.internal.Skill;
import fr.skiller.service.StorageService;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StaffController_uploadResume_Test {

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
	public void before() throws Exception {
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
	}

	@Test
	public void shouldUploadFile() throws Exception {
		ClassPathResource resource = new ClassPathResource( "/applications_files/ET_201709.doc");
		
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("file", resource);
		map.add("id", 1);
		map.add("type", StorageService.FILE_TYPE_DOC);
		ResponseEntity<ResumeDTO> response = this.restTemplate.postForEntity("/staff/api/uploadCV", map,
				ResumeDTO.class);

		assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
		List<ResumeSkillIdentifier> resultList = new ArrayList<ResumeSkillIdentifier>();
		
		response.getBody().experience.stream()
			.filter(item -> getIdSkill("C#") == item.idSkill )
			.forEach(resultList::add);
		assertThat(!resultList.isEmpty());
		
		resultList.clear();
		response.getBody().experience.stream()
		.filter(item -> getIdSkill("Java") == item.idSkill)
		.forEach(resultList::add);
		assertThat(resultList.isEmpty());

		resultList.clear();
		response.getBody().experience.stream()
		.filter(item -> getIdSkill("Spring") == item.idSkill)
		.forEach(resultList::add);
		assertThat(!resultList.isEmpty());

		// {C#=2, AngularJS=3, Maven=1, Tomcat=1, Mercurial=1, SVN=1, Dojo=1, JUnit=1, PL-SQL=2, MySql=1, Javascript=4, Spring-WS=1, .Net=1, JSF=1, Oracle=3, ArcGIS=1, Java=2, Hibernate=7, myBatis=1, JBoss=3, Sonar=1, ant=1, Spring=8, Jenkins=1, Play=2, IIS=2, WebLogic=3, jQuery=3, Ansible=1, Flex=1, SQLServer=3, Kubernetes=1}
	}
	
	private int getIdSkill (final String title) {
		Optional<Skill> optSkill = skillHandler.getSkills()
				.values().stream()
				.filter(skill -> title.equals(skill.title)).findFirst();
		if (optSkill.isPresent()) {
			return optSkill.get().id;
		} else {
			throw new RuntimeException("Should not pass here!");
		}
	}
}