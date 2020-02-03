
package com.fitzhi.bean.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;

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
		assertThat(staff.getFirstName().toLowerCase()).isEqualTo("stéphane");
		assertThat(staff.getLastName().toLowerCase()).isEqualTo("lagrange");
	}

	@Test
	public void lookupWordsLastFistName() {
		Staff staff = this.staffHandler.lookup("Lagrange Stéphane");
		assertThat(staff).isNotNull();
		assertThat(staff.getFirstName().toLowerCase()).isEqualTo("stéphane");
		assertThat(staff.getLastName().toLowerCase()).isEqualTo("lagrange");
		assertThat(staff.getLogin().toLowerCase()).isEqualTo("stlagrange");
	}

	@Test
	public void lookup2WordsFirstLastName() {
		Staff staff = this.staffHandler.lookup("Nobilleau Frederic");
		assertThat(staff).isNotNull();
		assertThat(staff.getFirstName().toLowerCase()).isEqualTo("frederic");
		assertThat(staff.getLastName().toLowerCase()).isEqualTo("nobilleau");
		assertThat(staff.getLogin().toLowerCase()).isEqualTo("fnobilleau");
	}
}
