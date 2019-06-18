/**
 * 
 */
package fr.skiller.data.internal;

import static fr.skiller.Global.UNKNOWN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.skiller.Global;
import fr.skiller.SkillerRuntimeException;
/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Sunburst data build from the History of the Source repository with layout information.
 */
public class DataChart implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4466813253905697921L;

	/**
	 * A location (directory) inside the tree files of the repository.<br/>
	 * A class like {@code org.junit.runner.RunWith} involve 3 instances of RiskChartData, one for
	 * <code>org</code>, <code>junit</code> and <code>runner</code>
	 */
    private String location;
    
    /**
     * Last date of update for the source file inside the package/directory 
     */
    private Date lastUpdate;
    
    /**
     * Level of risk evaluated for this location.
     * @see fr.skiller.source.crawler.RepoScanner#evaluateTheRisk 
     */
    private int riskLevel = UNKNOWN;
    
 	/**
	 * The color of the slice of sunburst representing this directory. <br/>
	 * By default, this color will be grey.
	 */    
    private String color ="whiteSmoke";
    
	/**
	 * Number of source files contained within this directory and its subsequent sub-directories.
	 */        
    private int numberOfFiles;
    
	/**
	 * Array containing the sub-directories of the actual directory.
	 */        
    private List<DataChart> children;

    /**
     * Source file located inside directly in this directory.
     */
    private Set<SourceFile> sources;
    
	/**
	 * @param location the name of the directory (such as com, fr...)
	 */
	public DataChart(String location) {
		super();
		this.setLocation(location);
	}
   
	/**
	 * Add a sub-directory to the current one.
	 * @param subDir the directory record
	 * @return the item in the list corresponding to the passed subDir <br/><i>(either the newly inserted  item, or the already recorded one)</i>
	 */
	public DataChart addSubDir(final DataChart subDir) {
		if (getChildren() == null) {
			setChildren(new ArrayList<>());
			getChildren().add(subDir);
			return subDir;
		} else {
			Optional<DataChart> opt = getChildren().stream().filter(data -> data.getLocation().equals(subDir.getLocation())).findAny();
			if (!opt.isPresent()) {
				getChildren().add(subDir);	
				return subDir;
			} else {
				return opt.get();
			}
		}
	}

	/**
	 * Inject a source file in the collection.
	 * @param current position
	 * @param dirAndFilename an array containing the remaining sub-directories and the filename of the source element.
	 * @param date of the latest commit.
	 * @param committers Array of staff identifiers who are committed in this source file
	 */
	public void injectFile(final DataChart element, final String[] dirAndFilename, final Date latestCommit, final int[] committers) {
		// We register the filename in the source files set
		if (dirAndFilename.length == 1) {
			element.addSource(dirAndFilename[0], latestCommit, committers);
			if ((element.getLastUpdate() == null) || (element.getLastUpdate().before(latestCommit)))  {
				element.setLastUpdate(latestCommit);
			}
			return;
		}
		// Recursive call.
		injectFile( element.addSubDir(new DataChart(dirAndFilename[0])), 
				Arrays.copyOfRange(dirAndFilename, 1, dirAndFilename.length), 
				latestCommit, committers);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if (getChildren()!=null) {
			for (DataChart child : getChildren()) {
				sb.append("\t"+child.toString()+Global.LN);
			}
		}
		return "CodeDir [directory=" + getLocation() 
				+ ", lastUpdate=" + ((getLastUpdate() == null) ? "null" : getLastUpdate()) 
				+ ", color=" + ((getColor() == null) ? "null" : getColor())
				+ ", numberOfFiles=" + getNumberOfFiles()
				+ ( (sb.toString().length()>0) ? (Global.LN + sb.toString()) : "")
				+ "]";
	}
	
	/**
	 * Add a source file inside the collection, and subsequently, update the number of elements declared in that sub-directory.
	 * @param source the source filename.
	 */
	public void addSource(SourceFile sourceFile) {
		
		if (this.sources == null) {
			this.sources = new HashSet<>();
		}
		if (this.sources.stream().anyMatch(
				item -> item.getFilename().equals(sourceFile.getFilename()))) {
			throw new SkillerRuntimeException("@" + getLocation() + " " + sourceFile.getFilename() + " already exists.");
		}
		this.sources.add(sourceFile);
		this.setNumberOfFiles(this.sources.size());
	}

	/**
	 * Add a filename inside the collection, and subsequently, update the number of elements declared in that sub-directory.
	 * @param filename the source filename.
	 * @param lastCommit The most recent date of commit done on this file.
	 * @param committers list of committers involved in this source file
	 */
	public void addSource(final String filename, final Date lastCommit, final int[] committers) {
		
		if (this.sources == null) {
			this.sources = new HashSet<>();
		}
		if (this.sources.stream().anyMatch(item -> item.getFilename().equals(filename))) {
			throw new SkillerRuntimeException(filename + " already exists.");
		}
		this.sources.add(new SourceFile(filename, lastCommit, committers));
		this.setNumberOfFiles(this.sources.size());
	}
	/**
	 * Set the risk level.
	 * @param riskLevel the passed new risk level
	 * @return this instance of {@code SunburstData} for chaining invocations.
	 */
	public DataChart setRiskLevel(final int riskLevel) {
		this.riskLevel = riskLevel;
		return this;
	}
	
	/**
	 * @return riskLevel the current risk level
	 */
	public int getRiskLevel() {
		return this.riskLevel;
	}
	
	/**
	 * @return {@code true} if the directory has an unknown risk level, {@code false} otherwise
	 */
	public boolean hasUnknownRiskLevel() {
		return (this.riskLevel == UNKNOWN);
	}
	
	/**
	 * @return the set of classnames present in this directory.
	 */
	public Set<SourceFile> getClassnames() {
		return sources;
	}

	/**
	 * A location (directory) inside the tree files of the repository.<br/>
	 * A class like {@code org.junit.runner.RunWith} involve 3 instances of SunbustData, one for
	 * <code>org</code>, <code>junit</code> and <code>runner</code>
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the lastUpdate, last date of update for the source file inside the package/directory 
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the color of the slice of sunburst representing this directory. <br/>
	 * By default, this color will be grey.
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
	 * @return the numberOfFiles number of source files contained within this directory 
	 * and its subsequent sub-directories.
	 */
	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	/**
	 * @param numberOfFiles the numberOfFiles to set
	 */
	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	/**
	 * @return the children, an Array containing the sub-directories of the actual directory.
	 */
	public List<DataChart> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set.
	 * <br/>children is an array containing the sub-directories of the actual directory.
	 */
	public void setChildren(List<DataChart> children) {
		this.children = children;
	}
	
}
