/**
 * 
 */
package com.fitzhi.security;

import java.io.Serializable;

import javax.annotation.Generated;

import org.springframework.security.core.GrantedAuthority;

/**
 * <p>
 * Tixhì custom granted authority.<br/>
 * The only difference with {@link org.springframework.security.core.authority.SimpleGrantedAuthority SimpleGrantedAuthority} consists of an empty constructor.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class CustomGrantedAuthority implements GrantedAuthority {

	/**
	 * For serialization purpose.
	 */
	private static final long serialVersionUID = -6159897754284501386L;

	/**
	 * Empty constructor for serialization purpose.
	 */
	public CustomGrantedAuthority() {
	}
	
	/**
	 * Authority.
	 */
	String authority;
	
	/**
	 * Constructor.
	 * @param authority simple string containing the authority;
	 */
	public CustomGrantedAuthority(String authority) {
		super();
		this.authority = authority;
	}


	@Override
	public String getAuthority() {
		return authority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authority == null) ? 0 : authority.hashCode());
		return result;
	}


	@Override
	@Generated ("eclipse")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomGrantedAuthority other = (CustomGrantedAuthority) obj;
		if (authority == null) {
			if (other.authority != null)
				return false;
		} else if (!authority.equals(other.authority))
			return false;
		return true;
	}

}
