package com.fitzhi.data.external;

import java.io.Serializable;
import java.util.List;

import com.fitzhi.data.internal.Committer;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.RiskDashboard;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * This object is used to transfer data ready made to be used by an UI component.
 * </p>
 * <p>
 * Therefore, this object is coupled with an Angular component.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Data
@NoArgsConstructor
public class Sunburst implements Serializable {

	private static final long serialVersionUID = 7920817865031921102L;
	
	/**
	 * Project risk livel.
	 */
	private int projectRiskLevel;
	
	/**
	 * Project identifier.
	 */
	private int idProject;
	
	/**
	 * Data ready made for the sunburst chart.
	 */
	private DataChart sunburstData;

	/**
	 * Array of unknown contributors who are unknown in the staff collection.
	 */
	private List<Committer> ghosts;
	
	/**
	 * @param idProject project identifier.
	 * @param projectRiskLevel evaluated risk level of the whole project.
	 * @param riskDashboard Dashboard data ready to be injected in the sunburst chart.
	 */
	public Sunburst(final int idProject, final int projectRiskLevel, RiskDashboard riskDashboard) {
		this.setIdProject(idProject);
		this.projectRiskLevel = projectRiskLevel;
		this.setSunburstData(riskDashboard.riskChartData);
		this.setGhosts(riskDashboard.undefinedContributors);
	}

	/**
	 * @param idProject project identifier.
	 * @param projectRiskLevel evaluated risk level of the whole project.
	 * @param sunburstData data ready to be injected in the sunburst chart.
	 */
	public Sunburst(final int idProject, final int projectRiskLevel, DataChart sunburstData, int code, String message) {
		this.setIdProject(idProject);
		this.projectRiskLevel = projectRiskLevel;		
		this.setSunburstData(sunburstData);
	}

	/**
	 * @param idProject project identifier.
	 * @param projectRiskLevel evaluated risk level of the whole project.
	 * @param code code of processing error
	 * @param message corresponding message of error
	 */
	public Sunburst(final int idProject, final int projectRiskLevel, int code, String message) {
		this(idProject, projectRiskLevel, null, code, message);
	}
	
}
