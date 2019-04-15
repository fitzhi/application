package fr.skiller.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public enum FileType {
	FILE_TYPE_PDF (0),
	FILE_TYPE_DOCX (1),
	FILE_TYPE_DOC (2),
	FILE_TYPE_TXT (3);

	
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
	 * @return the value of the <code>enum</code>.
	 */
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
