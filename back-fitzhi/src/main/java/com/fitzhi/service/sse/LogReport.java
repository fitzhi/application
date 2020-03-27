package com.fitzhi.service.sse;

import com.fitzhi.data.external.ActivityLog;

import reactor.core.publisher.Flux;

public interface LogReport {

	/**
	 * Emit distinct {@link ActivityLog} every second.
	 * @param operation the current underlying operation
	 * @param idProject the project identifier
	 * @return a {@link Flux} of {@link ActivityLog}
	 */
	Flux<ActivityLog> sunburstGenerationLogNext(String operation, int idProject);
	
}
