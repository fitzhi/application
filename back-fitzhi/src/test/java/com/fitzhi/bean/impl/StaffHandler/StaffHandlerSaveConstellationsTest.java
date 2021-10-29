package com.fitzhi.bean.impl.StaffHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Test of the method {@link StaffHandler#saveSkillsConstellations}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerSaveConstellationsTest {

	@Autowired
	private StaffHandler staffHandler;
	
	@MockBean
	private DataHandler dataHandler;

	@Before
	public void before() {
		
		Staff s1 = new Staff(10001, "firstName 1", "lastName 1", "nickName 1", "login 1", "email 1", "ICD 1");
		s1.setActive(true);
		s1.setExternal(false);
		s1.getExperiences().add(new Experience (1,  4));
		s1.getExperiences().add(new Experience (2,  5));
		
		Staff s2 = new Staff(10002, "firstName 2", "lastName 2", "nickName 2", "login 2", "email 2", "ICD 2");
		s2.setExternal(true);
		s2.setActive(true);
		s2.getExperiences().add(new Experience (1,  2));
		s2.getExperiences().add(new Experience (2,  2));
		s2.getExperiences().add(new Experience (3,  2));

		Staff s3 = new Staff(10003, "firstName 3", "lastName 3", "nickName 3", "login 3", "email 3", "ICD 3");
		s3.setActive(false);
		s3.getExperiences().add(new Experience (1, 5));
		s3.getExperiences().add(new Experience (2, 5));
		s3.getExperiences().add(new Experience (4, 5));
		
		Staff s4 = new Staff(10001, "firstName 4", "lastName 4", "nickName 4", "login 4", "email 4", "ICD 4");
		s1.setActive(true);
		s1.setExternal(false);
		s1.getExperiences().add(new Experience (1,  1));
		s1.getExperiences().add(new Experience (2,  1));

		staffHandler.put(10001, s1);
		staffHandler.put(10002, s2);
		staffHandler.put(10003, s3);
		staffHandler.put(10003, s4);
	}

	@Test
	public void alreadySaved() throws ApplicationException {
		when(dataHandler.hasAlreadySavedSkillsConstellations(any(LocalDate.class))).thenReturn(true);
		staffHandler.saveCurrentConstellations();
		verify(dataHandler, times(0)).saveSkillsConstellations(any(LocalDate.class), Mockito.<Constellation>anyList());
	}

	@Test
	public void processAndSave() throws ApplicationException {
		when(dataHandler.hasAlreadySavedSkillsConstellations(any(LocalDate.class))).thenReturn(false);
		doNothing().when(dataHandler).saveSkillsConstellations(any(LocalDate.class), Mockito.<Constellation>anyList());
		staffHandler.saveCurrentConstellations();
		LocalDate currentMonth = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);

		List<Constellation> constellations = new ArrayList<>();
		constellations.add(Constellation.of(1, 5, 7));
		constellations.add(Constellation.of(2, 6, 8));
		constellations.add(Constellation.of(3, 0, 2));

		verify(dataHandler, times(1)).saveSkillsConstellations(currentMonth, constellations);
	}

}