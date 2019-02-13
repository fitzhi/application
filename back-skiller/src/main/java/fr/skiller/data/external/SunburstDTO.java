package fr.skiller.data.external;

import java.util.List;
import java.util.Set;

import fr.skiller.data.internal.Pseudo;
import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.RiskDashboard;

/**
 * <p>This class is used as a Data Transfer Object between the spring boot
 * application and the Angular front.</p> 
 * <p><i>FIXME one day : I did not find a way to use HTTP headers for transferring additional information <b>in the POST request</b>. 
 * So these data are embedded in the data transfer object.</i></p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class SunburstDTO extends BaseDTO {

	/**
	 * Project identifier.
	 */
	public int idProject;
	
	/**
	 * Data ready made for the sunburst chart.
	 */
	public RiskChartData sunburstData;

	/**
	 * Array of unknown contributors who are unknown in the staff collection.
	 */
	public Set<Pseudo> ghosts;
	
	/**
	 * @param idProject project identifier.
	 * @param riskDashboard Dashboard data ready to be injected in the sunburst chart.
	 */
	public SunburstDTO(final int idProject, RiskDashboard riskDashboard) {
		super();
		this.idProject = idProject;
		this.sunburstData = riskDashboard.riskChartData;
		this.ghosts = riskDashboard.undefinedContributors;
	}

	/**
	 * @param idProject project identifier.
	 * @param sunburstData data ready to be injected in the sunburst chart.
	 * @param code code of processing error
	 * @param message corresponding message of error
	 */
	public SunburstDTO(final int idProject, RiskChartData sunburstData, int code, String message) {
		super();
		this.idProject = idProject;
		this.sunburstData = sunburstData;
		this.code = code;
		this.message = message;
	}

	/**
	 * @param idProject project identifier.
	 * @param code code of processing error
	 * @param message corresponding message of error
	 */
	public SunburstDTO(final int idProject, int code, String message) {
		this(idProject, null, code, message);
	}
	
	/**
	 * Empty constructor.
	 */
	public SunburstDTO() { 
	}
}
