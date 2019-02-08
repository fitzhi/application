/**
 * 
 */
package fr.skiller.source.scanner;

import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.RiskDashboard;
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
	CommitRepository parseRepository(Project project, ConnectionSettings settings) throws Exception;

	/**
	 * Aggregate the history of the repository into the risks dashboard.
	 * @param project working project
	 * @param commitRepo the parsed repository history retrieved from the version control
	 * @return the dashboard of the current project.
	 */
	RiskDashboard aggregateDashboard(Project project, CommitRepository commitRepo);
		
	/**
	 * Generate and complete the dashboard generation figuring the activities of staff members for the passed project
	 * @param project the project whose source code files should be parsed in the repository
	 * @return the project risk dashboard 
	 * @throws Exception thrown if any application or network error occurs during the treatment.
	 */
	RiskDashboard generate(Project project) throws Exception;

	/**
	 * This method is an ASYNCHRONOUS wrapper from the method {@link #generate(Project)}
	 * <br/>
	 * Generate and complete the dashboard generation figuring the activities of staff members for the passed project
	 * @param project the project whose source code files should be parsed in the repository
	 * @return the project risk dashboard 
	 * @throws Exception thrown if any application or network error occurs during the treatment.
	 */
	RiskDashboard generateAsync(Project project) throws Exception;
	
	/**
	 * Test if risks dashboard have been executed.
	 * @param project the selected project
	 * @return 	{@code true} if the intermediate data are available to complete the dashboard, 
	 * 			{@code false} if the complete operation is required (Therefore, this operation will be asynchronous).  
	 * @throws Exception
	 */
	boolean hasAvailableGeneration(Project project) throws Exception;
}
