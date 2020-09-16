package com.fitzhi.data.internal;

import lombok.Data;

/**
 * <p>
 * This class represents a layer of project.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ProjectLayer {
    
    /**
     * Project identifier.
     */
    private int idProject;

    /**
     * Year of the layer.
     */
    private int year;

    /**
     * Week of the layer in the year
     */
    private int week;

    /**
     * Number of lines
     */
    private int lines;

    /**
     * Array of staff identifiers.
     */
    private int[] idStaffs;

    /**
     * Complete constructor.
     * @param idProject the project identifier
     * @param year the year of the layer
     * @param week the week in the year of the layer.
     * @param lines the number of lines
     * @param idStaff uniquer staff identifier
     */
    public ProjectLayer(int idProject, int year, int week, int lines, int idStaff) {
        this(idProject, year, week, lines, new int[]{idStaff});
    }
    
    /**
     * Complete constructor.
     * @param idProject the project identifier
     * @param year the year of the layer
     * @param week the week in the year of the layer.
     * @param lines the number of lines
     * @param idStaffs array of staff identifiers.
     */
    public ProjectLayer(int idProject, int year, int week, int lines, int[] idStaffs) {
        this.idProject = idProject;
        this.year = year;
        this.week = week;
        this.lines = lines;
        this.idStaffs = idStaffs;
    }

}
