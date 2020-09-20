package com.fitzhi.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectBuildingFactory {
    
    /**
     * <p>
     * Create a new prefilled {@link ProjectBuilding}.
     * </p>
     * @param project the current project
     * @param layers the {@link ProjectLayer project layers container}
     * @return an instance of {@link ProjectBuilding}
     */
    public static ProjectBuilding getInstance(Project project, ProjectLayers layers) {

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

        if (log.isDebugEnabled()) {
            log.debug(String.format("The project has started week %d in year %d", startingWeekInStartingYear.get(), startingYear.get()));
        }
        LocalDate starting = LocalDate.of(startingYear.get(), 2, 1)
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, startingWeekInStartingYear.get())
            .with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());
        if (log.isDebugEnabled()) {
            log.debug(String.format("Approximative start of date for the project %s", starting));
        }

        // Tis temporalField is used to retrieve the week number of the date into the year
        final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

        ProjectBuilding building = new ProjectBuilding();
        for (LocalDate date = starting; date.isBefore(LocalDate.now()); date = date.plusWeeks(1)) {
            building.initWeek(project.getId(), date.getYear(), date.get(woy));
        }

        return building;
    }
}
