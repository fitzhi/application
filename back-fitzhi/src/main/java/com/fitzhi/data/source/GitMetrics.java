package com.fitzhi.data.source;

import java.time.LocalDate;

/**
 * Metrics loaded from GIT for a submitter.
 */
public interface GitMetrics {

	/**
	 * @return the number of commits submited.
	 */
	int getNumberOfCommits();

	/**
	 * @return the number of files submited.
	 */
	int getNumberOfFiles();

	/**
	 * @return the date of the last commit.
	 */
	LocalDate getLastCommit(); 

	/**
	 * @return the date of the first commit.
	 */
	LocalDate getFirstCommit(); 

	/**
	 * @param numberOfCommits the number of metrics submited.
	 */
	void setNumberOfCommits(int numberOfCommits);

	/**
	 * @param numberOfFiles the number of files submited.
	 */
	void setNumberOfFiles(int numberOfFiles);

	/**
	 * @param lastCommit the date of the last commit.
	 */
	void setLastCommit(LocalDate lastCommit); 

	/**
	 * @param firstCommit the date of the first commit.
	 */
	void setFirstCommit(LocalDate firstCommit); 
}
