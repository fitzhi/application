/**
 * 
 */
package fr.skiller.data.internal;

/**
 * Knowledge background of a developer, technical expert, any staff member of the company.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Experience {

	public int id;
	public String title; 
	public int level;
	
	
	/**
	 * Empty constructor.
	 */
	public Experience() {
		super();
	}


	/**
	 * Constructor with param.
	 * @param id the id of the skill in an experience
	 * @param title the title of the skill
	 * @param level the degree of knowledge obtained by a developer on this skill
	 */
	public Experience(final int id, final String title, final int level) {
		this.id = id;
		this.title = title;
		this.level = level;
	}
	
	
}
