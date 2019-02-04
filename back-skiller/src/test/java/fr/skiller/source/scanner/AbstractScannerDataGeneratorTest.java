/**
 * 
 */
package fr.skiller.source.scanner;

import org.junit.Assert;
import org.junit.Test;

import fr.skiller.bean.impl.RiskProcessorImpl;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class AbstractScannerDataGeneratorTest {

	@Test
	public void test() {
		Assert.assertTrue(RiskProcessorImpl.isClassFile("com/sqli/rte/vegeo/api/services/arcgis/BaseServiceAbstract.java", "BaseServiceAbstract.java"));
	}
}
