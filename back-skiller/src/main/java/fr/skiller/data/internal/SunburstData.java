/**
 * 
 */
package fr.skiller.data.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.skiller.Global;
import fr.skiller.data.source.CommitRepository;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Sunburst data build from the History of the Source repository with layout information.
 */
public class SunburstData {

	/**
	 * A source directory and a component within a package.<br/>
	 * <i>A class like org.junit.runner.RunWith will involve 3 directories : </i>
	 * <code>org</code>, <code>junit</code>, <code>runner</code>
	 */
    public String directory;
    
    /**
     * Last updating date of source file inside the package/directory 
     */
    public String lastUpdate;
    
	/**
	 * The color of the slice of sunburst representing this directory. <br/>
	 * By default, this color will be grey.
	 */    
    String color ="grey";
    
	/**
	 * Number of source files contained within this directory and its subsequent sub-directories.
	 */        
    public int numberOfFiles;
    
	/**
	 * Array containing the sub-directories of the actual directory.
	 */        
    public List<SunburstData> children;

    /**
     * Classname (or equivalent) located inside this directory.
     */
    private Set<String> classNames;
    
	/**
	 * @param directory the name of the directory (such as com, fr...)
	 */
	public SunburstData(String directory) {
		super();
		this.directory = directory;
	}
   
	/**
	 * Add a sub-directory to the current one.
	 * @param subDir the directory record
	 * @return the item in the list corresponding to the passed subDir <br/><i>(either the newly inserted  item, or the already recorded one)</i>
	 */
	public SunburstData addsubDir(final SunburstData subDir) {
		if (children == null) {
			children = new ArrayList<>();
			children.add(subDir);
			return children.get(0);
		} else {
			Optional<SunburstData> opt = children.stream().filter(data -> data.directory.equals(subDir.directory)).findAny();
			if (!opt.isPresent()) {
				children.add(subDir);		
				return children.stream().filter(data -> data.directory.equals(subDir.directory)).findAny().get();
			} else {
				return opt.get();
			}
		}
	}

	/**
	 * Inject a source file in the collection.
	 * @param current position
	 * @param dirAndFilename an array containing the remaining sub-directories and the filename of the source element.
	 */
	public void injectFile(final SunburstData element, final String[] dirAndFilename) {
		// We register the filename in the classnames set
		if (dirAndFilename.length == 1) {
			element.addClassName(dirAndFilename[0]);
			return;
		}
		// Recurcive call.
		injectFile( element.addsubDir(new SunburstData(dirAndFilename[0])), 
				Arrays.copyOfRange(dirAndFilename, 1, dirAndFilename.length));
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if (children!=null) {
			for (SunburstData child : children) {
				sb.append("\t"+child.toString()+Global.LN);
			}
		}
		return "CodeDir [directory=" + directory 
				+ ", lastUpdate=" + ((lastUpdate == null) ? "null" : lastUpdate) 
				+ ", color=" + ((color == null) ? "null" : color)
				+ ", numberOfFiles=" + numberOfFiles
				+ ( (sb.toString().length()>0) ? (Global.LN + sb.toString()) : "")
				+ "]";
	}
	
	/**
	 * Add a filename inside the collection, and subsequently, update the number of elements declared in that sub-directory.
	 * @param fileName the filename
	 * @return {@code true} if the set did not already contain the specified filename
	 */
	public boolean addClassName(String fileName) {;
		if (this.classNames == null) {
			this.classNames = new HashSet<String>();
		}
		boolean done = this.classNames.add(fileName);
		this.numberOfFiles = this.classNames.size();
		return done;
	}
	
}
