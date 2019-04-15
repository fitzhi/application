package fr.skiller.bean.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

	private static final String PROJECT = "project";
	
	private static final String OPERATION_OF_TEST = "operation of test";
	
	@Autowired
	AsyncTask asyncTask;

	@Before
	public void before() throws SkillerException {
		asyncTask.addTask(OPERATION_OF_TEST, PROJECT, 1);		
	}
	
	@Test
	public void test2Add() throws SkillerException {
		try {
			asyncTask.addTask(OPERATION_OF_TEST, PROJECT, 1);
			Assert.fail("addTask should throw a SkillerException");
		} catch (final SkillerException e) {
		}
	}
	
	@Test
	public void testAddAndTest()  {
		Assert.assertTrue(asyncTask.containsTask(OPERATION_OF_TEST, PROJECT, 1));
	}

	@After
	public void after() {
		asyncTask.removeTask(OPERATION_OF_TEST, PROJECT, 1);		
	}
}
