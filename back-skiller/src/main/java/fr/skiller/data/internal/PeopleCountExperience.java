/**
 * 
 */
package fr.skiller.data.internal;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class PeopleCountExperience{

	/**
	 * The key skill-level, which is constructed as this example : skillId : "n", level : "p" -> key : "n-p"
	 */
	final String keyExperience;
	
	/**
	 * Number of developers having this skill in that level.
	 */
	final int count;
	
	/**
	 * @param keyExperience
	 * @param count
	 */
	public PeopleCountExperience(String keyExperience, int count) {
		super();
		this.keyExperience = keyExperience;
		this.count = count;
	}
	
}
