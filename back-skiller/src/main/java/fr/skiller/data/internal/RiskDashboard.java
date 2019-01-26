/**
 * 
 */
package fr.skiller.data.internal;

import java.util.HashSet;
import java.util.Set;

import javax.print.DocFlavor.STRING;


/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class RiskDashboard {

	/**
	 * Data ready to use for the Sunburst chart.
	 */
	public final RiskChartData riskChartData;	
	
	/**
	 * List of unknown contributors.
	 */
	public final Set<Unknown> undefinedContributors;

	/**
	 * @param riskChartData data ready to use for the Sunburst chart.
	 * @param contributors list of unknown contributors.
	 */
	public RiskDashboard(final RiskChartData riskChartData, final Set<String> contributors) {
		super();
		this.riskChartData = riskChartData;
		undefinedContributors = new HashSet<Unknown>();
		contributors.stream().forEach(contributor -> undefinedContributors.add(new Unknown(contributor)));
	}
	
}
