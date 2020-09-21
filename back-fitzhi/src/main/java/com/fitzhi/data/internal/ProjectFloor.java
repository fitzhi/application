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
    
    private int numberOfLinesByActiveDevelopers;

    private int numberOfLinesByInactiveDevelopers;

    /**
     * Public construction of a floor.
     * @param idProject the project identifier
     * @param year the year of the floor
     * @param week the week of the floor
     * @param numberOfLinesByActiveDevelopers the number of lines developed by active developers during that period
     * @param numberOfLinesByInactiveDevelopers the number of lines developed by inactive developers during that period
     */
    public ProjectFloor(int idProject, int year, int week, int numberOfLinesByActiveDevelopers,
            int numberOfLinesByInactiveDevelopers) {
        this.idProject = idProject;
        this.year = year;
        this.week = week;
        this.numberOfLinesByActiveDevelopers = numberOfLinesByActiveDevelopers;
        this.numberOfLinesByInactiveDevelopers = numberOfLinesByInactiveDevelopers;
    }

    /**
     * Add a number of lines developed by active developers
     * @param numberOfLinesByActiveDevelopers the number of lines
     */
    public void addNumberOfLinesByActiveDevelopers(int numberOfLinesByActiveDevelopers) {
        this.numberOfLinesByActiveDevelopers += numberOfLinesByActiveDevelopers;
    }

    /**
     * Add a number of lines developed by INactive developers
     * @param numberOfLinesByInactiveDevelopers the number of lines
     */
    public void addNumberOfLinesByInactiveDevelopers(int numberOfLinesByInactiveDevelopers) {
        this.numberOfLinesByInactiveDevelopers += numberOfLinesByInactiveDevelopers;
    }
}
