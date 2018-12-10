package fr.skiller.data.external;

import fr.skiller.data.internal.Experience;

/**
 * <p>
 * This class is used as a Data Transfer Object between the spring boot
 * application and the Angular front.
 * </p>
 * <p>
 * <i>FIXME one day : I did not find a way to use HTTP headers for transferring
 * additional information <b>in the POST request</b>. So these data are embedded
 * in the data transfer object.</i>
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class AssetDTO {

	/**
	 * Back-end code
	 */
	public int code = 0;
	/**
	 * Back-end message
	 */
	public String message = "";

	public Experience asset;

	/**
	 * @param asset
	 */
	public AssetDTO(Experience asset) {
		this.asset = asset;
	}

	/**
	 * @param asset
	 * @param code
	 * @param message
	 */
	public AssetDTO(Experience asset, int code, String message) {
		this.asset = asset;
		this.code = code;
		this.message = message;
	}

}
