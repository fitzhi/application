
package fr.skiller.bean.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;

/**
 * Testin the look-up method.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerTestingLookup {
	
    @Autowired
	private StaffHandler staffHandler;
	
	@Test
	public void lookupSimpleWord() {
		Staff staff = this.staffHandler.lookup("stlagrange");
		assertThat(staff).isNotNull();
		assertThat(staff.firstName.toLowerCase()).isEqualTo("stéphane");
		assertThat(staff.lastName.toLowerCase()).isEqualTo("lagrange");
	}

	@Test
	public void lookupWords_LastFistName() {
		Staff staff = this.staffHandler.lookup("Lagrange Stéphane");
		assertThat(staff).isNotNull();
		assertThat(staff.firstName.toLowerCase()).isEqualTo("stéphane");
		assertThat(staff.lastName.toLowerCase()).isEqualTo("lagrange");
		assertThat(staff.login.toLowerCase()).isEqualTo("stlagrange");
	}

	@Test
	public void lookup2Words_FirstLastName() {
		Staff staff = this.staffHandler.lookup("Nobilleau Frederic");
		assertThat(staff).isNotNull();
		assertThat(staff.firstName.toLowerCase()).isEqualTo("frederic");
		assertThat(staff.lastName.toLowerCase()).isEqualTo("nobilleau");
		assertThat(staff.login.toLowerCase()).isEqualTo("fnobilleau");
	}
}
