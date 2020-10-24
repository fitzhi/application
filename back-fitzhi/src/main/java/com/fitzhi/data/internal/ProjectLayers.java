package com.fitzhi.data.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fitzhi.data.internal.ProjectBuilding.YearWeek;

import lombok.Data;

/**
 * <p>
 * This class is a container of {@link ProjectLayer project layers}.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ProjectLayers {
    
    private List<ProjectLayer> layers;

    /**
     * The project.
     */
    private Project project;

    /**
     * Public construction of the container.
     * @param project the project of the layers
     */
    public ProjectLayers(Project project) {
        this (project, new ArrayList<>());
    }

    /**
     * Construction of this layers container
     * @param project the project of the layers
     * @param layers the list of Project layers
     */
    public ProjectLayers(Project project, List<ProjectLayer> layers) {
        this.project = project;
        this.layers = layers;
    }

    /**
     * Return the latest week recorded in the layers collection. 
     */
    public YearWeek LatestWeek() {
        int max = this.layers.stream()
            .mapToInt(layer -> layer.getYear()*100 + layer.getWeek())
            .reduce(0, Integer::max);

        ProjectBuilding pb = new ProjectBuilding();
        pb.setIdProject(project.getId());
        return pb.yearWeek(Math.floorDiv(max, 100), max - Math.floorDiv(max, 100)*100);
    }

    /**
     * Filter the layers on the given {@link YearWeek week}  
     * @param yearWeek the given filter
     * @return a filtered subset from the whole layers
     */
    public List<ProjectLayer> filterOnWeek(YearWeek yearWeek) {
        return this.layers
            .stream()
            .filter(layer -> ( 
                (layer.getIdProject() == yearWeek.getIdProject()) && 
                (layer.getYear() == yearWeek.getYear()) && 
                (layer.getWeek() == yearWeek.getWeek())))
            .collect(Collectors.toList());
    }

}