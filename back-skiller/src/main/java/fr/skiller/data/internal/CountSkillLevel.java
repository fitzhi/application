/**
 * 
 */
package fr.skiller.data.internal;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class CountSkillLevel{

	/**
	 * The key skill-level, which is constructed as this example : skillId : "n", level : "p" -> key : "n-p"
	 */
	final String keySkillLevel;
	
	/**
	 * Number of developers having this skill in that level.
	 */
	final int count;
	
	/**
	 * @param keySkillLevel
	 * @param count
	 */
	public CountSkillLevel(String keySkillLevel, int count) {
		super();
		this.keySkillLevel = keySkillLevel;
		this.count = count;
	}
	
}
