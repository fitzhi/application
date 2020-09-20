package com.fitzhi;

import java.util.concurrent.atomic.AtomicInteger;

import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;

public class ProjectBuildingFactory {
    
    public static ProjectBuilding getInstance(ProjectLayers layers) {

        // The year when the project has started.
        AtomicInteger startingYear = new AtomicInteger();

        // The week in first year, when the project has started.
        AtomicInteger startingWeekInStartingYear = new AtomicInteger();

        startingYear.set(Integer.MAX_VALUE);
        startingWeekInStartingYear.set(Integer.MAX_VALUE);

        layers.getLayers().stream().forEach((ProjectLayer projectLayer) -> {
            if (projectLayer.getYear() < startingYear.get()) {
                startingYear.set(projectLayer.getYear());
            }
            if (projectLayer.getYear() == startingYear.get()) {
                if (projectLayer.getWeek() < startingWeekInStartingYear.get()) {
                    startingWeekInStartingYear.set(projectLayer.getWeek());
                }              
            }
        });

        return new ProjectBuilding();
    }
}
