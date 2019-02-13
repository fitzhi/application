package fr.skiller.bean.impl;

import org.apache.poi.poifs.crypt.dsig.ExpiredCertificateSecurityException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties.Whitelabel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.AsyncTask;
import fr.skiller.exception.SkillerException;

/**
 * Testing the class AsyncTaskImpl
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyncTaskTest {

	@Autowired
	AsyncTask asyncTask;

	@Before
	public void before() throws Exception {
		asyncTask.addTask("operation of test", "project", 1);		
	}
	
	@Test
	public void test2Add() throws Exception {
		try {
			asyncTask.addTask("operation of test", "project", 1);
			Assert.fail("addTask should throw a SkillerException");
		} catch (final SkillerException e) {
		}
	}
	
	@Test
	public void testAddAndTest() throws Exception {
		Assert.assertTrue(asyncTask.containsTask("operation of test", "project", 1));
	}

	@After
	public void after() {
		asyncTask.removeTask("operation of test", "project", 1);		
	}
}
