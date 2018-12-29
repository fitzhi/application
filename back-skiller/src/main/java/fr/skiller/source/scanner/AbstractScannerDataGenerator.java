package fr.skiller.source.scanner;

import fr.skiller.data.internal.SunburstData;
import fr.skiller.data.source.CommitRepository;

/**
 * Abstract class in charge of generating the data collection for the project Sunburst viewer.
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public abstract class AbstractScannerDataGenerator {

	/**
	 * Generate the collection ready to display on the sunburst chart.
	 * @param commitRepo the repository history retrieve from a source control
	 * @return
	 */
	public SunburstData agregateSunburstData(final CommitRepository commitRepo) {
		return null;
	}

}
