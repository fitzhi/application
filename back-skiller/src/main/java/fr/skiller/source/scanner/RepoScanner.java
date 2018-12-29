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

	public SunburstData agregateSunburstData(CommitRepository commitRepo);
}
