package com.fitzhi.bean.impl.StaffHandler;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * <p>
 * Testing the method {@link StaffHandler#loadConstellations(java.time.LocalDate)},
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerLoadConstellationsTest {

	@Autowired
	StaffHandler staffHandler;
	
	@MockBean
	DataHandler dataHandler;

	@Test(expected = NotFoundException.class)
	public void notfound() throws ApplicationException {
		when(dataHandler.hasAlreadySavedSkillsConstellations(any())).thenReturn(false);
		staffHandler.loadConstellations(LocalDate.now());
	}

	@Test
	public void nominal() throws ApplicationException {
		when(dataHandler.hasAlreadySavedSkillsConstellations(any())).thenReturn(true);

		List<Constellation> constellations = new ArrayList<>();
		constellations.add(Constellation.of(1, 10, 20));
		when(dataHandler.loadSkillsConstellations(any())).thenReturn(constellations);

		List<Constellation> result = staffHandler.loadConstellations(LocalDate.now());
		Assert.assertEquals(result.get(0).getIdSkill(), 1);
		Assert.assertEquals(result.get(0).getStarsNumber(), 10);
		Assert.assertEquals(result.get(0).getStarsNumberWithExternal(), 20);
	}

}
