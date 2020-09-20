package com.fitzhi.data.internal;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * <p>
 * This class represents a project building ready to be integrated in the skyline.
 * </p> 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ProjectBuilding {

    /**
     * This class
     */
    public @Data class YearWeek {

        private int idProject;
        
        private int year;
        
        private int week;

        /**
         * Public construction
         * @param idProject the project identifier
         * @param year the year
         * @param week the week
         */
        public YearWeek(int idProject, int year, int week) {
            this.year = year;
            this.week = week;
        }
    
    }
    /**
     * The building which is a timestamped layering of development activities.
     */
    public Map<YearWeek, ProjectFloor> building = new HashMap<>();

    /**
     * Initialize a week in the year.
     * @param idProject the project identifier
     * @param year the year
     * @param week the week
     */
    public void initWeek (int idProject, int year, int week) {
        building.put(new YearWeek(idProject, year, week), new ProjectFloor(idProject, year, week, 0, 0));
    }
}
