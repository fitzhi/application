/**
 * 
 */
package com.fitzhi.controller.administrationController;

import static com.fitzhi.Error.CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static com.fitzhi.Error.MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
public class AdministrationControllerVeryFirstUserTest {

	private static final String LOGIN = "login";

	private static final String CST_STAFF_ID_STAFF = "$.idStaff";

	private static final String PASS_WORD = "password"; //NOSONAR

	@Autowired
	private MockMvc mvc;

	/**
	 * Directory where the footprint of the very first solution is made.
	 */
	@Value("${applicationOutDirectory}")
	private String rootLocation;

	@MockBean
	StaffHandler staffHandler;

	@Autowired
	public Administration administration;
	@Test
	public void creationVeryFirstUserOK() throws Exception {
				
		when(staffHandler.getStaff()).thenReturn(new HashMap<Integer, Staff>());
		when(staffHandler.createWorkforceMember(any(Staff.class))).thenReturn(new Staff(1, "adminForTest", "passForTest"));

		//
		// We disable this line for the Sonar analysis to avoid a useless password security check. 
		// This fake password is useless for any hacker
		//
		this.mvc.perform(post("/api/admin/veryFirstUser") //NOSONAR
					.param(LOGIN, "adminForTest") 
					.param(PASS_WORD, "passForTest"))  
				.andExpect(status().isOk())
				.andExpect(jsonPath(CST_STAFF_ID_STAFF, is(1)))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void creationVeryFirstUserKO() throws Exception {
		
		final Map<Integer, Staff> map = new HashMap<Integer, Staff>();
		map.put(1, new Staff(1789, "name", "password"));
		when(staffHandler.getStaff()).thenReturn(map);
		when(staffHandler.createWorkforceMember(any(Staff.class))).thenReturn(new Staff(1, "adminForTest", "passForTest"));

		//
		// We disable this line for the Sonar analysis to avoid a useless password security check. 
		// This fake password is useless for any hacker
		//
		this.mvc.perform(post("/api/admin/veryFirstUser") //NOSONAR
				.param(LOGIN, "adminForTest") 
				.param(PASS_WORD, "passForTest"))  
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code", is(CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED)))
			.andExpect(jsonPath("$.message", is(MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED)))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

}
