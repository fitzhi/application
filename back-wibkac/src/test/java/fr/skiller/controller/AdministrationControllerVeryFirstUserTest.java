/**
 * 
 */
package fr.skiller.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.text.AbstractDocument.Content;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import fr.skiller.bean.Administration;
import fr.skiller.bean.StaffHandler;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "allowSelfRegistration=false" }) 
public class AdministrationControllerVeryFirstUserTest {

	private static final String LOGIN = "login";

	private static final String CST_CODE = "$.code";

	private static final String CST_STAFF_ID_STAFF = "$.staff.idStaff";

	private static final String PASS_WORD = "password"; //NOSONAR

	private static String pass = "passvoid";
	
	@Autowired
	private MockMvc mvc;

	/**
	 * Directory where the footprint of the very first solution is made.
	 */
	@Value("${applicationOutDirectory}")
	private String rootLocation;

	@Autowired
	StaffHandler staffHandler;

	@Autowired
	public Administration administration;

	Logger logger = LoggerFactory.getLogger(AdministrationControllerVeryFirstUserTest.class.getCanonicalName());
	
	@Test
	public void creationVeryFirstUserOK() throws Exception {
		
		
		staffHandler.getStaff().clear();
		if (logger.isDebugEnabled()) {
			logger.debug("Staff collection is empty");
		}

		MockHttpServletResponse response = this.mvc.perform(get("/admin/veryFirstUser") //NOSONAR
					.param(LOGIN, "adminForTest") 
					.param(PASS_WORD, "passForTest"))  
				.andExpect(status().isOk())
				.andExpect(jsonPath(CST_STAFF_ID_STAFF, is(1)))
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse();
		
		assertEquals("1", response.getHeader("backend.return_code"));
		

	}


	
}
