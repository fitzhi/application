package fr.skiller.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import fr.skiller.security.TokenLoader;
import fr.skiller.service.StorageService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class FileUploadIntegrationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private StorageService storageService;

	@LocalServerPort
	private int port;

	@Autowired
	MockMvc mvc;

	public static String convertStreamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder(2048); // Define a size if you have an idea of it.
		char[] read = new char[128]; // Your buffer size.
		try (InputStreamReader ir = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			for (int i; -1 != (i = ir.read(read)); sb.append(read, 0, i))
				;
		} catch (Exception t) { }
		return sb.toString();
	}

	@Test
	@WithMockUser
	public void shouldUploadFile() throws Exception {

		ClassPathResource resource = new ClassPathResource("/api/uploadTest/testupload.txt");
		String content = convertStreamToString(resource.getInputStream());
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", content.getBytes());
		this.mvc.perform(fileUpload("/api/upload/do").file(multipartFile)).andExpect(status().isFound())
				.andExpect(header().string("Location", "/"));

		then(storageService).should().store(any(MultipartFile.class), "test.txt");
	}

	@Test
	@WithMockUser
	public void shouldDownloadFile() throws Exception {
		ClassPathResource resource = new ClassPathResource("/api/uploadTest/testupload.txt");
		given(this.storageService.loadAsResource("testupload.txt")).willReturn(resource);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, 
				"Bearer " + TokenLoader.obtainAccessMockToken(mvc));
		
		ResponseEntity<String> response = this.restTemplate
				.exchange("/api/upload/files/{filename}", 
						HttpMethod.GET, 
						new HttpEntity<>(headers), 
						String.class,
						"testupload.txt");
		
		assertThat(response.getStatusCodeValue() == 200);
		assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
				.isEqualTo("attachment; filename=\"testupload.txt\"");
		assertThat(response.getBody()).isEqualTo("Spring Framework");
	}

}
