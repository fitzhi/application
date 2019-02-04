/**
 * 
 */
package fr.skiller.data.internal;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class RiskLegend {

	/**
	 * Risk level.
	 */
	public int level;

	/**
	 * Risk color.
	 */
	public String color;
	
	/**
	 * Description of this risk.
	 */
	public String description;

	/**
	 * @param level risk level 
	 * @param color the color associated to this level
	 * @param description the description for this level
	 */
	public RiskLegend(final int level, final String color, final String description) {
		super();
		this.level = level;
		this.color = color;
		this.description = description;
	}
	
}
