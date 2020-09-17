package com.fitzhi.bean.impl;

import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.source.crawler.git.SourceChange;

import org.springframework.stereotype.Service;

/**
 * <p>
 * Main (an currently single) implementation in charge of the skyline
 * processing.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
public class SkylineProcessorImpl implements SkylineProcessor {

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
    public List<ProjectLayer> generateProjectLayers(Project project, SourceControlChanges changes)  {

        // Tis temporalField is used to retrieve the week number of the date into the year
        // This object we be used in the object below
        final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(); 

        final Function<SourceChange, Layer> layerIdentifier =  (SourceChange sourceChange) -> {
            return new Layer(
                sourceChange.getDateCommit().getYear(), 
                sourceChange.getDateCommit().get(woy),
                sourceChange.getIdStaff()) ;
        };

        final List<ProjectLayer> working_layers = new ArrayList<>();
        changes.getChanges().values().stream()
            .flatMap(hist -> hist.getChanges().stream())
            .collect(Collectors.toList())
            .stream()
            .collect(Collectors.groupingBy(layerIdentifier, Collectors.summingInt(SourceChange::lines)))
            .forEach( (layer, lines) -> {
                ProjectLayer projectLayer = new ProjectLayer(project.getId(), layer.year, layer.week, lines, layer.idStaff);
                working_layers.add(projectLayer);
            });

        Collections.sort(working_layers);

        final List<ProjectLayer> layers = new ArrayList<>();
        ProjectLayer layer = null;
        for (ProjectLayer wlayer : working_layers) {
            if (layer == null) {
                layer = wlayer;
                continue;
            } 
            if (layer.isSameWeek(wlayer)) {
                layer.setLines(layer.getLines() + wlayer.getLines());
                layer.getIdStaffs().add(wlayer.getIdStaffs().get(0));
            } else {
                layers.add(layer);
                layer = wlayer;
           }
        }
        layers.add(layer);

        return layers;
    }
    
}
