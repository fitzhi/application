/**
 * 
 */
package fr.skiller.source.scanner;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class AbstractScannerDataGeneratorTest {

	@Test
	public void test() {
		Assert.assertTrue(AbstractScannerDataGenerator.isClassFile("com/sqli/rte/vegeo/api/services/arcgis/BaseServiceAbstract.java", "BaseServiceAbstract.java"));
	}
}
