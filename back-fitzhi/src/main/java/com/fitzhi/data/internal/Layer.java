package com.fitzhi.data.internal;

import com.fitzhi.bean.SkylineProcessor;

import lombok.Data;


/**
 * <p>
 * This class represents the contribution  of a developer for a week of activity.
 * Layer has been created to be used by the {@link SkylineProcessor skykine generation}.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Data
public class Layer {
    
    /**
     * The idStaff
     */
    private int idStaff;
    /**
     * The year
     */
    private int year;
    /**
     * The week
     */
    private int week;


    public Layer(int year, int week, int idStaff) {
        this.year = year;
        this.week = week;
        this.idStaff = idStaff;
    }

}
