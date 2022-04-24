package com.fitzhi.controller.administrationController;

import static com.fitzhi.Error.CODE_INVALID_OPENID_SERVER;
import static com.fitzhi.Error.CODE_OPENID_ALREADY_REGISTERED;
import static com.fitzhi.Global.GITHUB_OPENID_SERVER;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.AdminController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.OpenId;
import com.fitzhi.data.internal.OpenIdCredentials;
import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.token.TokenHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test the method {@link AdminController#openidRegister(OpenIdCredentials)} with the Github identity server.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerGithubOpenIdRegisterTest {
	
	@Autowired
	private MockMvc mvc;

	@MockBean
	private StaffHandler staffHandler;

	@MockBean
	@Qualifier("GITHUB")
	private TokenHandler tokenHandler;

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().
		registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Test
	public void nominalPrimeRegister() throws Exception {

		when(staffHandler.createStaffMember(any())).thenReturn(new Staff (1789, "...login", "nope..."));

		OpenIdToken oit = OpenIdToken.of();
		oit.setServerId(GITHUB_OPENID_SERVER);
		when(tokenHandler.takeInAccountToken(any(String.class))).thenReturn(oit);

		OpenIdCredentials oic = OpenIdCredentials.of(GITHUB_OPENID_SERVER, "idToken"); 

		this.mvc.perform(post("/api/admin/openId/register")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.content(gson.toJson(oic)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.idStaff", is(1789)))
				.andExpect(jsonPath("$.login", is("...login")))
				.andExpect(jsonPath("$.password", is("nope...")))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void invalidOpenIdServer() throws Exception {

		OpenIdCredentials oic = OpenIdCredentials.of("UNKNOWN", "idToken"); 

		this.mvc.perform(post("/api/admin/openId/register")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.content(gson.toJson(oic)))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is("Invalid OpenId server UNKNOWN.")))
				.andExpect(jsonPath("$.code", is(CODE_INVALID_OPENID_SERVER)));
	}

	@Test
	public void invalidToken() throws Exception {

		OpenIdCredentials oic = OpenIdCredentials.of(GITHUB_OPENID_SERVER, "idToken"); 

		when(tokenHandler.takeInAccountToken(any(String.class))).thenThrow(new ApplicationException(1789, "Error 1789"));

		this.mvc.perform(post("/api/admin/openId/register")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.content(gson.toJson(oic)))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is("Error 1789")))
				.andExpect(jsonPath("$.code", is(1789)));
	}

	@Test
	public void tokenAlreadyRegisteredMessageWithEmail() throws Exception {

		Staff staff = new Staff();
		staff.setIdStaff(1789);
		staff.setLastName("VIDAL");
		staff.setFirstName("Frédéric");
		staff.setEmail("fv@nope.com");
		when(staffHandler.lookup(any(OpenId.class))).thenReturn(staff);

		OpenIdCredentials oic = OpenIdCredentials.of(GITHUB_OPENID_SERVER, "idToken"); 

		OpenIdToken oit = OpenIdToken.of();
		oit.serverId = GITHUB_OPENID_SERVER;
		oit.setUserId("testUserId");
		oit.setEmail("fv@nope.com");
		when(tokenHandler.takeInAccountToken(any(String.class))).thenReturn(oit);

		this.mvc.perform(post("/api/admin/openId/register")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.content(gson.toJson(oic)))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message").value("The login/email openID \"fv@nope.com\" is already registered with Frédéric VIDAL (1789)."))
				.andExpect(jsonPath("$.code", is(CODE_OPENID_ALREADY_REGISTERED)));
	}

	@Test
	public void tokenAlreadyRegisteredMessageWithLogin() throws Exception {

		Staff staff = new Staff();
		staff.setIdStaff(1789);
		staff.setLastName("VIDAL");
		staff.setFirstName("Frédéric");
		staff.setEmail("fv@nope.com");
		when(staffHandler.lookup(any(OpenId.class))).thenReturn(staff);

		OpenIdCredentials oic = OpenIdCredentials.of(GITHUB_OPENID_SERVER, "idToken"); 

		OpenIdToken oit = OpenIdToken.of();
		oit.serverId = GITHUB_OPENID_SERVER;
		oit.setUserId("testUserId");
		oit.setEmail("fv@nope.com");
		oit.setLogin("fvLogin");
		when(tokenHandler.takeInAccountToken(any(String.class))).thenReturn(oit);

		this.mvc.perform(post("/api/admin/openId/register")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.content(gson.toJson(oic)))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message").value("The login/email openID \"fvLogin\" is already registered with Frédéric VIDAL (1789)."))
				.andExpect(jsonPath("$.code", is(CODE_OPENID_ALREADY_REGISTERED)));
	}
}
