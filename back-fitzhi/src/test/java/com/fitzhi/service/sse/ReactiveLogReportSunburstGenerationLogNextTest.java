package com.fitzhi.service.sse;

import static com.fitzhi.Global.PROJECT;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.external.ActivityLog;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.TaskLog;
import com.fitzhi.exception.ApplicationException;

import reactor.test.StepVerifier;

/**
 * <p>
 * Test of method {@link ReactiveLogReport#sunburstGenerationLogNext(String, int)}
 * </p>
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@DirtiesContext(classMode=ClassMode.BEFORE_CLASS)
public class ReactiveLogReportSunburstGenerationLogNextTest {

	/**
	 * PROJECT IDENTIFIER
	 */
	final int ID_PROJECT = 1789;

	@Autowired
	private ProjectHandler projectHandler;
	
	@Autowired
	@Qualifier("default")
	AsyncTask asyncTask;

	@Autowired
	ReactiveLogReport logReport;
	
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	// End of operation marker.
	private String MARK_END_OF_OPERATION = "end of operation";
	
	ActivityLog activityLog1, activityLog2, activityLogEnd;

	private void eraseTime() {
		asyncTask.getTask("nopeOperation", PROJECT, ID_PROJECT).getLastestLog().setLogTime(0);
	}
	
	@Before
	public void before() throws ApplicationException {
		Project p = new Project (ID_PROJECT, "Revolutionnary project");
		projectHandler.addNewProject(p);
		asyncTask.addTask("nopeOperation", PROJECT, ID_PROJECT);
		asyncTask.logMessage("nopeOperation", PROJECT, ID_PROJECT, "my first message", 0);
		this.eraseTime();
		this.activityLog1 = new ActivityLog(ID_PROJECT, new TaskLog( 0, "my first message", 0, 0), false);
		this.activityLog2 = new ActivityLog(ID_PROJECT, new TaskLog(0, "my second message", 0, 0), false);
		this.activityLogEnd = new ActivityLog(ID_PROJECT, new TaskLog(0, "my second message", 100, 0), true);
		
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				asyncTask.logMessage("nopeOperation", PROJECT, ID_PROJECT, "my second message", 0);
				ReactiveLogReportSunburstGenerationLogNextTest.this.eraseTime();				
			}
		}, 3, TimeUnit.SECONDS);

		
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					asyncTask.completeTask("nopeOperation", MARK_END_OF_OPERATION, ID_PROJECT);
					ReactiveLogReportSunburstGenerationLogNextTest.this.eraseTime();				
				} catch (ApplicationException e) {
					log.error("Internal error", e);
				}
			}
		}, 4, TimeUnit.SECONDS);
	}

	@Test
	public void test() {
		StepVerifier.create(logReport.sunburstGenerationLogNext("nopeOperation", ID_PROJECT)) 
				.expectNext(this.activityLog1) 
				.expectNext(this.activityLog2)
				.expectNext(this.activityLogEnd)
				.expectComplete()
				.verify(Duration.ofSeconds(10));
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(ID_PROJECT);
		asyncTask.removeTask("nopeOperation", "mockProject", 1789);		
	}
	
}
