package com.fitzhi.bean.impl;

import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectFloor;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.git.SourceChange;
import com.fitzhi.source.crawler.git.SourceFileHistory;
import com.fitzhi.util.ProjectBuildingFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Main (an currently single) implementation in charge of the skyline
 * processing.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
@Slf4j
public class SkylineProcessorImpl implements SkylineProcessor {

    @Autowired
    private StaffHandler staffHandler;

    @Autowired
    private DataHandler dataHandler;
   
    class Layer {

        /**
         * The year
         */
        public int year;
        /**
         * The week
         */
        public int week;
        /**
         * The idStaff
         */
        public int idStaff;

        public Layer(int year, int week, int idStaff) {
            this.year = year;
            this.week = week;
            this.idStaff = idStaff;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + idStaff;
            result = prime * result + week;
            result = prime * result + year;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Layer other = (Layer) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
                return false;
            if (idStaff != other.idStaff)
                return false;
            if (week != other.week)
                return false;
            if (year != other.year)
                return false;
            return true;
        }

        private SkylineProcessorImpl getEnclosingInstance() {
            return SkylineProcessorImpl.this;
        }

    }

    interface LayerIdentifier {
        Layer processLayer(SourceChange sourceChange);
    }

    @Override
    public ProjectLayers generateProjectLayers(Project project, SourceControlChanges changes) {

        // This temporalField is used to retrieve the week number of the date into the year
        final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

        final Function<SourceChange, Layer> layerIdentifier = (SourceChange sourceChange) -> {
            return new Layer(sourceChange.getDateCommit().getYear(), sourceChange.getDateCommit().get(woy),
                    sourceChange.getIdStaff());
        };

        final List<ProjectLayer> layers = new ArrayList<>();
        changes.getChanges().values().stream().flatMap(hist -> hist.getChanges().stream()).collect(Collectors.toList())
                .stream().collect(Collectors.groupingBy(layerIdentifier, Collectors.summingInt(SourceChange::lines)))
                .forEach((layer, lines) -> {
                    ProjectLayer projectLayer = new ProjectLayer(project.getId(), layer.year, layer.week, lines,
                            layer.idStaff);
                    layers.add(projectLayer);
                });

        Collections.sort(layers);
        return new ProjectLayers(layers);
    }

    @Override
    public void actualizeStaff(Project project, SourceControlChanges changes) {
        final Map<String, Integer> cache = new HashMap<>();
        changes.getChanges().values().stream().flatMap((SourceFileHistory sfh) -> sfh.getChanges().stream())
                .forEach((SourceChange sc) -> {
                    final Staff staff = staffHandler.lookup(sc.getAuthorName());
                    if (cache.containsKey(sc.getAuthorName())) {
                        sc.setIdStaff(cache.get(sc.getAuthorName()).intValue());
                    } else {
                        sc.setIdStaff((staff != null) ? staff.getIdStaff() : -1);
                        cache.put(sc.getAuthorName(), sc.getIdStaff());
                    }
                });
    }

    @Override
    public ProjectBuilding generateProjectBuilding(Project project) throws SkillerException {

        ProjectLayers layers = dataHandler.loadSkylineLayers(project);
        if (log.isDebugEnabled()) {
            log.debug (String.format("Loading %d layers for the project %s", layers.getLayers().size(), project.getName()));
        }

        return generateProjectBuilding(project, layers);
    }

    @Override
    public ProjectBuilding generateProjectBuilding(Project project, ProjectLayers layers) {

        ProjectBuilding building = ProjectBuildingFactory.getInstance(project, layers);

        if (log.isDebugEnabled()) {
            log.debug(String.format("the building has %d floors", building.getBuilding().size()));
        }

        // This temporalField is used to retrieve the week number of the date into the year
        final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

        layers.getLayers().stream().forEach(layer -> {
            if (layer.getIdStaff() == -1) {
                building.addInactiveLines(layer.getLines(), layer.getYear(), layer.getWeek());
            } else {
                ProjectFloor floor = building.getProjectFloor(layer.getYear(), layer.getWeek());
                Staff staff = staffHandler.getStaff(layer.getIdStaff());
                if (staff == null) {
                    throw new RuntimeException(String.format("Identifier %d is not found in the staff members", layer.getIdStaff()));
                }
                if (staff.isActive()) {
                    building.addActiveOrInactiveLines(
                        layer.getLines(), 
                        layer.getYear(), layer.getWeek(), 
                        Integer.MAX_VALUE, Integer.MAX_VALUE);
                } else {
                    building.addActiveOrInactiveLines(
                        layer.getLines(), 
                        layer.getYear(), layer.getWeek(), 
                        staff.getDateInactive().getYear(), staff.getDateInactive().get(woy));
                }
            }
        });
        return building;
    }
  
}
