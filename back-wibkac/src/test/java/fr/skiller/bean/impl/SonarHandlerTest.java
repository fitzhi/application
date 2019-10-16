package fr.skiller.bean.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.SonarHandler;
import fr.skiller.data.internal.ProjectSonarMetric;
import fr.skiller.exception.SkillerException;


/**
 * 
 * <p>
 * Testing the class {@code SonarHandlerImpl}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SonarHandlerTest {

	@Autowired
	SonarHandler sonarHandler;
	
	/**
	 * Test of {@link fr.skiller.bean.SonarHandler#getDefaultProjectSonarMetrics()}
	 */
	@Test
	public void getDefaultProjectSonarMetrics() throws SkillerException {
		
		List<ProjectSonarMetric> defaultMetrics = sonarHandler.getDefaultProjectSonarMetrics();
		assertNotNull(defaultMetrics);
		assertEquals("We're supposed to get 4 metrics", 4, defaultMetrics.size());
	}
}
