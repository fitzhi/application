package fr.skiller.source.scanner;

import java.io.File;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.SunburstData;
import fr.skiller.data.source.CommitRepository;

/**
 * Abstract class in charge of generating the data collection for the project Sunburst viewer.
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public abstract class AbstractScannerDataGenerator implements RepoScanner {

	/**
	 * Generate the collection ready to display on the sunburst chart.
	 * @param commitRepo the repository history retrieve from a source control
	 * @return
	 */
	public SunburstData aggregateSunburstData(final CommitRepository commitRepo) {
		SunburstData root = new SunburstData("root");
		commitRepo.getRepository().values().stream().forEach(
				commit -> 
				root.injectFile(root, commit.sourcePath.split(File.separator), commit.evaluateDateLastestCommit()));
		
		return root;
	}

	/**
	 * For this first testing version. The risk will be randomly estimated.
	 * @param all or part of the source directories
	 */
	public void evaluateTheRisk(final SunburstData sunburstData) {
		if (sunburstData.hasUnknownRiskLevel()) {
			sunburstData.setRiskLevel( (int) (Math.random()*1000 % 11)); 
		}
		if (sunburstData.children != null) {
			sunburstData.children.stream().forEach(dir -> evaluateTheRisk(dir));
		}
	}

	public void evaluateTheRisk_v2(final SunburstData sunburstData, final StaffHandler staffHandler) {
		if (sunburstData.hasUnknownRiskLevel()) {
			sunburstData.setRiskLevel( (int) (Math.random()*1000 % 11)); 
		}
		if (sunburstData.children != null) {
			sunburstData.children.stream().forEach(dir -> evaluateTheRisk_v2(dir, staffHandler));
		}
	}
	
	
	@Override
	public void setPreviewSettings(SunburstData data) {
		if (!data.hasUnknownRiskLevel()) {
			int riskLevel = data.getRiskLevel();
			switch (riskLevel) {
			case 0:
			case 1:
				data.color = "green";
				break;
			case 2:
			case 3:
			case 4:
			case 5:
				data.color = "orange";
				break;
			case 6:
			case 7:
			case 8:
			case 9:
				data.color = "red";
				break;
			case 10:
				data.color = "black";
				break;
			}
		}
		if (data.children != null) {
			data.children.stream().forEach(dir -> setPreviewSettings(dir));
		}
	}
}
