package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * This class represents the classic credential info to access this application (user/password).
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public @Data class ClassicCredentials implements Serializable {

	/**
	 * For serialization purpose.
	 */
	public static final long serialVersionUID = 2914343440765766340L;

	private String login;
	private String password;

}
