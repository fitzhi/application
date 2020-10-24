package com.fitzhi.data.internal;

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

}