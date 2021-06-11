package com.fitzhi.data.internal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 * This object contains the setting which permits to isolate some special code patterns,
 * which characterize the level of a developer in a skill.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@AllArgsConstructor
public @Data class SkillLevelCodePattern {

    /**
     * This object identifier.
     */
    private int idSkillLevelCodePattern;

    /**
     * The Skill identifier.
     */
    private int idSkill;

    /**
     * The level in the skill corresponding to this codePattern.
     */
    private int level;

    /**
     * Patten to be used to detect a particular level in a skill.
     */
    private String codePattern;

    /**
     * Type of code pattern.
     */
    private TypeCodePattern typeCodePattern;
    
}
