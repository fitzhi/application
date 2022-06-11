package com.fitzhi.data.source;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Container of the Repository commits.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@NoArgsConstructor
@AllArgsConstructor
public @Data class DataCommitRepository {

	/**
	 * Map of commits identified by source code path.
	 */
	Map<String, CommitHistory> repo = new HashMap<>();
	
	/**
	 * This set contains the developers/contributors retrieved in the repository 
	 * but unrecognized during the parsing process.
	 */
	Set<String> unknownContributors = new HashSet<>();

}
