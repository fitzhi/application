/**
 * 
 */
package com.fitzhi.bean.impl;

import static com.fitzhi.Global.INTERNAL_FILE_SEPARATOR;
import static com.fitzhi.Global.LN;
import static com.fitzhi.Global.UNKNOWN;
import static com.fitzhi.data.internal.DataChartTypeData.IMPORTANCE;
import static com.fitzhi.data.internal.DataChartTypeData.RISKLEVEL_TIMES_IMPORTANCE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitzhi.SkillerRuntimeException;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RiskLegend;
import com.fitzhi.data.internal.SourceFile;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.data.source.CommitRepository;

import lombok.Data;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Service("commitAndDevActive")
public class RiskCommitAndDevActiveProcessorImpl implements RiskProcessor {

	
	/**
	 * Statistic of activity
	 * 
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	public @Data class StatActivity {

		/**
		 * Filename.
		 */
		private final String filename;

		/**
		 * Total number of the commits submitted.
		 */
		private final long countCommits;

		/**
		 * Number of the commits submitted by active developers.
		 */
		private final long countCommitsByActiveDevelopers;


		private RiskCommitAndDevActiveProcessorImpl getOuterType() {
			return RiskCommitAndDevActiveProcessorImpl.this;
		}

	}

	/**
	 * Bean in charge of handling the staff.
	 */
	@Autowired
	StaffHandler staffHandler;

	/**
	 * Bean in charge of handling the projects.
	 */
	@Autowired
	ProjectHandler projectHandler;
	
	/**
	 * The logger for the Risk Surveyor.
	 */
	Logger logger = LoggerFactory.getLogger(RiskCommitAndDevActiveProcessorImpl.class.getCanonicalName());

	@Override
	public Map<Integer, RiskLegend> riskLegends() {

		final Map<Integer, RiskLegend> explanations = new HashMap<>();

		//
		// Setting the color of risks.
		//
		String[] colors = new String[11];
		// 28 -> 139
		// 183 -> 0
		// 69 -> 0
		for (int i=0; i<=10; i++) {
			
			String red = String.format("%X", (int) (28 + (139-28)*i/10));
			red = (red.length() == 1) ? ("0" + red) : red;
			
			String green = String.format("%X", (int) (183 - 183*i/10));
			green = (green.length() == 1) ? ("0" + green) : green;
			
			String blue = String.format("%X", (int) (69 - 69*i/10));
			blue = (blue.length() == 1) ? ("0" + blue) : blue;
			
			colors[i] = "#" + red + green + blue;
		}
		
		
		explanations.put(0, new RiskLegend(0, colors[0],
				"commits have been submitted by active developers. Perfect level of proficiency on this project"));
		explanations.put(1, new RiskLegend(1, colors[1],
				"At least, 90% of the commits have been submitted by active developers."));
		explanations.put(2, new RiskLegend(2, colors[2],
				"At least, 80% of the commits have been submitted by active developers."));
		explanations.put(3,
				new RiskLegend(3, colors[3], "At least, 70% of the commits have been submitted by active developers."));
		explanations.put(4, new RiskLegend(4, colors[4],
				"At least, 60% of the commits have been submitted by active developers."));
		explanations.put(5,
				new RiskLegend(5, colors[5], "At least, 50% of the commits have been submitted by active developers."));
		explanations.put(6,
				new RiskLegend(6, colors[6], "At least, 40% of the commits have been submitted by active developers."));
		explanations.put(7, new RiskLegend(7, colors[7],
				"At least, 30% of the commits have been submitted by active developers."));
		explanations.put(8, new RiskLegend(8, colors[8],
				"At least, 20% of the commits have been submitted by active developers."));
		explanations.put(9,
				new RiskLegend(9, colors[9], "At least, 10% of the commits have been submitted by active developers."));
		explanations.put(10, new RiskLegend(10, colors[10],
				"commits have been proceeded by INACTIVE developers. Everyone is newbie on this project."));

		return explanations;
	}

	/**
	 * <p>
	 * Evaluate the level of risk on all entries in the repository from the
	 * staff/level point of view.
	 * </p>
	 * <p>
	 * This implementation will compute the proportion of commits executed by active developers regarding the total number of commits.<br/>
	 * 11 levels of risks are inferred from this proportion.
	 * <ul>
	 * <li>0% of commits have been made on this directory by active developers. 
	 * the level of risk is 10. This is a problem</li> 
	 * <li>x% of commits have been made on this directory by active developers. Level is ceil(1-X%)</li> 
	 * </ul>
	 * </p>
	 * <p>
	 * The range of risks start a 0 and ends at 10. It presents 11 steps.<br/>
	 * </p>
	 * <p>
	 * <font color="darkOrange">
	 * If there is no commit on this directory, the level of risk is set to -1.
	 * </font></br>
	 * @param repository the repository retrieved and parsed from the source control
	 *                   tool (i.e. GIT, SVN...).
	 * @param data       repository data prepared to be displayed by the Sunburst
	 *                   chart
	 */
	public void evaluateTheRisk(final CommitRepository repository, final DataChart data,
			final List<StatActivity> statsCommit) {

		agregateCommits("", repository, data, statsCommit);

		evaluateActiveDevelopersCoverage("", data, statsCommit);
	}

	/**
	 * Count commits submitted from a directory.<br/>
	 * <i>This method is public for testing purpose.</i>
	 * 
	 * @param dir   the directory where we'll sum the number of the commits
	 *              submitted on each file
	 * @param stats the list containing a statistic entry for each class file
	 * @return the resulting count
	 */
	public long agregateCountCommits(final String dir, final List<StatActivity> stats) {
		return stats.stream().filter(entry -> entry.getFilename().indexOf(dir) == 0)
				.filter(entry -> ((entry.getFilename().length() == dir.length())
						|| (entry.getFilename().indexOf(INTERNAL_FILE_SEPARATOR, dir.length() + 1) == -1)))
				.mapToLong(StatActivity::getCountCommits).sum();
	}

	/**
	 * Count commits submitted from a directory by active developers.<br/>
	 * <i>This method is public for testing purpose.</i>
	 * 
	 * @param dir   the directory where we sum the number of commits submitted
	 *              within this directory.
	 * @param stats the list containing a statistic entry for each class file
	 * @return the resulting count
	 */
	public long agregateCountCommitsByActiveDevelopers(String dir, List<StatActivity> stats) {
		return stats.stream().filter(entry -> entry.getFilename().indexOf(dir) == 0)
				.filter(entry -> ((entry.getFilename().length() == dir.length())
						|| (entry.getFilename().indexOf(INTERNAL_FILE_SEPARATOR, dir.length() + 1) == -1)))
				.mapToLong(StatActivity::getCountCommitsByActiveDevelopers).sum();
	}

	/**
	 * <p>Evaluate the risk regarding the proportion of active developers.</p>
	 * <i>This method is public for testing purpose.</i>
	 * 
	 * @param dir   directory where the commits have been executed.
	 * @param data  location data (containing the relative location, the risks &
	 *              colors, and its children)
	 * @param stats the list containing a statistic entry for each class file
	 */
	public void evaluateActiveDevelopersCoverage(final String dir, final DataChart data,
			final List<StatActivity> stats) {

		long countCommits = agregateCountCommits(dir + data.getLocation(), stats);
		long countCommitsByActiveDevelopers = agregateCountCommitsByActiveDevelopers(dir + data.getLocation(), stats);
		int riskLevel = -1;
		if (countCommits > 0) {
			riskLevel = (int) Math.ceil(((1 - ((double) countCommitsByActiveDevelopers / (double) countCommits)) * 10));
		}

		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder(LN);
			sb.append("Evaluating the risk for " + dir + data.getLocation()).append(LN);
			sb.append("countCommits : " + countCommits).append(LN);
			sb.append("countCommitsByActiveDevelopers : " + countCommitsByActiveDevelopers).append(LN);
			sb.append("riskLevel evaluated : " + riskLevel).append(LN);
			logger.debug(sb.toString());
		}
		data.setRiskLevel(riskLevel);

		if (data.getChildren() != null) {
			data.getChildren().stream()
					.forEach(dat -> evaluateActiveDevelopersCoverage(dir + data.getLocation() + "/", dat, stats));
		}
	}

	/**
	 * <p>
	 * Aggregate the commits by class name. <i>This method is recursive!</i>
	 * </p>
	 * <p>
	 * Each entry in the class name contains :
	 * <ul>
	 * <li>the class full name (with its path)</li>
	 * <li>the number of commits executed on that file</li>
	 * <li>the number of commits executed <b>by active developers</b></li>
	 * </ul>
	 * 
	 * @param baseDir    the direction to be scanned.
	 * @param repository the repository retrieved and parsed from the source control
	 *                   tool (i.e. GIT, SVN...).
	 * @param data       repository data prepared to be displayed by the Sunburst
	 *                   chart. At this point, this is a working draft.
	 * @param stats      the list containing a statistic entry for each class file
	 */
	public void agregateCommits(final String baseDir, final CommitRepository repository,
			final DataChart sunburstData, final List<StatActivity> stats) {

		// This directory contains class within it.
		if ((sunburstData.getClassnames() != null) && !sunburstData.getClassnames().isEmpty()) {
			for (SourceFile source : sunburstData.getClassnames()) {

				final String searchedFile;
				if (baseDir.length() == 0) {
					searchedFile = source.getFilename();
				} else {
					searchedFile = (baseDir.indexOf("root") == 0)
						? (baseDir + sunburstData.getLocation() + "/" + source.getFilename()).substring("root/".length())
						: (baseDir + sunburstData.getLocation() + "/" + source.getFilename()).substring("/".length());
				}
				
				// We retrieve historic information regarding this class name
				Optional<String> optKey;
				optKey = repository.getRepository().keySet().stream().filter(k -> k.equals(searchedFile)).findFirst();
				if (!optKey.isPresent()) {
					if (logger.isErrorEnabled()) {
						logger.error(String.format("Searching %s in", searchedFile));
						repository.getRepository().keySet().stream().forEach(f -> logger.error(f));
					}
					System.out.println(source.getFilename());
					throw new SkillerRuntimeException( String.format("%s not found! (base dir %s, sunB location %s, source filename %s)", searchedFile, baseDir, sunburstData.getLocation(), source.getFilename()));
				}

				final CommitHistory activity = repository.getRepository().get(optKey.get());

				long countCommits = activity.countCommits();
				long countCommitsByActiveDevelopers = activity.countCommitsByActiveDevelopers(staffHandler);

				String fullClass = baseDir + sunburstData.getLocation() + "/" + source.getFilename();
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Adding stat entry for %s : %d / %d", fullClass,
							countCommitsByActiveDevelopers, countCommits));

				}

				stats.add(new StatActivity(fullClass, countCommits, countCommitsByActiveDevelopers));
			}
		}

		if (sunburstData.getChildren() != null) {
			sunburstData.getChildren().stream()
					.forEach(data -> agregateCommits(baseDir + sunburstData.getLocation() + "/", repository, data, stats));
		}
	}

	public String colorOfRisk(final int riskLevel) {
		return this.riskLegends().get(riskLevel).getColor();
	}

	/**
	 * Test if the filename contains the class name
	 * 
	 * @param filename  the complete file name
	 * @param classname the searched class name
	 * @return if {@code true} the file name contains class name, {@code false}
	 *         otherwise
	 */
	public static boolean isClassFile(final String filename, final String classname) {
		int pos = filename.lastIndexOf(classname);
		if (pos == -1) {
			return false;
		} else {
			return classname.equals(filename.substring(pos));
		}
	}

	@Override
	public void setPreviewSettings(DataChart data) {
		if (!data.hasUnknownRiskLevel()) {
			int riskLevel = data.getRiskLevel();
			data.setColor(colorOfRisk(riskLevel));
		}
		if (data.getChildren() != null) {
			data.getChildren().stream().forEach(this::setPreviewSettings);
		}
	}

	@Override
	public int meanTheRisk(final DataChart location) {
		if ((location.getChildren() == null) || (location.getChildren().isEmpty())) {
			return location.getRiskLevel();
		}
		int risk = (int) Math.floor(location.getChildren().stream().mapToInt(this::meanTheRisk).average().getAsDouble());
		if (location.getRiskLevel() == UNKNOWN) {
			location.setRiskLevel(risk);
		} else {
			location.setRiskLevel((int) Math.floor((double) (risk + location.getRiskLevel()) / 2));
		}
		return location.getRiskLevel();
	}

	@Override
	public void evaluateProjectRisk(Project project, DataChart dataTree) {
		
		double sumImportance = dataTree.sum(IMPORTANCE);
		double sumRiskLevelTimesImportance = dataTree.sum(RISKLEVEL_TIMES_IMPORTANCE);
		int projectRisk = (int) (sumRiskLevelTimesImportance / sumImportance);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"Project %s has risk %d ", 
					project.getName(), projectRisk));
		}
		this.projectHandler.saveRisk(project, projectRisk);
	}

}
