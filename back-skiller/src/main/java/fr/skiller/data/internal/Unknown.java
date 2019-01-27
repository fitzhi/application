package fr.skiller.data.internal;

public class Unknown {

	public final String login;

	/**
	 * @param login of an undefined contributor.
	 */
	public Unknown(final String login) {
		this.login = login;
	}

	@Override
	public String toString() {
		return "Unknown [login=" + login + "]";
	}
	
}
