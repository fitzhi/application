package com.fitzhi.bean.impl.StaffHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.impl.StaffHandlerImpl;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * Test the method {@link StaffHandlerImpl#renumber(com.fitzhi.data.internal.Staff, int)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class StaffHandlerRenumberTest {
	
	@Autowired
	ProjectHandler projectHandler;

	@MockBean
	DataHandler dataHandler;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void test() throws Exception {
		File file = new File("./src/test/resources/slave-save-data/staff.json");
		final BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder realStaff = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
		br.close();

		List<Staff> list = objectMapper.readValue(realStaff.toString(), new TypeReference<List<Staff>>(){});
		Assert.assertEquals(2, list.size());
		Staff staff = list.get(1);
		Assert.assertEquals(1000001, staff.getIdStaff());
		Assert.assertTrue(staff.getMissions().stream().anyMatch(m -> m.getIdStaff() == 1000001));
		Assert.assertTrue(staff.getMissions().stream().flatMap(m -> m.getStaffActivitySkill().values().stream()).anyMatch(sas -> sas.getIdStaff() == 1000001));

		StaffHandlerImpl.renumber(staff, 1789);
		Assert.assertEquals(1789, staff.getIdStaff());
		Assert.assertFalse(staff.getMissions().stream().anyMatch(m -> m.getIdStaff() == 1000001));
		Assert.assertFalse(staff.getMissions().stream().flatMap(m -> m.getStaffActivitySkill().values().stream()).anyMatch(sas -> sas.getIdStaff() == 1000001));
		Assert.assertTrue(staff.getMissions().stream().anyMatch(m -> m.getIdStaff() == 1789));
		Assert.assertTrue(staff.getMissions().stream().flatMap(m -> m.getStaffActivitySkill().values().stream()).anyMatch(sas -> sas.getIdStaff() == 1789));

	}

}
