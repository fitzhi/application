package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * This object is called &laquo; <strong>Constellation</strong> &raquo; 
 * because its representation on the Angular front-end is a serie of &#x2605; stars.
 * </p>
 * <p>A constellation in fact, represents the number of units of skills registered for axx skill.
 * Each &#x2605; represents a &laquo; unit of skill &raquo;. 
 * E.g., a Java expert (5 &#x2605;) brings 5 ​​units to the count, when  one novice developer (1 &#x2605;) brings one.
 * </p>
 * <p>
 * Every month, a constellation will be saved in Fitzhì.
 * </p>
 */
public @Data class Constellation implements Serializable {
    
}
