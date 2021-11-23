package com.fitzhi.bean.impl.DataHandler;

import java.time.LocalDate;
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
 * Test of the method {@link DataHandler#loadSkillsConstellations(LocalDate)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataHandlerLoadSkillsConstellationsTest {
 
	@Autowired
	DataHandler dataHandler;

	@Test (expected = ApplicationException.class)
	public void notfound() throws ApplicationException {
		dataHandler.loadSkillsConstellations(LocalDate.of(1964, 2, 8));
	}

	@Test
	public void load() throws ApplicationException {
		List<Constellation> constellations = dataHandler.loadSkillsConstellations(LocalDate.of(1969, 7, 20));
		Assert.assertNotNull(constellations);
		Assert.assertEquals(1, constellations.get(0).getIdSkill());
		Assert.assertEquals(10, constellations.get(0).getStarsNumber());
		Assert.assertEquals(15, constellations.get(0).getStarsNumberWithExternal());
		Assert.assertEquals(2, constellations.get(1).getIdSkill());
		Assert.assertEquals(20, constellations.get(1).getStarsNumber());
		Assert.assertEquals(25, constellations.get(1).getStarsNumberWithExternal());
	}

}
