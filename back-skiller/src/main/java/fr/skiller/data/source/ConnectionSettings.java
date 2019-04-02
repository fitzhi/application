/**
 * 
 */
package fr.skiller.data.source;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Connection settings to the source repository whatever it is.
 */
public class ConnectionSettings {
	/**
	 * Empty Constructor.
	 */
	public ConnectionSettings() {
		super();
	}
	/**
	 * Connection URL.
	 */
	private String url;

	/**
	 * Connection login.
	 */
	private String login;

	/**
	 * Connection password.
	 */
	private String password;
	
	/**
	 * Local repository where the remote one is cloned.
	 */
	private String localRepository;

	@Override
	public String toString() {
		return "ConnectionSettings [url=" + getUrl() + ", login=" + getLogin() + ", password=" + getPassword() + ", localRepository="
				+ getLocalRepository() + "]";
	}

	/**
	 * @return the Connection URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the connection URL to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the localRepository
	 */
	public String getLocalRepository() {
		return localRepository;
	}

	/**
	 * @param localRepository the localRepository to set
	 */
	public void setLocalRepository(String localRepository) {
		this.localRepository = localRepository;
	}

}
