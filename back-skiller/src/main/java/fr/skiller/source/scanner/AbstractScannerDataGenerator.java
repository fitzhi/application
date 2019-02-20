package fr.skiller.source.scanner;

import static fr.skiller.Global.LN;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Pseudo;
import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.source.CommitRepository;

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
				root.injectFile(root, 
						commit.sourcePath.split(File.separator), 
						commit.evaluateDateLastestCommit(),
						commit.committers()));

		
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

}
