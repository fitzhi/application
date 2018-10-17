/**
 * 
 */
package fr.skiller.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.ResumeDTO;
import fr.skiller.data.internal.Resume;
import fr.skiller.data.internal.ResumeSkill;
import fr.skiller.data.internal.Staff;
import fr.skiller.service.StorageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.AssertTrue;

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

	@LocalServerPort
	private int port;

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
		List<ResumeSkill> resultList = new ArrayList<ResumeSkill>();
		response.getBody().experience.stream()
			.filter(item -> "C#".equals(item.skill))
			.forEach(resultList::add);
		assertThat(!resultList.isEmpty());
		
		resultList.clear();
		response.getBody().experience.stream()
		.filter(item -> "Java".equals(item.skill))
		.forEach(resultList::add);
		assertThat(resultList.isEmpty());

		resultList.clear();
		response.getBody().experience.stream()
		.filter(item -> "Spring".equals(item.skill))
		.forEach(resultList::add);
		assertThat(!resultList.isEmpty());

		// {C#=2, AngularJS=3, Maven=1, Tomcat=1, Mercurial=1, SVN=1, Dojo=1, JUnit=1, PL-SQL=2, MySql=1, Javascript=4, Spring-WS=1, .Net=1, JSF=1, Oracle=3, ArcGIS=1, Java=2, Hibernate=7, myBatis=1, JBoss=3, Sonar=1, ant=1, Spring=8, Jenkins=1, Play=2, IIS=2, WebLogic=3, jQuery=3, Ansible=1, Flex=1, SQLServer=3, Kubernetes=1}
	}
	
}