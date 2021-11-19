/**
 * 
 */
package com.fitzhi.bean;

import java.util.List;
import java.util.Map;

import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RiskLegend;
import com.fitzhi.data.source.CommitRepository;

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
     * <p>Evaluate the level of risk on all entries in the repository from the staff/level point of view.<p>
     * The actual active risk computation is located in {@link RiskCommitAndDevActiveProcessorImpl#evaluateTheRisk}<br/> 
     * The scale of risks contains 10 levels x+ 1 problem: <br/>
	 * @param repository the repository retrieved and parsed from the source control tool (i.e. GIT, SVN...)..
	 * @param data repository data prepared for the Sunburst chart.
	 * @param statsCommit list of statistics of commits.
	 */
	void evaluateTheRisk(CommitRepository repository, DataChart data, List<StatActivity> statsCommit) ;

	/**
	 * <p>Evaluated the global risk of the given project.</p>
	 * @param project active project currently under investigation.
	 * @param dataTree the data tree representing the repository of this project.
	 */
	void evaluateProjectRisk(Project project, DataChart dataTree);

	/**
	 * <p>Set the preview settings for each directory in the passed tree.</p>
	 * <p>
	 * e.g. for the Sunburst chart, the color of each sliced is designed 
	 * depending on the level of risk evaluated. </p>
	 * @param dataTree the data tree representing the repository directories
	 */
	void setPreviewSettings(DataChart dataTree);
	
	/**
	 * <p>Mean the risk for the children of this location.</p>
	 * <p>
	 * Fill the risk for this location if no risk has been affected yet.
	 * </p>
	 * A location without source files, cannot get a calculated risk level.
	 * @param location data previewed location prepared for the sunburst chart 
	 * @return the calculated level of risk
	 */
	int meanTheRisk(DataChart location);

	
}
