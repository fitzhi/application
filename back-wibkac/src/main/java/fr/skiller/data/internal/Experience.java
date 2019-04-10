/**
 * 
 */
package fr.skiller.data.internal;

/**
 * Knowledge background of a developer, technical expert, any staff member of the company.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Experience {

	private int id;
	
	private int level;
	
	/**
	 * Empty constructor.
	 */
	public Experience() {
		super();
	}

	/**
	 * Constructor with parameters.
	 * @param id the id of the skill in an experience
	 * @param level the degree of knowledge obtained by a developer on this skill
	 */
	public Experience(final int id, final int level) {
		this.setId(id);
		this.setLevel(level);
	}
	
	/**
	 * This is a key pattern executed to create the key of experience used into map of data.
	 * Theses Maps are used for data exchange with the Angular application.
	 * @return key : constructed key
	 */
	public String key() {
		return getId()+"-"+getLevel();
	}

	@Override
	public String toString() {
		return "Experience [id=" + getId() + ", level=" + getLevel() + "]";
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
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

}
