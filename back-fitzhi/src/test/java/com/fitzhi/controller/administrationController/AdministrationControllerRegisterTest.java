package com.fitzhi.controller.administrationController;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.ClassicCredentials;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * <p>
 * Testing the creation of a user.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "allowSelfRegistration=false" }) 
public class AdministrationControllerRegisterTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().
		registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	/**
	 * Directory where the footprint of the very first solution is made.
	 */
	@Value("${applicationOutDirectory}")
	private String rootLocation;

	@MockBean
	StaffHandler staffHandler;

	@MockBean
	public Administration administration;

	@Test
	public void registerOk() throws Exception {
				
		when(administration.createNewUser("adminForTest", "passForTest")).thenReturn(
			new Staff(1789, "login", "password"));

		ClassicCredentials cc = ClassicCredentials.of("adminForTest", "passForTest");

		this.mvc.perform(post("/api/admin/classic/register")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.content(gson.toJson(cc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.idStaff", is(1789)))
				.andExpect(jsonPath("$.login", is("login")))
				.andExpect(jsonPath("$.password", is("password"))) // not returning the given password by a géneric work.
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		Mockito.verify(administration, times(1)).createNewUser("adminForTest", "passForTest");
	}

	@Test
	public void registerKo() throws Exception {
				
		when(administration.createNewUser("adminForTest", "passForTest")).thenThrow(new ApplicationException(666, "Failure"));

		ClassicCredentials cc = ClassicCredentials.of("adminForTest", "passForTest");

		this.mvc.perform(post("/api/admin/classic/register")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.content(gson.toJson(cc)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code", is(666)))
				.andExpect(jsonPath("$.message", is("Failure")))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

}
