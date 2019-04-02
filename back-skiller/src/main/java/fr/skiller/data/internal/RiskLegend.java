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
	private int level;

	/**
	 * Risk color.
	 */
	private String color;
	
	/**
	 * Description of this risk.
	 */
	private String description;

	/**
	 * @param level risk level 
	 * @param color the color associated to this level
	 * @param description the description for this level
	 */
	public RiskLegend(final int level, final String color, final String description) {
		super();
		this.setLevel(level);
		this.setColor(color);
		this.setDescription(description);
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
