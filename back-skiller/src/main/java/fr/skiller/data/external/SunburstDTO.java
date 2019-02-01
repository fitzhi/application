package fr.skiller.data.external;

import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.internal.Unknown;

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
	 * Data ready made for the sunburst chart.
	 */
	public RiskChartData sunburstData;

	/**
	 * Array of unknown contributors who are unknown in the staff collection.
	 */
	public Unknown unknowns[];
	
	/**
	 * @param riskDashboard Dashboard data ready to be injected in the sunburst chart.
	 */
	public SunburstDTO(RiskDashboard riskDashboard) {
		super();
		this.sunburstData = riskDashboard.riskChartData;
		unknowns = riskDashboard.undefinedContributors.stream().toArray(size -> new Unknown[size]);
	}

	/**
	 * @param sunburstData data ready to inject in the sunburst chart.
	 * @param code code of error
	 * @param message message of error
	 */
	public SunburstDTO(RiskChartData sunburstData, int code, String message) {
		super();
		this.sunburstData = sunburstData;
		this.code = code;
		this.message = message;
	}

	/**
	 * Empty constructor.
	 */
	public SunburstDTO() { }
}
