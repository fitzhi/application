/**
 * 
 */
package fr.skiller.bean;

import java.util.List;
import java.util.Map;

import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.RiskLegend;
import fr.skiller.data.source.CommitRepository;

/**
 * Interface in charge of the risks evaluation of a project regarding the team.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface RiskProcessor {

	/**
	 * @return the legend for each level of risk.<br/>
	 * The key is an integer. It contains the risk level from 0 to 10.<br/>
	 * The value, a representation for this risk (a level, a color and a description).
	 */
	Map<Integer, RiskLegend> riskLegends();
	
	/**
     * Evaluate the level of risk on all entries in the repository from the staff/level point of view.<br/>
     * The scale of risks contains 10 levels x+ 1 problem: <br/>
	 * @param repository the repository retrieved and parsed from the source control tool (i.e. GIT, SVN...).
	 * @param data repository data prepared for the Sunburst chart 
	 */
	void evaluateTheRisk(CommitRepository repository, RiskChartData data) ;

	/**
	 * Set the preview settings for each directory in the passed tree.
	 * @param dataTree the data tree representing the repository directories
	 */
	void setPreviewSettings(RiskChartData dataTree);
	
	/**
	 * Mean the risk for the children of this location.<br/>
	 * Fill the risk for this location if no risk has been affected yet.<br/>
	 * A location without source files, cannot get a calculated risk level.
	 * @param data data previewed location prepared for the sunburst chart 
	 * @return the calculated level of risk
	 */
	int meanTheRisk(RiskChartData location);
	
}
