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
    
    private int idNumberOfLinesByActiveDevelopers;

    private int idNumberOfLinesByInactiveDevelopers;

    /**
     * Public construction of a floor.
     * @param idProject the project identifier
     * @param year the year of the floor
     * @param week the week of the floor
     * @param idNumberOfLinesByActiveDevelopers the number of lines developed by active developers during that period
     * @param idNumberOfLinesByInactiveDevelopers the number of lines developed by inactive developers during that period
     */
    public ProjectFloor(int idProject, int year, int week, int idNumberOfLinesByActiveDevelopers,
            int idNumberOfLinesByInactiveDevelopers) {
        this.idProject = idProject;
        this.year = year;
        this.week = week;
        this.idNumberOfLinesByActiveDevelopers = idNumberOfLinesByActiveDevelopers;
        this.idNumberOfLinesByInactiveDevelopers = idNumberOfLinesByInactiveDevelopers;
    }

}
