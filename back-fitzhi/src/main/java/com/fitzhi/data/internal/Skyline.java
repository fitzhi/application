package com.fitzhi.data.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * The Rising skyline container
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
public @Data class Skyline {

    private List<ProjectFloor> floors = new ArrayList<>();

    /**
     * Adding the history of a skyline in the rising skyline.
     * @param projectBuilding the building history
     */
    public void addBuilding(ProjectBuilding projectBuilding) {
        Collection<ProjectFloor> building = projectBuilding.getBuilding().values();
        if (!building.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug (String.format(
                    "Adding %d layers corresponding to the project %d in the skyline", 
                    building.size(), 
                    projectBuilding.getIdProject()));
            }
            floors.addAll(building);
        } 
    }

    /**
     * @return {@code true} id the skyline is empty, {@code false} otherwise. 
     */
    public boolean isEmpty() {
        return floors.isEmpty();
    }
}
