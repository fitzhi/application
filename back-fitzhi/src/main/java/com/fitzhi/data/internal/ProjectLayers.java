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
    
    private List<ProjectLayer> layers = new ArrayList<>();

    /**
     * Public construction of the container.
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

}