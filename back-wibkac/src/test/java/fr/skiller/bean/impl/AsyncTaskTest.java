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
import fr.skiller.data.internal.Task;
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
	
	@Test (expected = SkillerException.class)
	public void test2TasksAdd() throws SkillerException {
		asyncTask.addTask(OPERATION_OF_TEST, PROJECT, 1);
	}
	
	@Test
	public void testAddTaskAfterFormerCompletion() throws InterruptedException, SkillerException {
		// We log 2 messages
		asyncTask.logMessage(OPERATION_OF_TEST, PROJECT, 1, "first Message");
		Thread.sleep(2);
		asyncTask.logMessage(OPERATION_OF_TEST, PROJECT, 1, 404, "Not found Message");
		
		asyncTask.completeTask(OPERATION_OF_TEST, PROJECT, 1);

		asyncTask.addTask(OPERATION_OF_TEST, PROJECT, 1);
		
		Task t = asyncTask.getTask(OPERATION_OF_TEST, PROJECT, 1);
		Assert.assertEquals("After fresh start, the log is clear", 0, t.getActivityLogs().size());
		Assert.assertTrue(!t.isComplete());
		Assert.assertNull(t.getLastBreath());
		
	}
	
	@Test
	public void testAddNominalAndTest()  {
		Assert.assertTrue(asyncTask.containsTask(OPERATION_OF_TEST, PROJECT, 1));
	}

	@Test
	public void testAddAndCompleteTheTask() throws InterruptedException, SkillerException  {
		
		// We log 2 messages
		asyncTask.logMessage(OPERATION_OF_TEST, PROJECT, 1, "first Message");
		Thread.sleep(2);
		asyncTask.logMessage(OPERATION_OF_TEST, PROJECT, 1, 404, "Not found Message");
		
		// We control the fact that this task is active
		Task t = asyncTask.getTask(OPERATION_OF_TEST, PROJECT, 1);
		Assert.assertFalse(t.isComplete());
		Assert.assertNull(t.getLastBreath());
		Assert.assertEquals("2 logs message", 2, t.getActivityLogs().size());
		
		// We task is completed, we check the resulting states.
		asyncTask.completeTask(OPERATION_OF_TEST, PROJECT, 1);
		t = asyncTask.getTask(OPERATION_OF_TEST, PROJECT, 1);
		Assert.assertEquals("After completion, the log is clear", 0, t.getActivityLogs().size());
		Assert.assertTrue(t.isComplete());
		Assert.assertNotNull(t.getLastBreath());
		Assert.assertEquals("last log inside last breath", 404, t.getLastBreath().getCode());
		
	}

	@Test
	public void testHasActiveTask() throws SkillerException {
		Assert.assertFalse(asyncTask.hasActiveTask("ABSOLUTLY NEW OPERATION OF TEST", PROJECT, 1));
		Assert.assertTrue(asyncTask.hasActiveTask(OPERATION_OF_TEST, PROJECT, 1));
		asyncTask.completeTask(OPERATION_OF_TEST, PROJECT, 1);
		Assert.assertFalse(asyncTask.hasActiveTask(OPERATION_OF_TEST, PROJECT, 1));
	}
	
	
	@After
	public void after() {
		asyncTask.removeTask(OPERATION_OF_TEST, PROJECT, 1);		
	}
}
