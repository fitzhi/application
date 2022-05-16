/**
 * 
 */
package com.fitzhi.controller.staff;

import static com.fitzhi.Error.CODE_STAFF_NOFOUND;
import static com.fitzhi.Error.MESSAGE_STAFF_NOFOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.external.StaffResume;
import com.fitzhi.data.internal.Resume;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.service.FileType;
import com.fitzhi.service.ResumeParserService;
import com.fitzhi.service.StorageService;
import com.fitzhi.service.impl.storageservice.ApplicationStorageProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class StaffControllerUploadApplicationFileTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private StaffHandler staffHandler;

	@MockBean()
	@Qualifier("Application")
	private StorageService storageService;

	@MockBean
	private ResumeParserService resumeParserService;

	@LocalServerPort
	private int port;
	
	@Autowired
	MockMvc mvc;

	@Autowired
	ApplicationStorageProperties storageProperties;

	@Test
	@WithMockUser
	public void shouldUploadFile() throws Exception {

		ClassPathResource resource = new ClassPathResource( "/applications_files/ET_201709.doc");

		when(staffHandler.getStaff(1)).thenReturn(new Staff(1, "login", "pass"));
		doNothing().when(storageService).store(any(MultipartFile.class), anyString());
		when(resumeParserService.extract(anyString(), any(FileType.class))).thenReturn(new Resume());
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("file", resource);
		map.add("type", FileType.FILE_TYPE_DOC.getValue());
		
		ResponseEntity<StaffResume> response = this.restTemplate
				.exchange("/api/staff/1/uploadCV", HttpMethod.POST, new HttpEntity<>(map, new HttpHeaders()),
				StaffResume.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

		Mockito.verify(staffHandler, times(1)).getStaff(1);
		Mockito.verify(storageService, times(1)).store(any(MultipartFile.class), anyString());
		Mockito.verify(resumeParserService, times(1)).extract(anyString(), any(FileType.class));
	}
	
	@Test
	@WithMockUser
	public void shouldHandleFailure() throws Exception {

		ClassPathResource resource = new ClassPathResource( "/applications_files/ET_201709.doc");

		when(staffHandler.getStaff(1)).thenThrow(
			new NotFoundException(CODE_STAFF_NOFOUND, MESSAGE_STAFF_NOFOUND, 1));

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("file", resource);
		map.add("type", FileType.FILE_TYPE_DOC.getValue());
		
		ResponseEntity<StaffResume> response = this.restTemplate
				.exchange("/api/staff/1/uploadCV", HttpMethod.POST, new HttpEntity<>(map, new HttpHeaders()),
				StaffResume.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

		Mockito.verify(staffHandler, times(1)).getStaff(1);
	}

}