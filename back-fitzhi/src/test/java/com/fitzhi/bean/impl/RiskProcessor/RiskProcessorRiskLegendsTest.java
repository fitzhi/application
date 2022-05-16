package com.fitzhi.bean.impl.RiskProcessor;

import com.fitzhi.bean.RiskProcessor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p> Test of the method {@link RiskProcessor#riskLegends()} </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskProcessorRiskLegendsTest {
	
	@Autowired
	RiskProcessor riskProcessor;

	@Test
	public void nominal() {
		Assert.assertEquals(11, riskProcessor.riskLegends().values().size());
		Assert.assertTrue(riskProcessor.riskLegends().containsKey(0));
		Assert.assertTrue(riskProcessor.riskLegends().containsKey(10));
	}
}
