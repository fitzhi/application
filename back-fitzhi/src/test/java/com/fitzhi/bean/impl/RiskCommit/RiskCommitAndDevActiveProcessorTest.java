package com.fitzhi.bean.impl.RiskCommit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
/**
 * Testing the class {@link RiskCommitAndDevActiveProcessorImpl}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RiskCommitAndDevActiveProcessorTest {
		
	private static final String FR_TWO_TWO_G_JAVA = "fr/two/two/G.java";

	private static final String FR_TWO_ONE_F_JAVA = "fr/two/one/F.java";

	private static final String FR_ONE_ONE_A_JAVA = "fr/one/one/A.java";

	@Autowired
	StaffHandler staffHandler;
		
	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	@Qualifier("commitAndDevActive")
	RiskProcessor riskProcessor;
	
	@Autowired
	@Qualifier("GIT")
	RepoScanner repoScanner;
	
	CommitRepository comRep;
	
	Staff first = null;
	Staff second = null;
	Staff third = null; 
	Staff fourth = null;
	Staff fifth = null;
	
	Project prj;
	
	@Before
	public void before() throws ApplicationException {
		comRep = new BasicCommitRepository();
		
		staffHandler.getStaff().values().stream().map(Staff::fullName).forEach(log::debug);

		first = (Staff) staffHandler.getStaff().values().toArray()[0];
		second = (Staff) staffHandler.getStaff().values().toArray()[1];
		third = (Staff) staffHandler.getStaff().values().toArray()[2];
		fourth = (Staff) staffHandler.getStaff().values().toArray()[3];
		fifth = (Staff) staffHandler.getStaff().values().toArray()[4];
		
		prj = new Project(8021964, "testRiskEvaluation");
		projectHandler.addNewProject(prj);

		comRep.addCommit(FR_ONE_ONE_A_JAVA, first.getIdStaff(), "thefirstAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		comRep.addCommit(FR_ONE_ONE_A_JAVA, first.getIdStaff(), "thefirstAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()-1000), 1);
		comRep.addCommit(FR_ONE_ONE_A_JAVA, first.getIdStaff(), "thefirstAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()-2000), 1);
		comRep.addCommit("fr/one/one/B.java", second.getIdStaff(), "thesecondAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		comRep.addCommit("fr/one/one/C.java", third.getIdStaff(), "theThirdAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);

		comRep.addCommit("fr/one/two/D.java", fourth.getIdStaff(), "theFourthAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		comRep.addCommit("fr/one/two/E.java", fifth.getIdStaff(), "theFifthAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		
		comRep.addCommit("fr/two/Z.java", second.getIdStaff(), "thesecondAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		
		comRep.addCommit(FR_TWO_ONE_F_JAVA, first.getIdStaff(), "thefirstAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		comRep.addCommit(FR_TWO_ONE_F_JAVA, second.getIdStaff(), "thesecondAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		comRep.addCommit(FR_TWO_ONE_F_JAVA, second.getIdStaff(), "thesecondAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()-1000), 1);
		
		comRep.addCommit(FR_TWO_TWO_G_JAVA, fourth.getIdStaff(), "theFourthAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		comRep.addCommit(FR_TWO_TWO_G_JAVA, fifth.getIdStaff(), "theFifthAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		comRep.addCommit(FR_TWO_TWO_G_JAVA, fifth.getIdStaff(), "theFifthAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()-1000), 1);
		comRep.addCommit(FR_TWO_TWO_G_JAVA, fifth.getIdStaff(), "theFifthAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()-2000), 1);
	}
	
	@Test
	public void testAgragreCommitsAllDevActive() {
		first.setActive(true);
		second.setActive(true);
		third.setActive(true);
		fourth.setActive(true);
		fifth.setActive(true);

		RiskDashboard dash = repoScanner.aggregateDashboard(prj, comRep);
		
		RiskCommitAndDevActiveProcessorImpl impl = (RiskCommitAndDevActiveProcessorImpl) riskProcessor;
		List<StatActivity> stats = new ArrayList<>();
		impl.agregateCommits("", comRep, dash.riskChartData, stats);

		List<StatActivity> expected = new ArrayList<>();
		expected.add(impl.new StatActivity("root/fr/one/one/A.java", 3, 3));
		expected.add(impl.new StatActivity("root/fr/one/one/B.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/one/C.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/two/D.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/two/E.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/two/Z.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/two/one/F.java", 3, 3));
		expected.add(impl.new StatActivity("root/fr/two/two/G.java", 4, 4));
		
		Collections.sort(stats, (StatActivity sa1, StatActivity sa2) -> sa1.getFilename().compareTo(sa2.getFilename()));
		Collections.sort(expected, (StatActivity sa1, StatActivity sa2) -> sa1.getFilename().compareTo(sa2.getFilename()));
		Assert.assertArrayEquals(expected.toArray(), stats.toArray());
	}
	
	@Test
	public void testAgragreCommitsAllDevActiveExceptTheFirstOne()  {
		first.setActive(false);
		second.setActive(true);
		third.setActive(true);
		fourth.setActive(true);
		fifth.setActive(true);
		RiskDashboard dash = repoScanner.aggregateDashboard(prj, comRep);
		
		RiskCommitAndDevActiveProcessorImpl impl = (RiskCommitAndDevActiveProcessorImpl) riskProcessor;
		List<StatActivity> stats = new ArrayList<>();
		impl.agregateCommits("", comRep, dash.riskChartData, stats);

		List<StatActivity> expected = new ArrayList<>();
		expected.add(impl.new StatActivity("root/fr/one/one/A.java", 3, 0));
		expected.add(impl.new StatActivity("root/fr/one/one/B.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/one/C.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/two/D.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/two/E.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/two/Z.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/two/one/F.java", 3, 2));
		expected.add(impl.new StatActivity("root/fr/two/two/G.java", 4, 4));
		
		Collections.sort(stats, (StatActivity sa1, StatActivity sa2) -> sa1.getFilename().compareTo(sa2.getFilename()));
		Collections.sort(expected, (StatActivity sa1, StatActivity sa2) -> sa1.getFilename().compareTo(sa2.getFilename()));
		Assert.assertArrayEquals(expected.toArray(), stats.toArray());
	}

	@Test
	public void testEvaluateActiveDevelopersCoverage()  {
		first.setActive(false);
		second.setActive(true);
		third.setActive(true);
		fourth.setActive(true);
		fifth.setActive(true);

		RiskDashboard dash = repoScanner.aggregateDashboard(prj, comRep);
		
		RiskCommitAndDevActiveProcessorImpl impl = (RiskCommitAndDevActiveProcessorImpl) riskProcessor;
		List<StatActivity> stats = new ArrayList<>();
		impl.agregateCommits("", comRep, dash.riskChartData, stats);
		
		impl.evaluateActiveDevelopersCoverage("", dash.riskChartData, stats);
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.getProjects().remove(8021964);
	}
	
}
