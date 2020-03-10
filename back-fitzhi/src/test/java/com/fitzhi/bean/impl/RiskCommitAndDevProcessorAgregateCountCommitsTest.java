/**
 * 
 */
package com.fitzhi.bean.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskCommitAndDevProcessorAgregateCountCommitsTest {
	
	@Autowired
	@Qualifier("commitAndDevActive")
	RiskProcessor riskProcessor;
	
	@Test
	public void agregateCountCommits() {
		RiskCommitAndDevActiveProcessorImpl impl = ((RiskCommitAndDevActiveProcessorImpl) riskProcessor);
		List<StatActivity> stats = new ArrayList<>();
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
