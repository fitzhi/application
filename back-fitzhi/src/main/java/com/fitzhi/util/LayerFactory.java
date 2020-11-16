package com.fitzhi.util;

import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import com.fitzhi.data.internal.Layer;
import com.fitzhi.source.crawler.git.SourceChange;

/**
 * <p>
 * This is the factory of {@link Layer layers}. 
 * The motivation and goal for this specific factory is to handle correctly 
 * the processing of the couple (year; week) given a source date. 
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class LayerFactory {
    
    // This temporalField is used to retrieve the week number of the date into the year
    private final static TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
    
    // This one returns the associated year.
    private final static TemporalField yowoy = WeekFields.of(Locale.getDefault()).weekBasedYear();

    public static Layer getInstance(SourceChange sourceChange) {
        return new Layer(
            sourceChange.getDateCommit().get(yowoy), 
            sourceChange.getDateCommit().get(woy),
            sourceChange.getIdStaff());

    }
}
