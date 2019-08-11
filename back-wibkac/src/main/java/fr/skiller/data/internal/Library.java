/**
 * 
 */
package fr.skiller.data.internal;

/**
 * <p>External dependency detected or declared in the repository.<br/>
 * All source files within the exclusion directory will be excluded from this analysis.</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class Library {
	
	private String exclusionDirectory;
	
	private int type;

	/**
	 * Empty constructor for serialization purpose.
	 */
	public Library() {
	}
	
	public Library(String exclusionDirectory, int type) {
		super();
		this.exclusionDirectory = exclusionDirectory;
		this.type = type;
	}

	/**
	 * @return the exclusionDirectory
	 */
	public String getExclusionDirectory() {
		return exclusionDirectory;
	}

	/**
	 * @param exclusionDirectory the exclusionDirectory to set
	 */
	public void setExclusionDirectory(String exclusionDirectory) {
		this.exclusionDirectory = exclusionDirectory;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	
}
