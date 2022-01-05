/**
 * 
 */
package com.fitzhi.bean.impl.RiskCommit;

import static com.fitzhi.Global.INTERNAL_FILE_SEPARATOR;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.exception.ApplicationException;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskCommitAndDevProcessorMeanTheRiskTest {
	
	private static final String FR_ONE_ONE_A_JAVA = "fr/one/one/A.java";

	private final Logger logger = LoggerFactory.getLogger(RiskCommitAndDevProcessorMeanTheRiskTest.class.getCanonicalName());

	@Autowired
	@Qualifier("commitAndDevActive")
	RiskProcessor riskProcessor;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	StaffHandler staffHandler;

	CommitRepository comRep;
	
	private Staff first = null;
	private Staff second = null;
	private Staff third = null;
	
	
	@Before
	public void before() throws ApplicationException {

		comRep = new BasicCommitRepository();
		
		first = (Staff) staffHandler.getStaff().values().toArray()[0];
		second = (Staff) staffHandler.getStaff().values().toArray()[1];
		third = (Staff) staffHandler.getStaff().values().toArray()[2];
		
		Project prj = new Project(8021964, "testRiskEvaluation");
		projectHandler.addNewProject(prj);

		comRep.addCommit(FR_ONE_ONE_A_JAVA, first.getIdStaff(), "theFirstAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		comRep.addCommit(FR_ONE_ONE_A_JAVA, first.getIdStaff(), "theFirstAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()-1000), 1);
		comRep.addCommit(FR_ONE_ONE_A_JAVA, first.getIdStaff(), "theFirstAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()-2000), 1);
		comRep.addCommit("fr/one/one/B.java", second.getIdStaff(), "theSecondtAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
		comRep.addCommit("fr/one/one/C.java", third.getIdStaff(), "theThirdAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);

		comRep.addCommit("fr/A.java", second.getIdStaff(), "theSecondAuthor", "email@nope.com", new Timestamp(System.currentTimeMillis()), 1);
}

	@Test
	public void agregateCountCommits() {
		first.setActive(false);
		second.setActive(true);
		third.setActive(true);

		RiskCommitAndDevActiveProcessorImpl impl = ((RiskCommitAndDevActiveProcessorImpl) riskProcessor);

		DataChart data = new DataChart("");
		comRep.getRepository().values().stream().forEach(
				commit -> 
				data.injectFile(data, 
						commit.getSourcePath().split(INTERNAL_FILE_SEPARATOR), 
						1,
						commit.evaluateDateLastestCommit(),
						commit.committers()));

		final List<StatActivity> statsCommit = new ArrayList<>();
		impl.evaluateTheRisk(comRep, data, statsCommit);
		
		for (StatActivity stat : statsCommit) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s %d %d", stat.getFilename(), stat.getCountCommits(), stat.getCountCommitsByActiveDevelopers()));
			}
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
	public void after() throws ApplicationException {
		projectHandler.getProjects().remove(8021964);
	}

}
