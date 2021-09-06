package com.fitzhi.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.source.CommitRepository;

public class GhostsListFactory {
    
    /**
     * @param unknownContributors set of unknown  contributors.
	 * @return the list of {@link Ghost detected ghosts}.
	 */
	public static List<Ghost> getInstance(Set<String> unknownContributors) {
		List<Ghost> detectedGhosts = new ArrayList<>();
		unknownContributors
			.stream()
			.forEach(unknown -> detectedGhosts.add(new Ghost(unknown, false)));
		return detectedGhosts;
    }

    /**
     * @param repository the analysis repository.
	 * @return the list of {@link Ghost detected ghosts}.
	 */
	public static List<Ghost> getInstance(CommitRepository repository) {
		List<Ghost> detectedGhosts = new ArrayList<>();
		repository.unknownContributors()
			.stream()
			.forEach(unknown -> {
				Ghost ghost = new Ghost(unknown, false);
				// ghost.setFirstCommit(repository.firstCommit(idStaff));
				detectedGhosts.add(ghost);
			});

		return detectedGhosts;
    }
	
}
