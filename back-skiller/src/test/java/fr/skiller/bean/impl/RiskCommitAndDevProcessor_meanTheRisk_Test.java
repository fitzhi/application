/**
 * 
 */
package fr.skiller.bean.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskCommitAndDevProcessor_meanTheRisk_Test {
	
	@Autowired
	@Qualifier("commitAndDevActive")
	RiskProcessor riskProcessor;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	StaffHandler staffHandler;

	CommitRepository comRep;
	
	private Staff first = null, second = null, third = null;
	
	private Project prj;
	
	@Before
	public void before() throws Exception {

		comRep = new BasicCommitRepository();
		
		first = (Staff) staffHandler.getStaff().values().toArray()[0];
		second = (Staff) staffHandler.getStaff().values().toArray()[1];
		third = (Staff) staffHandler.getStaff().values().toArray()[2];
		
		prj = new Project(8021964, "testRiskEvaluation");
		projectHandler.addNewProject(prj);

		comRep.addCommit("fr/one/one/A.java", first.getIdStaff(), new Timestamp(System.currentTimeMillis()));
		comRep.addCommit("fr/one/one/A.java", first.getIdStaff(), new Timestamp(System.currentTimeMillis()-1000));
		comRep.addCommit("fr/one/one/A.java", first.getIdStaff(), new Timestamp(System.currentTimeMillis()-2000));
		comRep.addCommit("fr/one/one/B.java", second.getIdStaff(), new Timestamp(System.currentTimeMillis()));
		comRep.addCommit("fr/one/one/C.java", third.getIdStaff(), new Timestamp(System.currentTimeMillis()));

		comRep.addCommit("fr/A.java", second.getIdStaff(), new Timestamp(System.currentTimeMillis()));
}

	@Test
	public void agregateCountCommits() {
		first.setActive(false);
		second.setActive(true);
		third.setActive(true);

		RiskCommitAndDevActiveProcessorImpl impl = ((RiskCommitAndDevActiveProcessorImpl) riskProcessor);

		RiskChartData data = new RiskChartData("");
		comRep.getRepository().values().stream().forEach(
				commit -> 
				data.injectFile(data, 
						commit.sourcePath.split(File.separator), 
						commit.evaluateDateLastestCommit(),
						commit.committers()));

		final List<StatActivity> statsCommit = new ArrayList<StatActivity>();
		impl.evaluateTheRisk(comRep, data, statsCommit);
		
		for (StatActivity stat : statsCommit) {
			System.out.println(stat.getFilename() + " " + stat.getCountCommits() + " " + stat.getCountCommitsByActiveDevelopers());
		}
		
		Assert.assertEquals(-1, data.getRiskLevel());
		Assert.assertEquals(0, data.getChildren().get(0).getRiskLevel());
		Assert.assertEquals(-1, data.getChildren().get(0).getChildren().get(0).getRiskLevel());
		Assert.assertEquals(6, data.getChildren().get(0).getChildren().get(0).getChildren().get(0).getRiskLevel());
		
		impl.meanTheRisk(data);

		Assert.assertEquals(3, data.getRiskLevel());
		Assert.assertEquals(3, data.getChildren().get(0).getRiskLevel());
		Assert.assertEquals(6, data.getChildren().get(0).getChildren().get(0).getRiskLevel());
		Assert.assertEquals(6, data.getChildren().get(0).getChildren().get(0).getChildren().get(0).getRiskLevel());
		
	}

	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(8021964);
	}

}
