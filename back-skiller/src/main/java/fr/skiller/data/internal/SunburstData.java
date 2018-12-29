/**
 * 
 */
package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.List;

import fr.skiller.Global;

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
	 * @param directory the name of the directory (such as com, fr...)
	 */
	public SunburstData(String directory) {
		super();
		this.directory = directory;
	}
   
	/**
	 * Add a sub-directory to the current one.
	 * @param subDir the directory record
	 * @return the updated children list.
	 */
	public List<SunburstData> addsubDir(final SunburstData subDir) {
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(subDir);
		return children;
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
}
