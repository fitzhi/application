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
	public String url;

	/**
	 * Connection login.
	 */
	public String login;

	/**
	 * Connection password.
	 */
	public String password;
	
	/**
	 * Local repository where the remote one is cloned.
	 */
	public String localRepository;

}
