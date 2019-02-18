/**
 * 
 */
package fr.skiller.bean.impl;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.RiskProcessor;
import fr.skiller.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskCommitAndDevProcessor_agregateCountCommits_Test {
	
	@Autowired
	@Qualifier("commitAndDevActive")
	RiskProcessor riskProcessor;
	
	@Test
	public void agregateCountCommits() {
		RiskCommitAndDevActiveProcessorImpl impl = ((RiskCommitAndDevActiveProcessorImpl) riskProcessor);
		List<StatActivity> stats = new ArrayList<StatActivity>();
		stats.add( impl.new StatActivity("fr/test/nope/one.java", 6, 2));
		stats.add( impl.new StatActivity("fr/test/nope/two.java", 6, 2));
		stats.add(impl.new StatActivity("fr/test/twice/otro.java", 14, 2));
		stats.add( impl.new StatActivity("fr/test/yop.java", 10, 6));
		Assert.assertEquals(0, impl.agregateCountCommits("fr", stats));
		Assert.assertEquals(10, impl.agregateCountCommits("fr/test", stats));
		Assert.assertEquals(12, impl.agregateCountCommits("fr/test/nope", stats));
		Assert.assertEquals(14, impl.agregateCountCommits("fr/test/twice", stats));
	}

}
