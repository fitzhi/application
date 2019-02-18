/**
 * 
 */
package fr.skiller.bean.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.RiskProcessor;
import fr.skiller.bean.StaffHandler;
import fr.skiller.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.source.scanner.RepoScanner;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
/**
 * Testing the class {@link RiskCommitAndDevActiveProcessorImpl}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskCommitAndDevActiveProcessorTest {
		
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
	
	Staff first = null, second = null, third = null, fourth = null, fifth = null;
	
	Project prj;
	
	@Before
	public void before() throws Exception {
		comRep = new BasicCommitRepository();
		
		first = (Staff) staffHandler.getStaff().values().toArray()[0];
		second = (Staff) staffHandler.getStaff().values().toArray()[1];
		third = (Staff) staffHandler.getStaff().values().toArray()[2];
		fourth = (Staff) staffHandler.getStaff().values().toArray()[3];
		fifth = (Staff) staffHandler.getStaff().values().toArray()[4];
		
		prj = new Project(8021964, "testRiskEvaluation");
		projectHandler.addNewProject(prj);

		comRep.addCommit("fr/one/one/A.java", first.idStaff, new Timestamp(System.currentTimeMillis()));
		comRep.addCommit("fr/one/one/A.java", first.idStaff, new Timestamp(System.currentTimeMillis()-1000));
		comRep.addCommit("fr/one/one/A.java", first.idStaff, new Timestamp(System.currentTimeMillis()-2000));
		comRep.addCommit("fr/one/one/B.java", second.idStaff, new Timestamp(System.currentTimeMillis()));
		comRep.addCommit("fr/one/one/C.java", third.idStaff, new Timestamp(System.currentTimeMillis()));

		comRep.addCommit("fr/one/two/D.java", fourth.idStaff, new Timestamp(System.currentTimeMillis()));
		comRep.addCommit("fr/one/two/E.java", fifth.idStaff, new Timestamp(System.currentTimeMillis()));
		
		comRep.addCommit("fr/two/Z.java", second.idStaff, new Timestamp(System.currentTimeMillis()));
		
		comRep.addCommit("fr/two/one/F.java", first.idStaff, new Timestamp(System.currentTimeMillis()));
		comRep.addCommit("fr/two/one/F.java", second.idStaff, new Timestamp(System.currentTimeMillis()));
		comRep.addCommit("fr/two/one/F.java", second.idStaff, new Timestamp(System.currentTimeMillis()-1000));
		
		comRep.addCommit("fr/two/two/G.java", fourth.idStaff, new Timestamp(System.currentTimeMillis()));
		comRep.addCommit("fr/two/two/G.java", fifth.idStaff, new Timestamp(System.currentTimeMillis()));
		comRep.addCommit("fr/two/two/G.java", fifth.idStaff, new Timestamp(System.currentTimeMillis()-1000));
		comRep.addCommit("fr/two/two/G.java", fifth.idStaff, new Timestamp(System.currentTimeMillis()-2000));
	}
	
	@Test
	public void testAgragreCommitsAllDevActive() throws Exception {
		first.isActive = second.isActive = third.isActive = fourth.isActive = fifth.isActive = true;
		RiskDashboard dash = repoScanner.aggregateDashboard(prj, comRep);
		
		RiskCommitAndDevActiveProcessorImpl impl = (RiskCommitAndDevActiveProcessorImpl) riskProcessor;
		List<StatActivity> stats = new ArrayList<StatActivity>();
		impl.agregateCommits("", comRep, dash.riskChartData, stats);

		List<StatActivity> expected = new ArrayList<StatActivity>();
		expected.add(impl.new StatActivity("root/fr/one/one/A.java", 3, 3));
		expected.add(impl.new StatActivity("root/fr/one/one/B.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/one/C.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/two/D.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/two/E.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/two/Z.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/two/one/F.java", 3, 3));
		expected.add(impl.new StatActivity("root/fr/two/two/G.java", 4, 4));
		
		Collections.sort(stats, (StatActivity sa1, StatActivity sa2) -> sa1.filename.compareTo(sa2.filename));
		Collections.sort(expected, (StatActivity sa1, StatActivity sa2) -> sa1.filename.compareTo(sa2.filename));
		assertThat(stats, is(expected));
	}
	
	@Test
	public void testAgragreCommitsAllDevActiveExceptTheFirstOne() throws Exception {
		first.isActive = false;
		second.isActive = third.isActive = fourth.isActive = fifth.isActive = true;
		RiskDashboard dash = repoScanner.aggregateDashboard(prj, comRep);
		
		RiskCommitAndDevActiveProcessorImpl impl = (RiskCommitAndDevActiveProcessorImpl) riskProcessor;
		List<StatActivity> stats = new ArrayList<StatActivity>();
		impl.agregateCommits("", comRep, dash.riskChartData, stats);

		List<StatActivity> expected = new ArrayList<StatActivity>();
		expected.add(impl.new StatActivity("root/fr/one/one/A.java", 3, 0));
		expected.add(impl.new StatActivity("root/fr/one/one/B.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/one/C.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/two/D.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/one/two/E.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/two/Z.java", 1, 1));
		expected.add(impl.new StatActivity("root/fr/two/one/F.java", 3, 2));
		expected.add(impl.new StatActivity("root/fr/two/two/G.java", 4, 4));
		
		Collections.sort(stats, (StatActivity sa1, StatActivity sa2) -> sa1.filename.compareTo(sa2.filename));
		Collections.sort(expected, (StatActivity sa1, StatActivity sa2) -> sa1.filename.compareTo(sa2.filename));
		assertThat(stats, is(expected));
	}

	@Test
	public void testEvaluateActiveDevelopersCorverage() throws Exception {
		first.isActive = false;
		second.isActive = third.isActive = fourth.isActive = fifth.isActive = true;

		RiskDashboard dash = repoScanner.aggregateDashboard(prj, comRep);
		
		RiskCommitAndDevActiveProcessorImpl impl = (RiskCommitAndDevActiveProcessorImpl) riskProcessor;
		List<StatActivity> stats = new ArrayList<StatActivity>();
		impl.agregateCommits("", comRep, dash.riskChartData, stats);
		
		impl.evaluateActiveDevelopersCoverage("", dash.riskChartData, stats);
	}
	
	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(8021964);
	}
	
}
