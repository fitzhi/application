package com.fitzhi.data.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing the method {@link ProjectLayers#LatestWeek()}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ProjectLayersLatestWeekTest {

    /**
     * Simple test of {@code latestWeek()}
     */
    @Test
    public void testLatestWeek() {
        ProjectLayers pl = new ProjectLayers(new Project(1, "test"));
        pl.getLayers().add(new ProjectLayer(1, 2020, 3, 10, 1));
        pl.getLayers().add(new ProjectLayer(1, 2020, 3, 20, 2));
        pl.getLayers().add(new ProjectLayer(1, 2019, 23, 45, 1));
        assertEquals(pl.LatestWeek().getYear(), 2020);
        assertEquals(pl.LatestWeek().getWeek(), 3);
        assertEquals(pl.LatestWeek().getIdProject(), 1);
    }
}