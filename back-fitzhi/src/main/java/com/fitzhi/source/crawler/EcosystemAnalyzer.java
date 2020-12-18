package com.fitzhi.source.crawler;

import java.util.List;
import java.util.Map;

import com.fitzhi.data.internal.Ecosystem;
import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * Interface in charge of analysis the repository.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface EcosystemAnalyzer {

	/**
	 * Load the ecosystems declared inside the application.
	 * @return the list of ecosystems
	 * @throws ApplicationException thrown if any problem occurs
	 */
	Map<Integer, Ecosystem> loadEcosystems() throws ApplicationException;
	
	/**
	 * Scan the repository and detect the ecosystems .
	 * @param pathnames the list of pathnames retrieved in the repository
	 * @return the list of detected ecosystem in the repository
	 * @throws ApplicationException thrown if any problem occurs
	 */
	List<Ecosystem> detectEcosystems(List<String> pathnames) throws ApplicationException;
	
}
