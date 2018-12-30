package fr.skiller.source.scanner;

import java.io.File;

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
	public SunburstData aggregateSunburstData(final CommitRepository commitRepo) {
		SunburstData root = new SunburstData("root");
		commitRepo.getRepository().keySet().stream().forEach(filename -> root.injectFile(root, filename.split(File.separator)));
		
		return root;
	}

}
