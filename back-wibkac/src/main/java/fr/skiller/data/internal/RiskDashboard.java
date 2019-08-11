/**
 * 
 */
package fr.skiller.data.internal;

import java.util.Set;


/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class RiskDashboard {

	/**
	 * Global level of risk evaluated of the whole project.
	 */
	private int projectRiskLevel = -1;
	
	/**
	 * Data ready to use for the chart.
	 */
	public final DataChart riskChartData;	
	
	/**
	 * List of unknown contributors.
	 */
	public final Set<Committer> undefinedContributors;

	/**
	 * Main constructor of the risk Dashboard
	 * @param riskChartData data ready to use for the Sunburst chart.
	 * @param contributors list of unknown contributors.
	 */
	public RiskDashboard(final DataChart riskChartData, final Set<Committer> contributors) {
		super();
		this.riskChartData = riskChartData;
		this.undefinedContributors = contributors;
	}

	/**
	 * @return the projectRiskLevel
	 */
	public int getProjectRiskLevel() {
		return projectRiskLevel;
	}

	/**
	 * @param projectRiskLevel the projectRiskLevel to set
	 */
	public void setProjectRiskLevel(int projectRiskLevel) {
		this.projectRiskLevel = projectRiskLevel;
	}
	
}
