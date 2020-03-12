/**
 * 
 */
package com.fitzhi.data.source;

import lombok.Data;

/**
 * <p>
 * Connection settings to the source repository whatever it is.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ConnectionSettings {
	/**
	 * Empty Constructor.
	 */
	public ConnectionSettings() {
		super();
	}
	
	/**
	 * The repository is public.
	 */
	private boolean publicRepository = false;
	
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
		return "ConnectionSettings [publicRepository=" + publicRepository + ", url=" + url + ", login=" + login
				+ ", password=" + password + "]";
	}

	
}
