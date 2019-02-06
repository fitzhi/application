/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Global.LN;
import static fr.skiller.Global.UNKNOWN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SystemPropertyUtils;

import fr.skiller.Global;
import fr.skiller.bean.RiskProcessor;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.RiskLegend;
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
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	public class StatActivity {
		
		/**
		 * Filename.
		 */
		public String filename;

		/**
		 * Total number of the commits submitted.
		 */
		public long countCommits;
		
		/**
		 * Number of the commits submitted by active developers.
		 */
		public long countCommitsByActiveDevelopers;
		
		/**
		 * @param fileName
		 * @param countCommits
		 * @param countCommitsByActiveDevelopers
		 */
		StatActivity(final String fileName, final long countCommits, final long countCommitsByActiveDevelopers) {
			super();
			this.filename = fileName;
			this.countCommits = countCommits;
			this.countCommitsByActiveDevelopers = countCommitsByActiveDevelopers;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (int) (countCommits ^ (countCommits >>> 32));
			result = prime * result + (int) (countCommitsByActiveDevelopers ^ (countCommitsByActiveDevelopers >>> 32));
			result = prime * result + ((filename == null) ? 0 : filename.hashCode());
			return result;
		}

		@Override
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
			if (countCommits != other.countCommits)
				return false;
			if (countCommitsByActiveDevelopers != other.countCommitsByActiveDevelopers)
				return false;
			if (filename == null) {
				if (other.filename != null)
					return false;
			} else if (!filename.equals(other.filename))
				return false;
			return true;
		}

		private RiskCommitAndDevActiveProcessorImpl getOuterType() {
			return RiskCommitAndDevActiveProcessorImpl.this;
		}

		@Override
		public String toString() {
			return "StatActivity [filename=" + filename + ", countCommits=" + countCommits
					+ ", countCommitsByActiveDevelopers=" + countCommitsByActiveDevelopers + "]";
		}
		
		
	}

	/**
	 * Bean in charge of handling staff.
	 */
	@Autowired StaffHandler staffHandler;

 	/**
 	 * The logger for the Risk Surveyor.
 	 */
	Logger logger = LoggerFactory.getLogger(RiskCommitAndDevActiveProcessorImpl.class.getCanonicalName());

	@Override
	public Map<Integer, RiskLegend> riskLegends() {
	
		final Map<Integer, RiskLegend> explanations = new HashMap<Integer, RiskLegend>();
		
		explanations.put (0, new RiskLegend( 0, "darkGreen", 
				"commits have been submitted by active developers. Perfect level of proficiency on this project"));
		explanations.put (1, new RiskLegend( 1, "ForestGreen",
				"At least, 90% of the commits have been submitted by active developers."));
		explanations.put (2, new RiskLegend( 2, "limeGreen",
				"At least, 80% of the commits have been submitted by active developers."));
		explanations.put (3, new RiskLegend( 3, "lime",
				"At least, 70% of the commits have been submitted by active developers."));
		explanations.put (4, new RiskLegend( 4, "lightGreen",
				"At least, 60% of the commits have been submitted by active developers."));
		explanations.put (5, new RiskLegend( 5, "yellow",
				"At least, 50% of the commits have been submitted by active developers."));
		explanations.put (6, new RiskLegend( 6, "orange",
				"At least, 40% of the commits have been submitted by active developers."));
		explanations.put (7, new RiskLegend( 7, "darkOrange",
				"At least, 30% of the commits have been submitted by active developers."));
		explanations.put (8, new RiskLegend( 8, "lightCoral",
				"At least, 20% of the commits have been submitted by active developers."));
		explanations.put (9, new RiskLegend( 9, "crimson",
				"At least, 10% of the commits have been submitted by active developers."));
		explanations.put (10, new RiskLegend( 10, "darkRed",
				"commits have been proceeded by INACTIVE developers. Everyone is newbie on this project."));

		return explanations;
	}

	/**
     * Evaluate the level of risk on all entries in the repository from the staff/level point of view.<br/>
     * The scale of risks contains 10 levels + 1 problem: <br/>
	 * @param repository the repository retrieved and parsed from the source control tool (i.e. GIT, SVN...).
	 * @param data repository data prepared to be displayed by the Sunburst chart 
	 */
	public void evaluateTheRisk(
			final CommitRepository repository, 
			final RiskChartData data) {
		
		List<StatActivity> stats = new ArrayList<StatActivity>();
		agregateCommits(new String(), repository, data, stats);
		
		evaluateActiveDevelopersCoverage("", data, stats);
	}	

	/**
	 * Count commits submitted from a directory.
	 * @param dir the directory, and its directories, from where we sum the number of the commits submitted
	 * @param stats the list containing a statistic entry for each class file 
	 * @return the resulting count
	 */
	long agregateCountCommits(final String dir, final List<StatActivity> stats) {
		return stats.stream().filter(entry -> entry.filename.indexOf(dir)==0).mapToLong(entry -> entry.countCommits).sum();
	}

	/**
	 * Count commits submitted from a directory by active developers.
	 * @param dir the directory, and its directories, from where we sum the number of commits submitted
	 * @param stats the list containing a statistic entry for each class file 
	 * @return the resulting count
	 */
	long agregateCountCommitsByActiveDevelopers(
			final String dir, 
			final List<StatActivity> stats) {
		return stats.stream().filter(entry -> entry.filename.indexOf(dir)==0).mapToLong(entry -> entry.countCommitsByActiveDevelopers).sum();
	}

	/**
	 * Evaluate the risk for the active developers.
	 * @param dir directory containing the passed 
	 * @param data location data (containing the relative location, the risks & colors, and its children)
	 * @param stats the list containing a statistic entry for each class file 
	 */
	void evaluateActiveDevelopersCoverage(
			final String dir,
			final RiskChartData data, 
			final List<StatActivity> stats) {
	
		long countCommits = agregateCountCommits(dir+data.location, stats);
		long countCommitsByActiveDevelopers = agregateCountCommitsByActiveDevelopers(dir+data.location, stats);
		int riskLevel = -1;
		if (countCommits > 0) {
			riskLevel = (int) Math.ceil( ((1 -  ((double) countCommitsByActiveDevelopers / (double) countCommits)) * 10));
		} 

		if (logger.isDebugEnabled()) { 
			StringBuilder sb = new StringBuilder(LN);
			sb.append("Evaluating the risk for " + dir + data.location).append(LN);
			sb.append("countCommits : " + countCommits).append(LN);
			sb.append("countCommitsByActiveDevelopers : " + countCommitsByActiveDevelopers).append(LN);
			sb.append("riskLevel evaluated : " + riskLevel).append(LN);
			logger.debug(sb.toString());
		}
		data.setRiskLevel(riskLevel);
		
		if (data.children != null) {
			data.children.stream().forEach(dat -> evaluateActiveDevelopersCoverage(dir + data.location + "/", dat, stats));
		}
	}
	
	/**
     * Aggregate the commits by class name. <i>This method is recursive!</i>
     * @param baseDir the direction to be scanned.
	 * @param repository the repository retrieved and parsed from the source control tool (i.e. GIT, SVN...).
	 * @param data repository data prepared to be displayed by the Sunburst chart. At this point, this is a working draft.
	 * @param stats the list containing a statistic entry for each class file 
	 */
	public void agregateCommits(
			final String baseDir,
			final CommitRepository repository, 
			final RiskChartData sunburstData,
			final List<StatActivity> stats) {
		
		// This directory contains class within it.
		if ((sunburstData.getClassnames() != null) && !sunburstData.getClassnames().isEmpty()) {
			for (String classname : sunburstData.getClassnames()) {
				// We retrieve historic information regarding this class name
				Optional<String> optKey;
				optKey = repository.getRepository()
						.keySet()
						.stream()
						.filter(k -> isClassFile(k, classname))
						.findFirst();
				if (!optKey.isPresent()) {
					throw new RuntimeException(classname + " not found!");
				}
				
				final CommitHistory activity = repository.getRepository().get(optKey.get());
				
				long countCommits = activity.countCommits();
				long countCommitsByActiveDevelopers = 	activity.countCommitsByActiveDevelopers(staffHandler);

				String fullClass = baseDir.toString() + sunburstData.location + "/"+classname;
				if (logger.isDebugEnabled()) {
					logger.debug("Adding stat entry for " + fullClass + " : " + countCommitsByActiveDevelopers + "/" + countCommits);
					
				}
				
				stats.add(new StatActivity(fullClass, countCommits, countCommitsByActiveDevelopers));			
			}
		} 
		
		if (sunburstData.children != null) {
			sunburstData.children.stream().forEach(data -> agregateCommits(baseDir + sunburstData.location + "/", repository, data, stats));
		}
	}
	
	public String colorOfRisk(final int riskLevel) {
		return this.riskLegends().get(riskLevel).color;
	};
	
	/**
	 * Test if the filename contains the class name
	 * @param filename the complete file name
	 * @param classname the searched class name
	 * @return if {@code true} the file name contains class name, {@code false} otherwise 
	 */
	public static boolean isClassFile (final String filename, final String classname) {
		int pos = filename.lastIndexOf(classname);
		if (pos == -1) {
			return false;
		} else {
			return classname.equals(filename.substring(pos));
		}
	}
	
	@Override
	public void setPreviewSettings(RiskChartData data) {
		if (!data.hasUnknownRiskLevel()) {
			int riskLevel = data.getRiskLevel();
			data.color = colorOfRisk(riskLevel);
		}
		if (data.children != null) {
			data.children.stream().forEach(dir -> setPreviewSettings(dir));
		}
	}

	@Override
	public int meanTheRisk(final RiskChartData location) {
		if ( (location.children == null) || (location.children.size() == 0) ) {
			return location.getRiskLevel();
		}
		int risk = (int) Math.floor(location.children.stream().mapToInt(child -> meanTheRisk(child)).average().getAsDouble());
		if (location.getRiskLevel() == UNKNOWN) {
			location.setRiskLevel(risk);
			return risk;
		} 
		return location.getRiskLevel();
	}
}
