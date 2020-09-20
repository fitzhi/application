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
public @Data class ProjectBuilding {

    /**
     * This class contains a key for the building.
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
            this.idProject = idProject;
            this.year = year;
            this.week = week;
        }
    
    }
    /**
     * The building which is a timestamped layering of development activities.
     */
    private Map<YearWeek, ProjectFloor> building = new HashMap<>();

    /**
     * Current project identifier.
     */
    private int idProject;

    /**
     * Initialize a week in the year.
     * @param idProject the project identifier
     * @param year the year
     * @param week the week
     */
    public void initWeek (int idProject, int year, int week) {
        this.idProject = idProject;
        building.put(new YearWeek(idProject, year, week), new ProjectFloor(idProject, year, week, 0, 0));
    }

    /**
     * <p>
     * Instanciate a new {@link YearWeek}
     * </p>
     * @param year the given year
     * @param week the given week
     * @return a new instance of {@link YearWeek}
     */
    public YearWeek yearWeek(int year, int week) {
        return new YearWeek(idProject, year, week);
    }
}
