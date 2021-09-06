package com.fitzhi.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fitzhi.data.internal.Ghost;

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
	
}
