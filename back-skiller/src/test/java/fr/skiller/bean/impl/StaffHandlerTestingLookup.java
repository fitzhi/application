
package fr.skiller.bean.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import javax.validation.constraints.AssertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;

/**
 * Testin the look-up method.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffHandlerTestingLookup {
	
    @Autowired
	private StaffHandler staffHandler;
	
	@SuppressWarnings("null")
	@Test
	public void lookupSimpleWord() {
		
		Staff staff = this.staffHandler.lookup("stlagrange");
		assertThat(staff).isNotNull();
		assertThat(staff.firstName.toLowerCase()).isEqualTo("stéphane");
		assertThat(staff.lastName.toLowerCase()).isEqualTo("lagrange");
	}

	@SuppressWarnings("null")
	@Test
	public void lookup2Words() {
		
		Staff staff = this.staffHandler.lookup("Lagrange Stéphane");
		assertThat(staff).isNotNull();
		assertThat(staff.firstName.toLowerCase()).isEqualTo("stéphane");
		assertThat(staff.lastName.toLowerCase()).isEqualTo("lagrange");
		assertThat(staff.login.toLowerCase()).isEqualTo("stlagrange");
	}

}
