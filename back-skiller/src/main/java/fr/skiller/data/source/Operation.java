package fr.skiller.data.source;

import java.util.Date;

public class Operation {

	/**
	 *identifier of the committer
	 */
	final String login;
	
	/**
	 * email of the committer
	 */
	final String email;
	
	/**
	 * last of date for this commit.
	 */
	Date dateCommit;

	/**
	 * @param login author's login
	 * @param email author's email
	 * @param dateCommit date of commit
	 */
	public Operation(String login, String email, Date dateCommit) {
		super();
		this.login = login;
		this.email = email;
		this.dateCommit = dateCommit;
	}

	
}
