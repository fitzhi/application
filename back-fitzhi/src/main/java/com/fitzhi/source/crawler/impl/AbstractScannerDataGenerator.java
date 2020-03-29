package com.fitzhi.source.crawler.impl;

import static com.fitzhi.Global.INTERNAL_FILE_SEPARATOR;
import static com.fitzhi.Global.LN;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.fitzhi.SkillerRuntimeException;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Committer;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.StaffActivitySkill;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.SourceChange;
import com.fitzhi.source.crawler.git.SourceFileHistory;

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
	 * This bean in set by the upper concrete service.
	 * </p>
	 * We retrieve the {@link StaffHandler} interface from the upper-class which has been injected in the Spring-way..
	 */
	protected abstract StaffHandler staffHandler();

	/**
	 * <p>
	 * Service in charge of handling the project collection.
	 * This bean in set by the upper concrete service.
	 * </p>
	 * We retrieve the {@link ProjectHandler} interface from the upper-class which has been injected in the Spring-way..
	 */
	protected abstract ProjectHandler projectHandler();
		
	/**
	 * <p>
	 * Service in charge of handling the skill collection.
	 * This bean in set by the upper concrete service.
	 * </p>
	 * We retrieve the {@link SkillHandler} interface from the upper-class which has been injected in the Spring-way..
	 */
	protected abstract SkillHandler skillHandler();
	
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
					String login = staffHandler().getStaff().get(ghost.getIdStaff()).getLogin();
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

	@Override
	public void gatherContributorsActivitySkill(List<Contributor> contributors, SourceControlChanges changes, Set<String> pathSourceFileNames) {
		for (Skill skill : skillHandler().getSkills().values()) {
			for(String path : pathSourceFileNames) {
				if (skillHandler().isSkillDetectedWithFilename(skill, path)) {
					SourceFileHistory history = changes.getChanges().get(path);
					// Some change like the rename of a directory might not be in the history  
					if ((history == null) && log.isDebugEnabled()) {
						log.debug(String.format("%s not retrieved in the history of changes", path));
					} 
					if (history != null) {
						history.getChanges().stream().forEach(change -> {
							takeInAccount(skill, contributors, change);
						});
					}
				}
			}
		}
	}

	private void takeInAccount(Skill skill, List<Contributor> contributors, SourceChange change) {
		Optional<Contributor> oContributor = contributors.stream().filter(contributor -> contributor.getIdStaff() == change.getIdStaff()).findFirst();
		if (oContributor.isPresent()) {
			StaffActivitySkill sas = oContributor.get().getStaffActivitySkill().get(skill.getId());
			if (sas == null) {
				oContributor.get().getStaffActivitySkill().put(skill.getId(), 
						new StaffActivitySkill(skill.getId(), change.getIdStaff(), change.getDateCommit(), change.getDateCommit(), 1));
			} else {
				sas.incChange();
				sas.takeInAccount(change.getDateCommit());
			}
		}
	}
	
}
