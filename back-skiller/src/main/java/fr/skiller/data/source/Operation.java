package fr.skiller.data.source;

import java.util.Date;

public class Operation {

	/**
	 *identifier of the commiter
	 */
	final String login;
	
	/**
	 * last of date for this commit.
	 */
	Date dateCommit;

	/**
	 * @param login
	 * @param dateCommit
	 */
	public Operation(String login, Date dateCommit) {
		super();
		this.login = login;
		this.dateCommit = dateCommit;
	}

	
}
