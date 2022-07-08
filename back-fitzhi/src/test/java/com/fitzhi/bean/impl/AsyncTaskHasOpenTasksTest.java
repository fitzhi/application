package com.fitzhi.bean.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.data.internal.Task;
import com.fitzhi.exception.ApplicationException;

/**
 * Test of the method {@link AsyncTaskImpl#isEmpty()}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyncTaskHasOpenTasksTest {

	@Autowired
	@Qualifier("default")
	AsyncTask asyncTask;

	@Test 
	public void hasNoTask() {
		Assert.assertFalse(asyncTask.hasOpenTasks());
	}

	@Test 
	public void hasNoActiveTask() throws ApplicationException {
		asyncTask.addTask("nope", "nope", 1789);
		Task t = asyncTask.getTask("nope", "nope", 1789);
		t.setComplete(true);
		Assert.assertFalse(asyncTask.hasOpenTasks());
	}

	@Test 
	public void hasOpenTasks() throws ApplicationException {
		asyncTask.addTask("nope", "nope", 1789);
		Assert.assertTrue(asyncTask.hasOpenTasks());
		asyncTask.removeTask("nope", "nope", 1789);
	}

}
