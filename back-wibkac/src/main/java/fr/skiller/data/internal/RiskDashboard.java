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
	 * Data ready to use for the chart.
	 */
	public final DataChart riskChartData;	
	
	/**
	 * List of unknown contributors.
	 */
	public final Set<Pseudo> undefinedContributors;

	/**
	 * Main constructor of the risk Dashboard
	 * @param riskChartData data ready to use for the Sunburst chart.
	 * @param contributors list of unknown contributors.
	 */
	public RiskDashboard(final DataChart riskChartData, final Set<Pseudo> contributors) {
		super();
		this.riskChartData = riskChartData;
		this.undefinedContributors = contributors;
	}
	
}
