package fr.skiller.source.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Pseudo;
import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.internal.Unknown;
import fr.skiller.data.source.CommitHistory;
import fr.skiller.data.source.CommitRepository;

import static fr.skiller.Global.UNKNOWN;
import static fr.skiller.Global.LN;

/**
 * Abstract class in charge of generating the data collection for the project Sunburst viewer.
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public abstract class AbstractScannerDataGenerator implements RepoScanner {
	
	/**
	 * Service in charge of handling the staff collection.<br/>
	 * This bean in filled by the upper concrete service<br/>
	 * {@link fr.skiller.source.scanner.git.GitScanner#init() GitScanner.init} is the first implementation for Git.
	 */
	protected StaffHandler staffHandler;

	/**
	 * Service in charge of handling the projects.
	 * This bean in filled by the upper concrete service<br/>
	 * {@link fr.skiller.source.scanner.git.GitScanner#init() GitScanner.init} is the first implementation for Git.
	 */
	protected ProjectHandler projectHandler;
	
	private Logger logger = LoggerFactory.getLogger(AbstractScannerDataGenerator.class.getCanonicalName());

	@Override
	public RiskDashboard aggregateDashboard(final Project project, final CommitRepository commitRepo) {
		RiskChartData root = new RiskChartData("root");
		commitRepo.getRepository().values().stream().forEach(
				commit -> 
				root.injectFile(root, commit.sourcePath.split(File.separator), commit.evaluateDateLastestCommit()));
		
		Set<Pseudo> ghosts = new HashSet<Pseudo>();
		commitRepo.unknownContributors().stream()
			.forEach(unknown -> {
				
				Ghost g = projectHandler.getGhost(project, unknown);
				if (g == null) {
					ghosts.add(new Pseudo(unknown, false));
				} else {
					if (g.technical) {
						ghosts.add(new Pseudo(unknown, true));											
					} else {
						String fullName = staffHandler.getFullname(g.idStaff);
						String login = staffHandler.getStaff().get(g.idStaff).login;
						ghosts.add(new Pseudo(unknown, g.idStaff, fullName, login, g.technical));
					}
				}
			});
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder(LN);
			ghosts.stream().forEach(g -> sb.append(g).append(LN));
			logger.debug(sb.toString());
		}
		return new RiskDashboard(root, ghosts);
	}

	/**
	 * For this first testing version. The risk will be randomly estimated.
	 * @param all or part of the source directories
	 */
	public void evaluateTheRisk(final RiskChartData sunburstData) {
		if (sunburstData.hasUnknownRiskLevel()) {
			sunburstData.setRiskLevel( (int) (Math.random()*1000 % 11)); 
		}
		if (sunburstData.children != null) {
			sunburstData.children.stream().forEach(dir -> evaluateTheRisk(dir));
		}
	}

	/**
	 * Statistic of activity
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	class StatActivity {
		
		/**
		 * Total number of commits submitted.
		 */
		public long countCommits;
		
		/**
		 * Number of commits submitted by active developers.
		 */
		public long countCommitsByActiveDevelopers;
		
		/**
		 * Is the last contributor on this source file still active ? 
		 */
		public boolean isLastCommiterStillActive;

		/**
		 * @param countCommits
		 * @param countCommitsByActiveDevelopers
		 * @param isLastCommiterStillActive
		 */
		public StatActivity(long countCommits, long countCommitsByActiveDevelopers, boolean isLastCommiterStillActive) {
			super();
			this.countCommits = countCommits;
			this.countCommitsByActiveDevelopers = countCommitsByActiveDevelopers;
			this.isLastCommiterStillActive = isLastCommiterStillActive;
		}
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
	public void evaluateTheRisk(final CommitRepository repository, final RiskChartData sunburstData)  {
		
		if (staffHandler == null) {
			throw new RuntimeException("staffHandler should not be null at this point");
		}
		
		final List<StatActivity> stats = new ArrayList<StatActivity>();
		
		// This directory has no files within it.
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
				
				long numberOfCommits = 	activity.countCommits();
				long countCommitsByActiveDevelopers = 	activity.countCommitsByActiveDevelopers(staffHandler);

				final int idStaff = activity.ultimateContributor();
				boolean lastContributorStillPresent = (idStaff != UNKNOWN) ?
						staffHandler.isActive(idStaff) : false;
	
				stats.add(new StatActivity(numberOfCommits, countCommitsByActiveDevelopers, lastContributorStillPresent));			
			}

			int percentageOfCommitsMadeByActiveDevelopers = (int) Math.floor (
					stats.stream().mapToLong(stat -> stat.countCommitsByActiveDevelopers).sum() * 100
					/ stats.stream().mapToLong(stat -> stat.countCommits).sum() );

			// There is at least one commit, that has been submitted by a developer who quits 
			boolean hasLostARecentContributor = stats.stream()
					.filter(stat -> !stat.isLastCommiterStillActive)
					.findFirst()
					.isPresent();
			
			// Lookup if some source files in this directory are only 55% covered by active developers
			boolean hasASourceFileUnder50pct = 
					stats.stream()
					.mapToLong(stat -> stat.countCommitsByActiveDevelopers*100 / stat.countCommits)
					.filter(i -> i <= 50)
					.findAny().isPresent();
			
			boolean hasSourceWithoutContributor = 
					stats.stream()
					.mapToLong(stat -> stat.countCommitsByActiveDevelopers)
					.filter(i -> i == 0)
					.findAny().isPresent();
			
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder();
				sb.append(LN+"dir : " + sunburstData.location + LN)
					.append("percentageOfCommitsMadeByActiveDevelopers : " + percentageOfCommitsMadeByActiveDevelopers + LN)
					.append("hasLostARecentContributor : " + hasLostARecentContributor + LN)
					.append("hasASourceFileUnder50pct : " + hasASourceFileUnder50pct + LN)
					.append("hasSourceWithoutContributor : " + hasSourceWithoutContributor + LN);
				logger.debug(sb.toString());
			}
			
			setRiskLevel(sunburstData, 
					percentageOfCommitsMadeByActiveDevelopers, 
					hasLostARecentContributor, 
					hasASourceFileUnder50pct,
					hasSourceWithoutContributor);
		}
		
		if (sunburstData.children != null) {
			sunburstData.children.stream().forEach(data -> evaluateTheRisk(repository, data));
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

	/**
	 * 
	 * @param sunburstData data representing a directory of the repository 
	 * @param percentageOfCommitsMadeByActiveDevelopers percentage of commits made by active developers ;-) 
	 * @param hasLostARecentContributor Did this directory loose a recent active contributor ? Last commit has been submitted by an absent. 
	 * @param hasASourceFileUnder50pct This directory has a source file which contains lest that 50% of active developers within the company. 
	 * @param hasSourceWithoutContributor This directory has a source file which contains no more active developers.
	 */
	private void setRiskLevel(
			final RiskChartData sunburstData, 
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
	
	final String colorOfRisk[] = {
            "darkGreen","ForestGreen","limeGreen", "Lime", "lightGreen","yellow","orange","darkOrange","lightCoral","crimson","darkRed"
	};
	
	@Override
	public void setPreviewSettings(RiskChartData data) {
		if (!data.hasUnknownRiskLevel()) {
			int riskLevel = data.getRiskLevel();
			data.color = colorOfRisk[riskLevel];
		}
		if (data.children != null) {
			data.children.stream().forEach(dir -> setPreviewSettings(dir));
		}
	}
}
