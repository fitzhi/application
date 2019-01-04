package fr.skiller.source.scanner.git;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.data.internal.SunburstData;
import fr.skiller.source.scanner.RepoScanner;

import static fr.skiller.Global.UNKNOWN;

/**
 * Test the level of risk evaluation tool.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GitScannerRiskLevelTest {
	
	/**
	 * Source control parser.
	 */
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
	
	@Test
	public void testFillTheHoles1() {
		SunburstData data = new SunburstData("A");
		data.setRiskLevel(1);
		
		data.addsubDir(new SunburstData("AA").setRiskLevel(2));
		SunburstData subData = new SunburstData("AB").setRiskLevel(UNKNOWN);
		data.addsubDir(subData);
		data.addsubDir(new SunburstData("AC").setRiskLevel(1));
		
		subData.addsubDir(new SunburstData("ABA").setRiskLevel(1));
		subData.addsubDir(new SunburstData("ABB").setRiskLevel(5));
		subData.addsubDir(new SunburstData("ABC").setRiskLevel(10));
		
		scanner.meanTheRisk(subData);
		
		subData = data.children.get(1);
		Assert.assertEquals("AB", subData.location);
		Assert.assertEquals(5, subData.getRiskLevel());
	}

	@Test
	public void testFillTheHoles2() {
		SunburstData data = new SunburstData("A");
		data.setRiskLevel(1);
		
		data.addsubDir(new SunburstData("AA").setRiskLevel(2));
		SunburstData subData = new SunburstData("AB").setRiskLevel(UNKNOWN);
		data.addsubDir(subData);
		data.addsubDir(new SunburstData("AC").setRiskLevel(1));
		
		subData.addsubDir(new SunburstData("ABA").setRiskLevel(1));
		subData.addsubDir(new SunburstData("ABB").setRiskLevel(5));
		subData.addsubDir(new SunburstData("ABC").setRiskLevel(10));
		
		scanner.meanTheRisk(subData);
		
		subData = data.children.get(1);
		Assert.assertEquals("AB", subData.location);
		Assert.assertEquals(5, subData.getRiskLevel());
	}

}
