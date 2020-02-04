/**
 * 
 */
package com.fitzhi.data.source;

/**
 * <p>
 * Connection settings to the source repository whatever it is.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
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
	
	@Override
	public String toString() {
		return "ConnectionSettings [url=" + getUrl() + ", login=" + getLogin() + "]";
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

}
