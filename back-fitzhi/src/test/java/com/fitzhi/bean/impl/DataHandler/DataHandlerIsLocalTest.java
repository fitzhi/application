package com.fitzhi.bean.impl.DataHandler;

import com.fitzhi.bean.DataHandler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of the method {@link DataHandler#isLocal())}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataHandlerIsLocalTest {
 
	@Autowired
	DataHandler dataHandler;

	@Test
	public void save() throws Exception {
		Assert.assertTrue(dataHandler.isLocal());
	}
}
