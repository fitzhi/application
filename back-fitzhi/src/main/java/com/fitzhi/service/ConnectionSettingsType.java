package com.fitzhi.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;

/**
 * Connection settings type possible to access the GIT repository.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public enum ConnectionSettingsType {
	/**
	* No GIT login settings available
	*/
	@SerializedName("0") NO_LOGIN (0), // SerializedName is used to save this enum as an number value
	/**
	* Constant representing one the 3 models of connection to the GIT repository : <br/>
	* This one if for the direct access : url repository / user / password ou token
	*/
	@SerializedName("1") DIRECT_LOGIN (1), 
	/**
	 * Constant representing one the 3 models of connection to the GIT repository : <br/>
	 * This one if for the indirect access : url repository / remote filename which contains the connection parameters.
	 */
	@SerializedName("2") REMOTE_FILE_LOGIN (2),
	/**
	 * Constant representing one the 3 models of connection settings : <br/>
	 * This one if for the PUBLIC access
	 */
	@SerializedName("3") PUBLIC_LOGIN (3);

	
	private int value;

	private static Map<Integer, ConnectionSettingsType> map = new HashMap<>();
	
    static {
        for (ConnectionSettingsType fileType : ConnectionSettingsType.values()) {
            map.put(fileType.value, fileType);
        }
    }
    
	/**
	 * Constructor of the <code>enum</code> with one unique value parameter
	 * @param value value of the <code>enum</code>
	 */
	ConnectionSettingsType(int value) {
		this.value = value;
	}
	
	/**
	 * @return the value of this <code>enum</code>.<br/>
	 * The annotation <b>{@code JsonValue}</b> is there to inform {@code Jackson 2.1.2} used by spring 
	 * that this value represents the {@code enum} during serialization.
	 */
	@JsonValue
	public int getValue() {
		return value;
	}

	/**
	 * Retrieved the type of application from its key
	 * @param type the key type of application
	 * @return the found type of connectionSettings if any, or <code>null</code> if this map contains no mapping for the key
	 */
    public static ConnectionSettingsType valueOf(int type) {
        return map.get(type);

    }	

    /**
     * String representation of this enum.
     */
    @Override
    public String toString() {
    	return String.valueOf(value);
    }
}
