package com.fitzhi.source.crawler.impl;

import static com.fitzhi.Global.INTERNAL_FILE_SEPARATOR;
import static com.fitzhi.Global.LN;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Committer;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.GitCrawler;

import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class in charge of generating the data collection for the project Sunburst viewer.
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
public abstract class AbstractScannerDataGenerator implements RepoScanner {
	
	private Random r = new Random();
	
	/**
	 * <p>
	 * Service in charge of handling the staff collection.
	 * This bean in set by the upper concrete service
	 * </p>
	 * {@link GitCrawler#init() GitScanner.init} is the first implementation for Git.
	 */
	protected StaffHandler parentStaffHandler;

	/**
	 * Service in charge of handling the projects.
	 * This bean in filled by the upper concrete service<br/>
	 * {@link fr.skiller.source.crawler.git.TreeWalkGitCrawler#init() GitScanner.init} is the first implementation for Git.
	 */
	protected ProjectHandler parentProjectHandler;
		
	@Override
	public RiskDashboard aggregateDashboard(final Project project, final CommitRepository commitRepo) {
		DataChart root = new DataChart("root");
		commitRepo.getRepository().values().stream().forEach(
				commit -> 
				root.injectFile(root, 
						commit.sourcePath.split(INTERNAL_FILE_SEPARATOR), 
						commit.getImportance(), 
						commit.evaluateDateLastestCommit(),
						commit.committers()));

		
		List<Committer> ghosts = new ArrayList<>();
		for (Ghost ghost : project.getGhosts()) {
			if (ghost.isTechnical()) {
				ghosts.add (new Committer(ghost.getPseudo(), true));
			} else {
				if (ghost.getIdStaff() > 0) {
					String login = parentStaffHandler.getStaff().get(ghost.getIdStaff()).getLogin();
					ghosts.add(new Committer(ghost.getPseudo(), ghost.getIdStaff(), login, false));
				} else {
					ghosts.add(new Committer(ghost.getPseudo(), false));
				}
			}
		};
		
		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder(LN);
			ghosts.stream().forEach(g -> sb.append(g).append(LN));
			log.debug(sb.toString());
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
