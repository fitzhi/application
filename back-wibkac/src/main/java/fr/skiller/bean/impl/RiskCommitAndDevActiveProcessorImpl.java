/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Global.LN;
import static fr.skiller.Global.UNKNOWN;
import static fr.skiller.data.internal.DataChartTypeData.IMPORTANCE;
import static fr.skiller.data.internal.DataChartTypeData.RISKLEVEL_TIMES_IMPORTANCE;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Generated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.RiskProcessor;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.DataChart;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.RiskLegend;
import fr.skiller.data.internal.SourceFile;
import fr.skiller.data.source.CommitHistory;
import fr.skiller.data.source.CommitRepository;

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
	public class StatActivity {

		/**
		 * Filename.
		 */
		private String filename;

		/**
		 * Total number of the commits submitted.
		 */
		private long countCommits;

		/**
		 * Number of the commits submitted by active developers.
		 */
		private long countCommitsByActiveDevelopers;

		/**
		 * @param fileName
		 * @param countCommits
		 * @param countCommitsByActiveDevelopers
		 */
		StatActivity(final String fileName, final long countCommits, final long countCommitsByActiveDevelopers) {
			super();
			this.setFilename(fileName);
			this.setCountCommits(countCommits);
			this.setCountCommitsByActiveDevelopers(countCommitsByActiveDevelopers);
		}

		@Override
		@Generated("eclipse")
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (int) (getCountCommits() ^ (getCountCommits() >>> 32));
			result = prime * result
					+ (int) (getCountCommitsByActiveDevelopers() ^ (getCountCommitsByActiveDevelopers() >>> 32));
			result = prime * result + ((getFilename() == null) ? 0 : getFilename().hashCode());
			return result;
		}

		@Override
		@Generated("eclipse")
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StatActivity other = (StatActivity) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (getCountCommits() != other.getCountCommits())
				return false;
			if (getCountCommitsByActiveDevelopers() != other.getCountCommitsByActiveDevelopers())
				return false;
			if (getFilename() == null) {
				if (other.getFilename() != null)
					return false;
			} else if (!getFilename().equals(other.getFilename()))
				return false;
			return true;
		}

		private RiskCommitAndDevActiveProcessorImpl getOuterType() {
			return RiskCommitAndDevActiveProcessorImpl.this;
		}

		@Override
		public String toString() {
			return "StatActivity [filename=" + getFilename() + ", countCommits=" + getCountCommits()
					+ ", countCommitsByActiveDevelopers=" + getCountCommitsByActiveDevelopers() + "]";
		}

		/**
		 * @return the filename
		 */
		public String getFilename() {
			return filename;
		}

		/**
		 * @param filename the filename to set
		 */
		public void setFilename(String filename) {
			this.filename = filename;
		}

		/**
		 * @return the countCommits<br/>
		 *         <i>(Total number of the commits submitted)</i>
		 */
		public long getCountCommits() {
			return countCommits;
		}

		/**
		 * @param countCommits the countCommits to set
		 */
		public void setCountCommits(long countCommits) {
			this.countCommits = countCommits;
		}

		/**
		 * @return the countCommitsByActiveDevelopers<br/>
		 *         <i>(Number of the commits submitted by active developers)</i>
		 */
		public long getCountCommitsByActiveDevelopers() {
			return countCommitsByActiveDevelopers;
		}

		/**
		 * @param countCommitsByActiveDevelopers the countCommitsByActiveDevelopers to
		 *                                       set
		 */
		public void setCountCommitsByActiveDevelopers(long countCommitsByActiveDevelopers) {
			this.countCommitsByActiveDevelopers = countCommitsByActiveDevelopers;
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

		explanations.put(0, new RiskLegend(0, "darkGreen",
				"commits have been submitted by active developers. Perfect level of proficiency on this project"));
		explanations.put(1, new RiskLegend(1, "ForestGreen",
				"At least, 90% of the commits have been submitted by active developers."));
		explanations.put(2, new RiskLegend(2, "limeGreen",
				"At least, 80% of the commits have been submitted by active developers."));
		explanations.put(3,
				new RiskLegend(3, "lime", "At least, 70% of the commits have been submitted by active developers."));
		explanations.put(4, new RiskLegend(4, "lightGreen",
				"At least, 60% of the commits have been submitted by active developers."));
		explanations.put(5,
				new RiskLegend(5, "yellow", "At least, 50% of the commits have been submitted by active developers."));
		explanations.put(6,
				new RiskLegend(6, "orange", "At least, 40% of the commits have been submitted by active developers."));
		explanations.put(7, new RiskLegend(7, "darkOrange",
				"At least, 30% of the commits have been submitted by active developers."));
		explanations.put(8, new RiskLegend(8, "lightCoral",
				"At least, 20% of the commits have been submitted by active developers."));
		explanations.put(9,
				new RiskLegend(9, "crimson", "At least, 10% of the commits have been submitted by active developers."));
		explanations.put(10, new RiskLegend(10, "darkRed",
				"commits have been proceeded by INACTIVE developers. Everyone is newbie on this project."));

		return explanations;
	}

	/**
	 * Evaluate the level of risk on all entries in the repository from the
	 * staff/level point of view.<br/>
	 * The scale of risks contains 10 levels + 1 problem: <br/>
	 * 
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
						|| (entry.getFilename().indexOf(File.separator, dir.length() + 1) == -1)))
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
						|| (entry.getFilename().indexOf(File.separator, dir.length() + 1) == -1)))
				.mapToLong(StatActivity::getCountCommitsByActiveDevelopers).sum();
	}

	/**
	 * Evaluate the risk for the active developers.<br/>
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
