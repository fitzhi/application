package com.fitzhi.data.internal;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * This class represents a layer of project.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@NoArgsConstructor
public @Data class ProjectLayer implements Comparable<ProjectLayer> {

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
     * Staff identifier.
     */
    private int idStaff;

    /**
     * Complete constructor.
     * 
     * @param idProject the project identifier
     * @param year      the year of the layer
     * @param week      the week in the year of the layer.
     * @param lines     the number of lines
     * @param idStaff   uniquer Staff identifier
     */
    public ProjectLayer(int idProject, int year, int week, int lines, int idStaff) {
        this.idProject = idProject;
        this.year = year;
        this.week = week;
        this.lines = lines;
        this.idStaff = idStaff;
    }

    /**
     * @return {@code true} if the given layer is located on the year/week than the current one
     */
    public boolean isSameWeek(ProjectLayer projectLayer) {
        return (projectLayer.year == year) && (projectLayer.week == week);
    }

    @Override
    public int compareTo(ProjectLayer pl) {
        int diff = getYear() - pl.getYear();
        if (diff != 0) {
            return diff;
        }
        diff = getWeek() - pl.getWeek();
        if (diff != 0) {
            return diff;
        }
        diff = idStaff - pl.getIdStaff();
        return diff; 
    }

}
