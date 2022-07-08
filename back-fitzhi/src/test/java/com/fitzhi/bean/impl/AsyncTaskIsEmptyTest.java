package com.fitzhi.bean.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.exception.ApplicationException;

/**
 * Test of the method {@link AsyncTaskImpl#isEmpty()}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyncTaskIsEmptyTest {

	@Autowired
	@Qualifier("default")
	AsyncTask asyncTask;

	@Test 
	public void testIsEmpty() {
		Assert.assertTrue(asyncTask.isEmpty());
	}

	@Test 
	public void testIsNonEmpty() throws ApplicationException {
		asyncTask.addTask("nope", "nope", 1789);
		Assert.assertFalse(asyncTask.isEmpty());
		asyncTask.removeTask("nope", "nope", 1789);
	}

}
