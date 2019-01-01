/**
 * 
 */
package fr.skiller.source.scanner;

import fr.skiller.data.internal.SunburstData;
import fr.skiller.data.internal.Project;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Source repository scanner.
 */
public interface RepoScanner {

	/**
	 * Clone the source code repository
	 * @param project Project whose source code files should be scan in the repository
	 * @param settings connection settings
	 * @throws Exception thrown if any application or network error occurs.
	 */
	void clone(Project project, ConnectionSettings settings) throws Exception;

	/**
	 * Parse the repository <u>already</u> cloned on the file system.<br/>
	 * <b>PREREQUESIT = The repository must have be cloned before</b>
	 * @param project Project whose source code files should be scan in the repository
	 * @param settings connection settings
	 * @return the parsed repository 
	 * @throws Exception thrown if any application or network error occurs.
	 */
	public CommitRepository parseRepository(Project project, ConnectionSettings settings) throws Exception;

	/**
	 * Aggregate the repository into the Sunburst data collections
	 * @param commitRepo repository with its history
	 * @return the data ready to use for the Sunburst chart.
	 */
	public SunburstData aggregateSunburstData(CommitRepository commitRepo);
	
	/**
     * Level of risk on all sources of this directory from the staff/level point of view.<br/>
     * The scale of risks contains 10 levels x+ 1 problem: <br/>
     * <ul>
     * 		<li>0 : All commits of all sources have been submitted by developers still active in the staff team.</li>
     * 		<li> 1 : <b>90% of all commits</b> have been made by active developers in the staff team.<br/>
     * 				 <b><u>AND</u></b> <b>the last commits</b> have been submitted by them.</li>
     * 		<li> 2 : <b>80% of all commits</b> have been made by active developers in the staff team. It's a global statistics evaluated on all sources in the directory.<br/>
     * 				 <b><u>AND</u></b> <b>the last commits</b> have been submitted by them.</li>
     * 		<li> 3 : <b>80% of all commits</b> have been made by active developers in the staff team. Some source files are not covered at this level</li>
     * 		<li> 4 : <b>60% of all commits</b> have been made by active developers in the staff team. All sources in the repository are covered.</li>
     * 		<li> 5 : <b>60% of all commits</b> have been made by active developers in the staff team.</li>
     * 		<li> 6 : <b>60% of all commits</b> have been made by active developers in the staff team. AND there are source files in the repository without active developer.</li>
     * 		<li> 7 : <b>33% of all commits</b> have been submitted by active developers in the staff team.</li>
     * 		<li> 8 : <b>20% or less of all commits</b> have been made by active developers in the staff team.</li>
     * 		<li> 9 : <b>20%, or less, of all commits</b> have been made by active developers in the staff team.
     * 				 <b><u>AND</u></b> none of them have submitted the most recent commits on the source files.</li>
     * 		<li>10 : It's no more a risk. It is a problem. None of the current developers in the company have worked on the source files.</li>
     * </ul>
	 * @param sunburstData
	 */
	public void evaluateTheRisk(SunburstData sunburstData);
	
	/**
	 * Set the preview settings for each directory in the passed tree.
	 * @param dataTree the data tree representing the repository directories
	 */
	public void setPreviewSettings(SunburstData dataTree);
}
