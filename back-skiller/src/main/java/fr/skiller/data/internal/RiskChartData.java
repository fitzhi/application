/**
 * 
 */
package fr.skiller.data.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.skiller.Global;
import static fr.skiller.Global.UNKNOWN;
import static fr.skiller.Global.LN;
/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Sunburst data build from the History of the Source repository with layout information.
 */
public class RiskChartData {

	/**
	 * A location (directory) inside the tree files of the repository.<br/>
	 * A class like {@code org.junit.runner.RunWith} involve 3 instances of SunbustData, one for
	 * <code>org</code>, <code>junit</code> and <code>runner</code>
	 */
    public String location;
    
    /**
     * Last date of update for the source file inside the package/directory 
     */
    public Date lastUpdate;
    
    /**
     * Level of risk evaluated for this location.
     * @see fr.skiller.source.scanner.RepoScanner#evaluateTheRisk 
     */
    private int riskLevel = UNKNOWN;
    
 	/**
	 * The color of the slice of sunburst representing this directory. <br/>
	 * By default, this color will be grey.
	 */    
    public String color ="whiteSmoke";
    
	/**
	 * Number of source files contained within this directory and its subsequent sub-directories.
	 */        
    public int numberOfFiles;
    
	/**
	 * Array containing the sub-directories of the actual directory.
	 */        
    public List<RiskChartData> children;

    /**
     * Source file located inside this directory.
     */
    private Set<SourceFile> sources;
    
	/**
	 * @param location the name of the directory (such as com, fr...)
	 */
	public RiskChartData(String location) {
		super();
		this.location = location;
	}
   
	/**
	 * Add a sub-directory to the current one.
	 * @param subDir the directory record
	 * @return the item in the list corresponding to the passed subDir <br/><i>(either the newly inserted  item, or the already recorded one)</i>
	 */
	public RiskChartData addsubDir(final RiskChartData subDir) {
		if (children == null) {
			children = new ArrayList<>();
			children.add(subDir);
			return subDir;
		} else {
			Optional<RiskChartData> opt = children.stream().filter(data -> data.location.equals(subDir.location)).findAny();
			if (!opt.isPresent()) {
				children.add(subDir);	
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
	public void injectFile(final RiskChartData element, final String[] dirAndFilename, final Date latestCommit, final int[] committers) {
		// We register the filename in the source files set
		//FIXME Fix here certainly...
		if (dirAndFilename.length == 1) {
			element.addSource(dirAndFilename[0], latestCommit, committers);
			if ((element.lastUpdate == null) || (element.lastUpdate.before(latestCommit)))  {
				element.lastUpdate = latestCommit;
			}
			return;
		}
		// Recursive call.
		injectFile( element.addsubDir(new RiskChartData(dirAndFilename[0])), 
				Arrays.copyOfRange(dirAndFilename, 1, dirAndFilename.length), 
				latestCommit, committers);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if (children!=null) {
			for (RiskChartData child : children) {
				sb.append("\t"+child.toString()+Global.LN);
			}
		}
		return "CodeDir [directory=" + location 
				+ ", lastUpdate=" + ((lastUpdate == null) ? "null" : lastUpdate) 
				+ ", color=" + ((color == null) ? "null" : color)
				+ ", numberOfFiles=" + numberOfFiles
				+ ( (sb.toString().length()>0) ? (Global.LN + sb.toString()) : "")
				+ "]";
	}
	
	/**
	 * Add a filename inside the collection, and subsequently, update the number of elements declared in that sub-directory.
	 * @param filename the source filename.
	 * @param lastCommit The most recent date of commit done on this file.
	 * @param committers list of committers involved in this source file
	 */
	public void addSource(final String filename, final Date lastCommit, final int[] committers) {
		
		if (this.sources == null) {
			this.sources = new HashSet<SourceFile>();
		}
		if (this.sources.stream().anyMatch(item -> item.filename.equals(filename))) {
			throw new RuntimeException(filename + " already exists.");
		}
		this.sources.add(new SourceFile(filename, lastCommit, committers));
		this.numberOfFiles = this.sources.size();
	}

	/**
	 * Set the risk level.
	 * @param riskLevel the passed new risk level
	 * @return this instance of {@code SunburstData} for chaining invocations.
	 */
	public RiskChartData setRiskLevel(final int riskLevel) {
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
	
}
