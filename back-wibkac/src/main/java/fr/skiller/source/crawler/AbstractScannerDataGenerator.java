package fr.skiller.source.crawler;

import static fr.skiller.Global.LN;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.DataChart;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Committer;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.source.CommitRepository;

/**
 * Abstract class in charge of generating the data collection for the project Sunburst viewer.
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public abstract class AbstractScannerDataGenerator implements RepoScanner {
	
	Random r = new Random();
	
	/**
	 * Service in charge of handling the staff collection.<br/>
	 * This bean in filled by the upper concrete service<br/>
	 * {@link fr.skiller.source.crawler.git.GitScanner#init() GitScanner.init} is the first implementation for Git.
	 */
	protected StaffHandler parentStaffHandler;

	/**
	 * Service in charge of handling the projects.
	 * This bean in filled by the upper concrete service<br/>
	 * {@link fr.skiller.source.crawler.git.GitScanner#init() GitScanner.init} is the first implementation for Git.
	 */
	protected ProjectHandler parentProjectHandler;
	
	private Logger logger = LoggerFactory.getLogger(AbstractScannerDataGenerator.class.getCanonicalName());
	
	@Override
	public RiskDashboard aggregateDashboard(final Project project, final CommitRepository commitRepo) {
		DataChart root = new DataChart("root");
		commitRepo.getRepository().values().stream().forEach(
				commit -> 
				root.injectFile(root, 
						commit.sourcePath.split(File.separator), 
						commit.evaluateDateLastestCommit(),
						commit.committers()));

		
		Set<Committer> ghosts = new HashSet<>();
		commitRepo.unknownContributors().stream()
			.forEach(unknown -> {
				Ghost g = parentProjectHandler.getGhost(project, unknown);
				if (g == null) {
					ghosts.add(new Committer(unknown, false));
				} else {
					if (g.isTechnical()) {
						ghosts.add(new Committer(unknown, true));											
					} else {
						String fullName = parentStaffHandler.getFullname(g.getIdStaff());
						String login = parentStaffHandler.getStaff().get(g.getIdStaff()).getLogin();
						ghosts.add(new Committer(unknown, g.getIdStaff(), fullName, login, g.isTechnical()));
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
	public void evaluateTheRisk(final DataChart sunburstData) {
		if (sunburstData.hasUnknownRiskLevel()) {
			sunburstData.setRiskLevel( (int) (r.nextInt() % 11)); 
		}
		if (sunburstData.getChildren() != null) {
			sunburstData.getChildren().stream().forEach(this::evaluateTheRisk);
		}
	}

}
