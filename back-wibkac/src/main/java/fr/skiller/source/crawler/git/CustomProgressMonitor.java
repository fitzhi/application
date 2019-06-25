package fr.skiller.source.crawler.git;

import org.eclipse.jgit.lib.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomProgressMonitor implements ProgressMonitor {

	/**
 	 * The logger for the GitScanner.
 	 */
	final Logger logger = LoggerFactory.getLogger(CustomProgressMonitor.class.getCanonicalName());
	
	@Override
	public void start(int totalTasks) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("start(%d)", totalTasks));
		}
	}

	@Override
	public void beginTask(String title, int totalWork) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("beginTask(%s, %d)", title, totalWork));
		}
	}

	@Override
	public void update(int completed) {
	}

	@Override
	public void endTask() {
		if (logger.isDebugEnabled()) {
			logger.debug("endTask()");
		}
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

}
