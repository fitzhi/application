package com.fitzhi.bean.impl.StaffHandler;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

/**
 * Test of method {@link StaffHandler#updateStaffAfterAnalysis(com.fitzhi.data.internal.Project, java.util.List)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerUpdateStaffAfterAnalysisTest {

	@Autowired
	private StaffHandler staffHandler;

	private Map<Integer, Staff> company() {
		Staff staff = new Staff(1000,"firstName", "lastName", "nick" , "loginWhaoo", "email@void.com", "level1");
		staff.addMission(
			new Mission(1000, 312, "Milvius", LocalDate.of(2022, 6, 1), LocalDate.of(2022, 7, 2), 1, 10));
		staff.addMission(
			new Mission(1000, 331, "Gaugamèles", LocalDate.now(), LocalDate.now(), 1, 10));

		Map<Integer, Staff> map = new HashMap<>();
		map.put(1000, staff);

		staff = new Staff(1001,"anotherFirstName", "anotherLastName", "nick2" , "loginWhaoo2", "email2@void.com", "level2");
		map.put(1001, staff);

		return map;
	}

	@Test
	public void updateStaff() throws ApplicationException {
		StaffHandler spy = spy(staffHandler);
		spy.init();
		when(spy.getStaff()).thenReturn(company());

		Staff staff = new Staff(1000);
		staff.addMission(new Mission(1000, 312, "Milvius", LocalDate.of(2022, 6, 1), LocalDate.of(2022, 7, 2), 3,  300));
		List<Staff> list = new ArrayList<>();
		list.add(staff);

		Project project = new Project(312, "Milvius battle");
		spy.updateStaffAfterAnalysis(project, list);

		List<Mission> missions = spy.getStaff(1000).getMissions();
		Assert.assertEquals(2, missions.size());
		Mission mission = missions.stream().filter(m -> m.getIdProject() == 312).findFirst().get();
		Assert.assertNotNull(mission);
		Assert.assertEquals(3, mission.getNumberOfCommits());
		Assert.assertEquals(300, mission.getNumberOfFiles());
	}

	@Test
	public void addNewStaff() throws ApplicationException {
		StaffHandler spy = spy(staffHandler);
		spy.init();
		when(spy.getStaff()).thenReturn(company());
		// We save the next ID To come. This ID will be used by the imported staff.
		int nextId = spy.nextIdStaff();

		Staff staff = new Staff(1000);
		staff.addMission(new Mission(1000, 312, "Milvius", LocalDate.of(2022, 6, 1), LocalDate.of(2022, 7, 2), 3,  300));
		List<Staff> list = new ArrayList<>();
		list.add(staff);
		staff = new Staff(StaffHandler.SLAVE_OFFSET+1, "brandNewFirstName", "brandNewFirstNameLastName", "newNickname" , "newLogin", "new_email@void.com", "levelNew");
		staff.addMission(new Mission(StaffHandler.SLAVE_OFFSET+1, 312, "Milvius", LocalDate.of(2021, 6, 1), LocalDate.of(2021, 7, 2), 7, 777));
		list.add(staff);

		Project project = new Project(312, "Milvius battle");
		spy.updateStaffAfterAnalysis(project, list);

		Map<Integer, Staff> company = spy.getStaff();
		Assert.assertTrue(company.containsKey(nextId));
		staff = company.get(nextId);
		Assert.assertEquals(1, staff.getMissions().size());
		Assert.assertEquals(7, staff.getMissions().get(0).getNumberOfCommits());
		Assert.assertEquals(777, staff.getMissions().get(0).getNumberOfFiles());
	}

}
