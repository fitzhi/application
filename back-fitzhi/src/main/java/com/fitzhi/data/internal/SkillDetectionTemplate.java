/**
 * 
 */
package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * Detection pattern for skills.<br/>
 * The data hosted by this object are provided to the parser in charge of skill detection within the repository.<br/>
 * For example, the skill <code>java</code> will be detected with
 * <ul>
 * <li> the type <b>source filename</b></li>
 * <li>the pattern <b><code>.java$</code></b></li>
 * </ul>
 * Some other detection patterns might need to dig inside the code.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class SkillDetectionTemplate implements Serializable {
	
	/**
	 * serialVersionUID as usual.
	 */
	private static final long serialVersionUID = -5754125817307425316L;

	private SkillDetectorType detectionType;
	
	private String pattern;

	/**
	 * Public EMPTY construction for serialization purpose
	 */
	public SkillDetectionTemplate() {
	}
	
	/**
	 * Public construction
	 * @param detectionType the type of detection
	 * @param pattern the pattern provided for this type of detection
	 */
	public SkillDetectionTemplate(SkillDetectorType detectionType, String pattern) {
		super();
		this.detectionType = detectionType;
		this.pattern = pattern;
	}

}
