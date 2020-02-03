/**
 * 
 */
package com.fitzhi.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fitzhi.SkillerRuntimeException;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.external.ResumeDTO;
import com.fitzhi.data.internal.ResumeSkillIdentifier;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.security.TokenLoader;
import com.fitzhi.service.FileType;
import com.fitzhi.service.impl.storageservice.ApplicationStorageProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
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
	
	@Autowired
	MockMvc mvc;

	@Autowired
	ApplicationStorageProperties storageProperties;

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
	@WithMockUser
	public void shouldUploadFile() throws Exception {
		ClassPathResource resource = new ClassPathResource( "/applications_files/ET_201709.doc");

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, 
				"Bearer " + TokenLoader.obtainAccessMockToken(mvc));
		
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("file", resource);
		map.add("id", 1);
		map.add("type", FileType.FILE_TYPE_DOC.getValue());
		
		ResponseEntity<ResumeDTO> response = this.restTemplate
				.exchange("/api/staff/api/uploadCV", HttpMethod.POST, new HttpEntity<>(map, headers),
				ResumeDTO.class);

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
		
		//
		// The file is correctly uploaded
		//
		File file = new File (storageProperties.getLocation() + 
				String.format("/%d-ET_201709.doc", 1));
		assertThat(file.exists());
		assertThat(file.delete());		
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
	
	@After
	public void after() {
		File file = new File (storageProperties.getLocation() + 
				String.format("/%d-ET_201709.doc", 1));
		if (file.exists()) {
			file.delete();		
		}
	}
}