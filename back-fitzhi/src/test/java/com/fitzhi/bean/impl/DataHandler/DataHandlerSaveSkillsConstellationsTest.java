package com.fitzhi.bean.impl.DataHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of the method {@link DataHandler#saveSkillsConstellations(LocalDate, List)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataHandlerSaveSkillsConstellationsTest {
 
	@Autowired
	DataHandler dataHandler;

	@Test
	public void save() throws ApplicationException {
		
		List<Constellation> constellations = new ArrayList<>();
		constellations.add(Constellation.of(11, 1, 2));
		constellations.add(Constellation.of(12, 10, 20));
		
		dataHandler.saveSkillsConstellations(LocalDate.of(2000, 1, 1), constellations);

		constellations = dataHandler.loadSkillsConstellations(LocalDate.of(2000, 1, 1));
		Assert.assertNotNull(constellations);
		Assert.assertEquals(11, constellations.get(0).getIdSkill());
		Assert.assertEquals(1, constellations.get(0).getStarsNumber());
		Assert.assertEquals(2, constellations.get(0).getStarsNumberWithExternal());
		Assert.assertEquals(12, constellations.get(1).getIdSkill());
		Assert.assertEquals(10, constellations.get(1).getStarsNumber());
		Assert.assertEquals(20, constellations.get(1).getStarsNumberWithExternal());
	}

}
