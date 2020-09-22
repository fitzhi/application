package com.fitzhi.data.internal;

import lombok.Data;

/**
 * <p>
 * This class represents a project floor ready to be integrated in the building.
 * Each building is made of floors which is a succession of layers.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ProjectFloor {
    
    private int idProject;

    private int year;

    private int week;
    
    /**
     * Number of lines developed by ACTIVE developers.
     */
    private int linesActiveDevelopers;

    /**
     * Number of lines developed by INACTIVE developers.
     */
    private int linesInactiveDevelopers;

    /**
     * Public construction of a floor.
     * @param idProject the project identifier
     * @param year the year of the floor
     * @param week the week of the floor
     * @param linesActiveDevelopers the number of lines developed by active developers during that period
     * @param linesInactiveDevelopers the number of lines developed by inactive developers during that period
     */
    public ProjectFloor(int idProject, int year, int week, int linesActiveDevelopers,
            int linesInactiveDevelopers) {
        this.idProject = idProject;
        this.year = year;
        this.week = week;
        this.linesActiveDevelopers = linesActiveDevelopers;
        this.linesInactiveDevelopers = linesInactiveDevelopers;
    }

    /**
     * Add a number of lines developed by active developers
     * @param linesActiveDevelopers the number of lines
     */
    public void addLinesActiveDevelopers(int linesActiveDevelopers) {
        this.linesActiveDevelopers += linesActiveDevelopers;
    }

    /**
     * Add a number of lines developed by INactive developers
     * @param linesInactiveDevelopers the number of lines
     */
    public void addLinesInactiveDevelopers(int linesInactiveDevelopers) {
        this.linesInactiveDevelopers += linesInactiveDevelopers;
    }
}
