package com.fitzhi.data.internal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * This class is a container of {@link ProjectLayer project layers}.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ProjectLayers {
    
    private List<ProjectLayer> layers = new ArrayList<>();

    /**
     * Public construction of the container
     */
    public ProjectLayers() {
    }

    /**
     * Construction of this layers container
     * @param layers the list of Project layers
     */
    public ProjectLayers(List<ProjectLayer> layers) {
        this.layers = layers;
    }

	/**
	 * Initialize the project Building of the building. 
	 */
	public ProjectBuilding ProjectBuilding() {




        LocalDate localDate = LocalDate.of(year, 2, 1)
        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
        .with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue());
        return LocalDate.now();
    }

}