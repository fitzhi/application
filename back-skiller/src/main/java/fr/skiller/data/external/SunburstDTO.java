package fr.skiller.data.external;

import fr.skiller.data.internal.SunburstData;

/**
 * <p>This class is used as a Data Transfer Object between the spring boot
 * application and the Angular front.</p> 
 * <p><i>FIXME one day : I did not find a way to use HTTP headers for transferring additional information <b>in the POST request</b>. 
 * So these data are embedded in the data transfer object.</i></p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class SunburstDTO {

	/**
	 * Back-end code
	 */
	public int code = 0;
	
	/**
	 * Back-end message
	 */
	public String message = "";

	
	/**
	 * Data ready made for the sunburst chart.
	 */
	public SunburstData sunburstData;

	/**
	 * @param sunburstData data ready to inject in the sunburst chart.
	 */
	public SunburstDTO(SunburstData sunburstData) {
		super();
		this.sunburstData = sunburstData;
	}

	/**
	 * @param sunburstData data ready to inject in the sunburst chart.
	 * @param code code of error
	 * @param message message of error
	 */
	public SunburstDTO(SunburstData sunburstData, int code, String message) {
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
