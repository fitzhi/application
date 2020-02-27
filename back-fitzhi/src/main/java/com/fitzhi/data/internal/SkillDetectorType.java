package com.fitzhi.data.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;


/**
 * <p>
 * Type of skill detector.<br/>
 * This <code>enum</code> is used by {@link SkillDetectionPattern}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public enum SkillDetectorType {
	//
	// SerializedName is used to save this enum as an number value
	//
	@SerializedName("0") FILENAME_DETECTOR_TYPE (0, "Filename filter pattern such as .java$"), 
	@SerializedName("1") PACKAGE_JSON_DETECTOR_TYPE (1, "Dependency detection in the package.json file"); 
	
	private int value;
    
	private String title;
	
	private static Map<Integer, SkillDetectorType> map = new HashMap<>();
	
    static {
        for (SkillDetectorType detectorType : SkillDetectorType.values()) {
            map.put(detectorType.value, detectorType);
        }
    }

    /**
	 * The annotation <b>{@code JsonValue}</b> is there to inform {@code Jackson 2.1.2} used by spring 
	 * that this value represents the {@code enum} during serialization.
	 */
	@JsonValue
	public int getValue() {
		return value;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Constructor of the <code>enum</code> with one unique value parameter
	 * @param value value of the <code>enum</code>
	 * @param title title of this <code>enum</code>
	 */
	SkillDetectorType(int value, String title) {
		this.value = value;
		this.title = title;
	}
	
    /**
     * @return the <code>String</code> representation of this <code>enum</code>.
     */
    @Override
    public String toString() {
    	return String.valueOf(this.value) + " " + this.title;
    }
    
    /**
     * @return the map containing all detector type.
     */
    public static Map<Integer, String> getDetectorTypes() {
    	return map
    			.entrySet()
    			.stream()
    			.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue().getTitle()));
    }
}
