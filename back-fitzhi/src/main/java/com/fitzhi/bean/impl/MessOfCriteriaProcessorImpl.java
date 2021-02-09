/**
 * 
 */
package com.fitzhi.bean.impl;

import static com.fitzhi.Global.LN;
import static com.fitzhi.Global.UNKNOWN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RiskLegend;
import com.fitzhi.data.internal.SourceFile;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.data.source.CommitRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @deprecated
 * <p>
 * This class is deprecated, and replaced by {@link RiskCommitAndDevActiveProcessorImpl}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Service("messOfCriteria")
@Deprecated
public class MessOfCriteriaProcessorImpl implements RiskProcessor {

	/**
	 * Statistic of activity
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	@Data class StatActivity {
		
		/**
		 * Total number of commits submitted.
		 */
		final long countCommits;
		
		/**
		 * Number of commits submitted by active developers.
		 */
		final long countCommitsByActiveDevelopers;
		
		/**
		 * Is the last contributor on this source file still active ? 
		 */
		final boolean isLastCommiterStillActive;

	}

	/**
	 * Bean in charge of handling staff.
	 */
	@Autowired StaffHandler staffHandler;

	@Override
	public Map<Integer, RiskLegend> riskLegends() {
	
		final Map<Integer, RiskLegend> explanations = new HashMap<>();
		
		explanations.put (0, new RiskLegend( 0, "darkGreen", 
				"All commits of all sources have been submitted by developers still active in the staff team."));
		explanations.put (1, new RiskLegend( 1, "ForestGreen",
				"90% of commits have been made by active developers in the staff team."+
	      		"NB : It's the calculated mean on all sources in the directory." +
	      		"AND the last commits have been submitted by them."));
		explanations.put (2, new RiskLegend( 2, "limeGreen",
				"80% of all commits have been made by active developers in the staff team."+
				"NB : It's the calculated mean of all sources in the directory."+
				"AND the last commits have been submitted by them."));
		explanations.put (3, new RiskLegend( 3, "lime",
				"80% of all commits have been made by active developers in the staff team. Some of source files are only covered at 50%"));
		explanations.put (4, new RiskLegend( 4, "lightGreen",
				"80% of all commits have been made by active developers in the staff team."));
		explanations.put (5, new RiskLegend( 5, "yellow",
				"60% of all commits have been made by active developers in the staff team."));
		explanations.put (6, new RiskLegend( 6, "orange",
				"60% of all commits have been made by active developers in the staff team."+
				"AND there are some file(s)  in this directory, without remaining active developers."));
		explanations.put (7, new RiskLegend( 7, "darkOrange",
				"33% of all commits have been submitted by active developers in the staff team."));
		explanations.put (8, new RiskLegend( 8, "lightCoral",
				"20% have been made by active developers in the staff team."));
		explanations.put (9, new RiskLegend( 9, "crimson",
				"10% have been made by active developers in the staff team."+
				"AND none of them have submitted the most recent commits on the source files."));
		explanations.put (10, new RiskLegend( 10, "darkRed",
				"It's no more a risk. It is a problem. None of the current developers in the company have worked on the source files."));

		return explanations;
	}

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
	public void evaluateTheRisk(
			final CommitRepository repository, 
			final DataChart sunburstData) {
		
		final List<StatActivity> stats = new ArrayList<>();
		
		// This directory contains class within it.
		if ((sunburstData.getClassnames() != null) && !sunburstData.getClassnames().isEmpty()) {
			for (SourceFile source : sunburstData.getClassnames()) {
	
				// We retrieve historic information regarding this class name
				Optional<String> optKey;
				optKey = repository.getRepository()
						.keySet()
						.stream()
						.filter(k -> isClassFile(k, source.getFilename()))
						.findFirst();
				if (!optKey.isPresent()) {
					throw new ApplicationRuntimeException(source.getFilename() + " not found!");
				}
				
				final CommitHistory activity = repository.getRepository().get(optKey.get());
				
				long numberOfCommits = 	activity.countCommits();
				long countCommitsByActiveDevelopers = 	activity.countCommitsByActiveDevelopers(staffHandler);

				final int idStaff = activity.ultimateContributor();
				boolean lastContributorStillPresent = 
						(idStaff != UNKNOWN) || staffHandler.isActive(idStaff);
	
				stats.add(new StatActivity(numberOfCommits, countCommitsByActiveDevelopers, lastContributorStillPresent));			
			}

			int percentageOfCommitsMadeByActiveDevelopers = (int) Math.floor (
					(double) stats.stream().mapToLong(stat -> stat.countCommitsByActiveDevelopers).sum() * 100
					/ stats.stream().mapToLong(stat -> stat.countCommits).sum() );

			// There is at least one commit, that has been submitted by a developer who quits 
			boolean hasLostARecentContributor = stats.stream()
					.anyMatch(stat -> !stat.isLastCommiterStillActive);
			
			// Lookup if some source files in this directory are only 55% covered by active developers
			boolean hasASourceFileUnder50pct = 
					stats.stream()
					.mapToLong(stat -> stat.countCommitsByActiveDevelopers*100 / stat.countCommits)
					.anyMatch(i -> i <= 50);
			
			boolean hasSourceWithoutContributor = 
					stats.stream()
					.mapToLong(stat -> stat.countCommitsByActiveDevelopers)
					.anyMatch(i -> i == 0);
			
			if (log.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder();
				sb.append(LN+"dir : " + sunburstData.getLocation() + LN)
					.append("percentageOfCommitsMadeByActiveDevelopers : " + percentageOfCommitsMadeByActiveDevelopers + LN)
					.append("hasLostARecentContributor : " + hasLostARecentContributor + LN)
					.append("hasASourceFileUnder50pct : " + hasASourceFileUnder50pct + LN)
					.append("hasSourceWithoutContributor : " + hasSourceWithoutContributor + LN);
				log.debug(sb.toString());
			}
			
			setRiskLevel(sunburstData, 
					percentageOfCommitsMadeByActiveDevelopers, 
					hasLostARecentContributor, 
					hasASourceFileUnder50pct,
					hasSourceWithoutContributor);
		}
		
		if (sunburstData.getChildren() != null) {
			sunburstData.getChildren().stream().forEach(data -> evaluateTheRisk(repository, data));
		}
		
	}
	
	/**
	 * 
	 * @param sunburstData data representing a directory of the repository 
	 * @param percentageOfCommitsMadeByActiveDevelopers percentage of commits made by active developers ;-) 
	 * @param hasLostARecentContributor Did this directory loose a recent active contributor ? Last commit has been submitted by an absent. 
	 * @param hasASourceFileUnder50pct This directory has a source file which contains lest that 50% of active developers within the company. 
	 * @param hasSourceWithoutContributor This directory has a source file which contains no more active developers.
	 */
	private void setRiskLevel(
			final DataChart sunburstData, 
			final int percentageOfCommitsMadeByActiveDevelopers,
			boolean hasLostARecentContributor,
			boolean hasASourceFileUnder50pct, 
			boolean hasSourceWithoutContributor) {
		
		// Everyone in the team is steam present, and active in the company.
		if (percentageOfCommitsMadeByActiveDevelopers == 100) {
			sunburstData.setRiskLevel(0);
			return;
		}
		
		if ((percentageOfCommitsMadeByActiveDevelopers >= 90) && !hasLostARecentContributor) {
			sunburstData.setRiskLevel(1);
			return;
		}
		
		if ((percentageOfCommitsMadeByActiveDevelopers >= 80) && !hasLostARecentContributor) {
			sunburstData.setRiskLevel(2);
			return;
		}
		
		if ((percentageOfCommitsMadeByActiveDevelopers >= 80) && !hasASourceFileUnder50pct ) {
			sunburstData.setRiskLevel(3);
			return;
		}

		if (percentageOfCommitsMadeByActiveDevelopers >= 80)  {
			sunburstData.setRiskLevel(4);
			return;
		}
		
		if ((percentageOfCommitsMadeByActiveDevelopers >= 60) && !hasSourceWithoutContributor) {
			sunburstData.setRiskLevel(5);
			return;
		}
	
		if (percentageOfCommitsMadeByActiveDevelopers >= 60) {
			sunburstData.setRiskLevel(6);
			return;
		}
		
		if (percentageOfCommitsMadeByActiveDevelopers >= 33) {
			sunburstData.setRiskLevel(7);
			return;
		}
		
		if ((percentageOfCommitsMadeByActiveDevelopers >= 20) && !hasLostARecentContributor) {
			sunburstData.setRiskLevel(8);
			return;
		}

		if (percentageOfCommitsMadeByActiveDevelopers >= 10) {
			sunburstData.setRiskLevel(9);
			return;
		}
		
		// Nobody in the company has worked on all files in this directory.
		sunburstData.setRiskLevel(10);
	}
	
	public String colorOfRisk(final int riskLevel) {
		return this.riskLegends().get(riskLevel).getColor();
	}
	
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
		if ( (location.getChildren() == null) || (location.getChildren().isEmpty()) ) {
			return location.getRiskLevel();
		}
		int risk = (int) Math.floor(location.getChildren().stream().mapToInt(this::meanTheRisk).average().getAsDouble());
		if (location.getRiskLevel() == UNKNOWN) {
			location.setRiskLevel(risk);
			return risk;
		} 
		return location.getRiskLevel();
	}

	@Override
	public void evaluateTheRisk(CommitRepository repository, DataChart data,
			List<com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity> statsCommit) {
		throw new ApplicationRuntimeException("Should not pass here!");
	}

	@Override
	public void evaluateProjectRisk(Project project, DataChart dataTree) {
		throw new ApplicationRuntimeException("Should not pass here!");
	}

	
}
