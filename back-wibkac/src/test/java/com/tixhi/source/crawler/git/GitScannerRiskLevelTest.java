package com.tixhi.source.crawler.git;

import static com.tixhi.Global.UNKNOWN;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.tixhi.bean.RiskProcessor;
import com.tixhi.data.internal.DataChart;

/**
 * Test the level of risk evaluation tool.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GitScannerRiskLevelTest {
	
	/**
	 * Risk evaluation processor
	 */
	@Autowired
	@Qualifier("messOfCriteria")
	RiskProcessor riskSurveyor;
	
	@Test
	public void testFillTheHoles1() {
		DataChart data = new DataChart("A");
		data.setRiskLevel(1);
		
		data.addSubDir(new DataChart("AA").setRiskLevel(2));
		DataChart subData = new DataChart("AB").setRiskLevel(UNKNOWN);
		data.addSubDir(subData);
		data.addSubDir(new DataChart("AC").setRiskLevel(1));
		
		subData.addSubDir(new DataChart("ABA").setRiskLevel(1));
		subData.addSubDir(new DataChart("ABB").setRiskLevel(5));
		subData.addSubDir(new DataChart("ABC").setRiskLevel(10));
		
		riskSurveyor.meanTheRisk(subData);
		
		subData = data.getChildren().get(1);
		Assert.assertEquals("AB", subData.getLocation());
		Assert.assertEquals(5, subData.getRiskLevel());
	}

	@Test
	public void testFillTheHoles2() {
		DataChart data = new DataChart("A");
		data.setRiskLevel(1);
		
		data.addSubDir(new DataChart("AA").setRiskLevel(2));
		DataChart subData = new DataChart("AB").setRiskLevel(UNKNOWN);
		data.addSubDir(subData);
		data.addSubDir(new DataChart("AC").setRiskLevel(1));
		
		subData.addSubDir(new DataChart("ABA").setRiskLevel(1));
		subData.addSubDir(new DataChart("ABB").setRiskLevel(5));
		subData.addSubDir(new DataChart("ABC").setRiskLevel(10));
		
		riskSurveyor.meanTheRisk(subData);
		
		subData = data.getChildren().get(1);
		Assert.assertEquals("AB", subData.getLocation());
		Assert.assertEquals(5, subData.getRiskLevel());
	}

}
