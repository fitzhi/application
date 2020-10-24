package com.fitzhi.data.internal;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing the method {@link ProjectLayers#filterOnWeek(com.fitzhi.data.internal.ProjectBuilding.YearWeek)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ProjectLayersFilterOnWeekTest {
   
    private ProjectLayers pl;

    private ProjectBuilding pb;

    @Before
    public void before() {
        pl = new ProjectLayers(new Project(1, "test"));
        pb = new ProjectBuilding();
        pb.setIdProject(1);
    }

    /**
     * Simple test of {@code latestWeek()}
     */
    @Test
    public void testNominalLatestWeek() {
        pl.getLayers().add(new ProjectLayer(1, 2020, 3, 10, 1));
        pl.getLayers().add(new ProjectLayer(1, 2020, 3, 20, 2));
        pl.getLayers().add(new ProjectLayer(1, 2019, 23, 45, 1));
        pl.getLayers().add(new ProjectLayer(1, 2020, 2, 10, 1));
        pl.getLayers().add(new ProjectLayer(1, 2020, 2, 20, 2));
        pl.getLayers().add(new ProjectLayer(1, 2020, 4, 10, 1));
        pl.getLayers().add(new ProjectLayer(1, 2020, 4, 20, 2));
 
        List<ProjectLayer> layers = pl.filterOnWeek(pb.yearWeek(2020, 2));
        Assert.assertEquals(2, layers.size());
        Assert.assertEquals(2020, layers.get(0).getYear());
        Assert.assertEquals(2, layers.get(0).getWeek());
        Assert.assertEquals(10, layers.get(0).getLines());
    }    

    /**
     * Simple test of {@code latestWeek()}
     */
    @Test
    public void testUnmatchLatestWeek() {
        pl.getLayers().add(new ProjectLayer(2, 2020, 3, 10, 1));
        pl.getLayers().add(new ProjectLayer(2, 2020, 3, 20, 2));
        pl.getLayers().add(new ProjectLayer(2, 2019, 23, 45, 1));
        pl.getLayers().add(new ProjectLayer(2, 2020, 2, 10, 1));
        pl.getLayers().add(new ProjectLayer(2, 2020, 2, 20, 2));
        pl.getLayers().add(new ProjectLayer(2, 2020, 4, 10, 1));
        pl.getLayers().add(new ProjectLayer(2, 2020, 4, 20, 2));
 
        List<ProjectLayer> layers = pl.filterOnWeek(pb.yearWeek(2020, 2));
        Assert.assertTrue(layers.isEmpty());
    }    
}
