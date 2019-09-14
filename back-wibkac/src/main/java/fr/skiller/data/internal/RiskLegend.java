/**
 * 
 */
package fr.skiller.data.internal;

import lombok.Data;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class RiskLegend {

	/**
	 * Risk level.
	 */
	private final int level;

	/**
	 * Risk color.
	 */
	private final String color;
	
	/**
	 * Description of this risk.
	 */
	private final String description;

}
