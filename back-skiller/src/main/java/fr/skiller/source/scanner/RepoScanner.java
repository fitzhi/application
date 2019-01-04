/**
 * 
 */
package fr.skiller.source.scanner;

import fr.skiller.data.internal.SunburstData;
import fr.skiller.data.internal.Project;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.exception.SkillerException;

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
	 * Set the preview settings for each directory in the passed tree.
	 * @param dataTree the data tree representing the repository directories
	 */
	public void setPreviewSettings(SunburstData dataTree);
	
	/**
     * Evaluate the level of risk on all entries in the repository from the staff/level point of view.<br/>
     * The scale of risks contains 10 levels x+ 1 problem: <br/>
     * <ul>
     * 		<li>0 : All commits of all sources have been submitted by developers still active in the staff team.</li><br/>
     * 
     * 		<li> 1 : <b>90% of all commits</b> have been made by active developers in the staff team.<br/>
     * 				 <i>NB : It's the calculated mean on all sources in the directory.</i><br/>
     * 				 <b><u>AND</u></b> <b>the last commits</b> have been submitted by them.</li><br/>
     * 
     * 		<li> 2 : <b>80% of all commits</b> have been made by active developers in the staff team.<br/>
     * 				 <i>NB : It's the calculated mean of all sources in the directory.</i><br/>
     * 				 <b><u>AND</u></b> <b>the last commits</b> have been submitted by them.</li><br/>
     * 
     * 		<li> 3 : <b>80% of all commits</b> have been made by active developers in the staff team. 
     * 					Some of source files are only covered at <b>50%</b></li><br/>
     * 
     * 		<li> 4 : <b>80% of all commits</b> have been made by active developers in the staff team.<br/><br/>
     * 
     * 		<li> 5 : <b>60% of all commits</b> have been made by active developers in the staff team.</li><br/><br/>
     * 
     * 		<li> 6 : <b>60% of all commits</b> have been made by active developers in the staff team.<br/> 
     * 				 AND there are some file(s)  in this directory, without remaining active developers.<br/><br/>
     * 
     * 		<li> 7 : <b>33% of all commits</b> have been submitted by active developers in the staff team.</li><br/>
     * 
     * 		<li> 8 : <b>20%</b> have been made by active developers in the staff team.</li></b><br/>
     * 
     * 		<li> 9 : <b>10%</b> have been made by active developers in the staff team.
     * 				 <b><u>AND</u></b> none of them have submitted the most recent commits on the source files.</li><br/>
     * 
     * 		<li>10 : It's no more a risk. It is a problem. None of the current developers in the company have worked on the source files.</li>
     * </ul>
	 * @param repository the repository retrieved and parsed from the source control tool (i.e. GIT, SVN...).
	 * @param data repository data prepared for the Sunburst chart 
	 */
	public void evaluateTheRisk(CommitRepository repository, SunburstData data) ;

	/**
	 * Mean the risk for the children of this location.<br/>
	 * Fill the risk for this location if no risk has been affected yet.<br/>
	 * A location without source files, cannot get a calculated risk level.
	 * @param data data previewed location prepared for the sunburst chart 
	 * @return the calculated level of risk
	 */
	public int meanTheRisk(SunburstData location);
}
