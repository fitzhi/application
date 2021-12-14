package com.fitzhi.bean.impl.DataHandler;

import java.time.LocalDate;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of the method {@link DataHandler#hasAlreadySavedSkillsConstellations(java.time.LocalDate)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataHandlerHasAlreadySavedSkillsConstellationsTest {
 
	@Autowired
	DataHandler dataHandler;

	@Test
	public void testNotfound() throws ApplicationException {
		Assert.assertFalse(dataHandler.hasAlreadySavedSkillsConstellations(LocalDate.of(1964, 2, 8)));
	}

	@Test
	public void testfound() throws ApplicationException {
		Assert.assertTrue(dataHandler.hasAlreadySavedSkillsConstellations(LocalDate.of(1969, 7, 20)));
	}

}
