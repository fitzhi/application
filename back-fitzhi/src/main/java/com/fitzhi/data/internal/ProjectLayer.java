package com.fitzhi.data.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * This class represents a layer of project.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
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
     * Array of staff identifiers.
     */
    private List<Integer> idStaffs = new ArrayList<>();

    /**
     * Complete constructor.
     * 
     * @param idProject the project identifier
     * @param year      the year of the layer
     * @param week      the week in the year of the layer.
     * @param lines     the number of lines
     * @param idStaff   uniquer staff identifier
     */
    public ProjectLayer(int idProject, int year, int week, int lines, int idStaff) {
        this(idProject, year, week, lines, new Integer[] { idStaff });
    }

    /**
     * Complete constructor.
     * 
     * @param idProject the project identifier
     * @param year      the year of the layer
     * @param week      the week in the year of the layer.
     * @param lines     the number of lines
     * @param idStaffs  array of staff identifiers.
     */
    public ProjectLayer(int idProject, int year, int week, int lines, Integer[] idStaffs) {
        this.idProject = idProject;
        this.year = year;
        this.week = week;
        this.lines = lines;
        this.idStaffs = new ArrayList<Integer>(Arrays.asList(idStaffs));
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
        // The comparison is limited to the first element in the staff list.
        diff = getIdStaffs().get(0) - pl.getIdStaffs().get(0);
        return diff; 
    }

}
