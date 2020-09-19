package com.fitzhi.data.external;

import com.fitzhi.data.internal.Experience;

/**
 * <p>
 * This class is used as a Data Transfer Object between the spring boot
 * server and the Angular front client.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class AssetDTO extends BaseDTO {

	public final Experience asset;

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
