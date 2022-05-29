package com.fitzhi.bean.impl.DataHandler;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.impl.HttpDataHandlerImpl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of some methods of {@link HttpDataHandlerImpl}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {"applicationUrl=http://mock-url", "organization=fitzhi" })
public class HttpDataHandlerApplicationTest {
 
	@Autowired
	DataHandler dataHandler;

	@Test
	public void testIsLocal() {
		Assert.assertTrue(dataHandler.isLocal());
	}

}
