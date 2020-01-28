package com.tixhi.source.crawler.git;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * History of a source file.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class SourceFileHistory {

	/**
	 * A numeric value representing the importance of this source file regarding the whole repository.
	 */
	private long importance;

	/**
	 * List of changes submitted on a source file.
	 */
	private List<SourceChange> changes;
	
	/**
	 * Empty constructor.
	 */
	public SourceFileHistory() {
		this(new ArrayList<SourceChange>());
	}

	/**
	 * Constructor with an already loaded changes collection.
	 */
	public SourceFileHistory(List<SourceChange> changes) {
		this.changes = changes;
	}

	/**
	 * @param idStaff the staff identifier
	 * @return {@code true} if the given staff has worked on this file.
	 */
	public boolean isInvolved(int idStaff) {
		return changes.stream().anyMatch(sc -> idStaff == sc.getIdStaff());
	}
}
