
package com.fitzhi.bean.impl.StaffHandler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.ApplicationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testinh the method {@ling StaffHandler#involve(com.fitzhi.data.internal.Project, java.util.List)}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerInvolveTest {
	
	@Autowired
	private StaffHandler staffHandler;
	
	@MockBean
	DataHandler dataHandler;

	@Test
	public void noErrorWithEmptyList() throws ApplicationException {
		Project project = new Project(1789, "The revolutionary project");
		List<Contributor> contributors = new ArrayList<>();
		staffHandler.involve(project, contributors);
	}

	@Test
	public void addMission() throws ApplicationException {
		Map<Integer, Staff> staffs = new HashMap<>();
		Staff st = new Staff(1802, "Napoleon", "Bonaparte", "l'emprereur", "l'empereur", "noemail", "Big level");
		staffs.put(1802, st);
		when(dataHandler.loadStaff()).thenReturn(staffs);
		staffHandler.getStaff().put(1802, st);

		Project project = new Project(1789, "The revolutionary project");
		List<Contributor> contributors = new ArrayList<>();
		contributors.add(new Contributor(1802, LocalDate.of(1802, 12, 1), LocalDate.of(1802, 12, 2), 2, 5));
		staffHandler.involve(project, contributors);
		Staff staff = staffHandler.getStaff(1802);
		assertEquals(1, staff.getMissions().size());
		assertEquals(1789, staff.getMissions().get(0).getIdProject());
		assertEquals(LocalDate.of(1802, 12, 1), staff.getMissions().get(0).getFirstCommit());
		assertEquals(LocalDate.of(1802, 12, 2), staff.getMissions().get(0).getLastCommit());
	}

	@Test
	public void updateMission() throws ApplicationException {
		Map<Integer, Staff> staffs = new HashMap<>();
		Staff st = new Staff(1802, "Napoleon", "Bonaparte", "l'emprereur", "l'empereur", "noemail", "Big level");
		staffs.put(1802, st);
		staffHandler.getStaff().put(1802, st);
   
		when(dataHandler.loadStaff()).thenReturn(staffs);
		Project project = new Project(1789, "The revolutionary project");
		// We add the mission of reference to be updated. 
		List<Contributor> contributors = new ArrayList<>();
		contributors.add(new Contributor(1802, LocalDate.of(1802, 12, 1), LocalDate.of(1802, 12, 2), 2, 5));
		staffHandler.involve(project, contributors);

		contributors = new ArrayList<>();
		contributors.add(new Contributor(1802, LocalDate.of(1802, 12, 3), LocalDate.of(1802, 12, 4), 5, 15));
		staffHandler.involve(project, contributors);
		Staff staff = staffHandler.getStaff(1802);
		assertEquals(1, staff.getMissions().size());
		assertEquals(1789, staff.getMissions().get(0).getIdProject());
		assertEquals(LocalDate.of(1802, 12, 3), staff.getMissions().get(0).getFirstCommit());
		assertEquals(LocalDate.of(1802, 12, 4), staff.getMissions().get(0).getLastCommit());
	}

}
