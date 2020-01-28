package com.tixhi.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;

/**
 * File type saved
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public enum FileType {
	@SerializedName("0") FILE_TYPE_PDF (0), // SerializedName is used to save this enum as an number value
	@SerializedName("1") FILE_TYPE_DOCX (1),
	@SerializedName("2") FILE_TYPE_DOC (2),
	@SerializedName("3") FILE_TYPE_TXT (3);

	
	private int value;

	private static Map<Integer, FileType> map = new HashMap<>();
	

    static {
        for (FileType fileType : FileType.values()) {
            map.put(fileType.value, fileType);
        }
    }
    
	/**
	 * Constructor of the <code>enum</code> with one unique value parameter
	 * @param value value of the <code>enum</code>
	 */
	FileType(int value) {
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
	 * @return the found ApplicationFileType if any, or <code>null</code> if this map contains no mapping for the key
	 */
    public static FileType valueOf(int type) {
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
