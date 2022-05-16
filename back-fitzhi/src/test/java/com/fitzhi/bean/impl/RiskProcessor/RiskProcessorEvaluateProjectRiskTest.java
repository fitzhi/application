package com.fitzhi.bean.impl.RiskProcessor;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.DataChartTypeData;
import com.fitzhi.data.internal.Project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p> Test of the method {@link RiskProcessor#evaluateProjectRisk(com.fitzhi.data.internal.Project, com.fitzhi.data.internal.DataChart)} </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskProcessorEvaluateProjectRiskTest {
	
	@Autowired
	RiskProcessor riskProcessor;

	@MockBean
	ProjectHandler projectHandler;

	@Test
	public void empty() {
		DataChart dataTree = new DataChart("fr");
		Project p = new Project(1789, "The French revolution");
		riskProcessor.evaluateProjectRisk(p, dataTree);
		verify(projectHandler, times(1)).saveRisk(p, 0);
	}

	@Test
	public void nominal() {
		DataChart dataTree = new DataChart("fr");
		DataChart spyDataTree = spy(dataTree);
		when(spyDataTree.sum(DataChartTypeData.IMPORTANCE)).thenReturn(100d);
		when(spyDataTree.sum(DataChartTypeData.RISKLEVEL_TIMES_IMPORTANCE)).thenReturn(300d);

		Project p = new Project(1789, "The French revolution");
		riskProcessor.evaluateProjectRisk(p, spyDataTree);
		
		verify(projectHandler, times(1)).saveRisk(p, 3);
	}
}
