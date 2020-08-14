/**
 * 
 */
package com.fitzhi.data.internal;

import static com.fitzhi.Global.UNKNOWN;
import static com.fitzhi.data.internal.DataChartTypeData.IMPORTANCE;
import static com.fitzhi.data.internal.DataChartTypeData.RISKLEVEL_TIMES_IMPORTANCE;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fitzhi.Global;
import com.fitzhi.SkillerRuntimeException;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Sunburst data build from the History of the Source repository with layout information.</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
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
    private LocalDate lastUpdate;
    
    /**
     * Level of risk evaluated for this location.
     * @see com.fitzhi.source.crawler.RepoScanner#evaluateTheRisk 
     */
    private int riskLevel = UNKNOWN;
    
 	/**
	 * The color of the slice of sunburst representing this directory. <br/>
	 * By default, this color will be grey.
	 */    
    private String color ="whiteSmoke";
    
	/**
	 * Number of source files contained within this directory.
	 */        
    private int numberOfFiles;
    
	/**
	 * <p>Proportion of the location inside the diagram.</p>
	 * <p>
	 * <i>A 2000 lines Java Class should have a higher importance than  2 small class of 100 lines</i>
	 * </p>
	 */        
    private long importance = 0;
    
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
	 * @param dirAndFilename an array containing the clean path of a source file.
	 * @param importance the importance of a source file
	 * @param date of the latest commit.
	 * @param committers Array of staff identifiers who are committed in this source file
	 */
		// We register the filename in the source files set
	public void injectFile(final DataChart element, String[] dirAndFilename, final long importance, final LocalDate latestCommit, final int[] committers) {
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"Injecting %s with important %d", 
				StringUtils.join(dirAndFilename, "/"),
				importance));
		}			
	
		if (dirAndFilename.length == 1) {
			if (log.isDebugEnabled()) {
				log.debug(String.format(
					"Adding %s with important %d", 
					dirAndFilename[0],
					importance));
			}			
			element.addSource(dirAndFilename[0], importance, latestCommit, committers);
			if ((element.getLastUpdate() == null) || (element.getLastUpdate().isBefore(latestCommit)))  {
				element.setLastUpdate(latestCommit);
			}
			return;
		}
		// Recursive call.
		injectFile( element.addSubDir(new DataChart(dirAndFilename[0])),  
			Arrays.copyOfRange(dirAndFilename, 1, dirAndFilename.length), importance,
			latestCommit, 
			committers);
	}
	
	public void dump(StringBuilder sb, String offset) {
		sb.append(offset).append(getLocation()).append(" i:").append(importance).append(" r:").append(riskLevel).append(Global.LN);
		if (sources!=null) {
			sb.append(offset);
			sources.stream().map(SourceFile::getFilename).forEach(s -> sb.append(s).append("/"));
			sb.append(Global.LN);
		}
		if (getChildren()!=null) {
			for (DataChart child : getChildren()) {
				child.dump (sb, offset+" ");
			}
		}
	}
	
	/**
	 * <p>
	 * Add a source file inside the collection, and subsequently, 
	 * update the number of elements declared in that sub-directory.
	 * </p>
	 * @param source the source filename.
	 */
	public void addSource(SourceFile sourceFile, long importance) {
		
		if (this.sources == null) {
			this.sources = new HashSet<>();
		}
		if (this.sources.stream().anyMatch(
				item -> item.getFilename().equals(sourceFile.getFilename()))) {
			throw new SkillerRuntimeException("@" + getLocation() + " " + sourceFile.getFilename() + " already exists.");
		}
		this.sources.add(sourceFile);
		this.setNumberOfFiles(this.sources.size());
		this.addToImportance(importance);
	}

	/**
	 * Add a filename inside the collection, and subsequently, update the number of elements declared in that sub-directory.
	 * @param filename the source filename.
	 * @param importance importance of this source file within the project
	 * @param lastCommit The most recent date of commit done on this file.
	 * @param committers list of committers involved in this source file
	 */
	public void addSource(final String filename, long importance, final LocalDate lastCommit, final int[] committers) {
		
		if (this.sources == null) {
			this.sources = new HashSet<>();
		}
		if (this.sources.stream().anyMatch(item -> item.getFilename().equals(filename))) {
			throw new SkillerRuntimeException(filename + " already exists.");
		}
		this.sources.add(new SourceFile(filename, lastCommit, committers));
		this.setNumberOfFiles(this.sources.size());
		this.addToImportance(importance);
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
	public LocalDate getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(LocalDate lastUpdate) {
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
	 * @return the importance representing this location.
	 */
	public long getImportance() {
		return importance;
	}

	/**
	 * @param importance the importance of a source file located in that directory 
	 * and contributing to the weight of that location in the project.
	 */
	public void addToImportance(long importance) {
		this.importance += importance;
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
	
	/** 
	 * Aggregate the content of a <code>DataChart</code> instance into this instance.
	 * @param input the <code>DataChart</code> to be transfered.
	 */
	public void aggregate(DataChart input) {
		this.location = this.location + "/" + input.location;
		this.importance += input.importance;
		this.color = input.color;
		this.lastUpdate = input.lastUpdate;
		this.numberOfFiles = input.numberOfFiles;
		this.riskLevel = input.riskLevel;
		this.sources = input.sources;
		this.children = input.children;
	}
	
	public double sum (DataChartTypeData type) {
		if ((this.children == null) || (this.children.isEmpty())) {
			return (type == IMPORTANCE) ?
			((double) this.importance) : ((double) this.importance * this.riskLevel);
		}
		if (type == IMPORTANCE) {
			return this.importance + 
					this.children.stream().mapToDouble(dc -> dc.sum(IMPORTANCE)).sum();
		} else {
			return this.riskLevel * this.importance + 
					this.children.stream().mapToDouble(dc -> dc.sum(RISKLEVEL_TIMES_IMPORTANCE)).sum();				
		}
	}
}
