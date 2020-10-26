package com.fitzhi.bean.impl;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.exception.SkillerException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Testing the method {@link SkylineProcessor#completeProjectLayers(com.fitzhi.data.internal.ProjectLayers)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkylineProcessorCompleteProjectLayersTest {

    static{
        Locale.setDefault(Locale.UK);
    }

    @Autowired
    SkylineProcessor skylineProcessor;

    // This temporalField is used to retrieve the week number of the date into the year.
    private final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
    
    @Test
    public void testTheNominalCompletion() throws SkillerException {
        Project project = new Project(1796, "Castiglione !");
        final ProjectLayers projectLayers =new ProjectLayers(project);
        projectLayers.getLayers().add(new ProjectLayer(1796, 2020, 1, 10, 1));        
        projectLayers.getLayers().add(new ProjectLayer(1796, 2020, 1, 20, 2));        
    
        skylineProcessor.completeProjectLayers(projectLayers);
        final int year = LocalDate.now().getYear();
        final int week = LocalDate.now().get(woy);
        ProjectBuilding pb = new ProjectBuilding();
        pb.setIdProject(1796);
        List<ProjectLayer> layers = projectLayers.filterOnWeek(pb.yearWeek(year, week));
        if (layers.size() == 0) {
            layers = projectLayers.filterOnWeek(pb.yearWeek(year, week-1));
        } 
        Assert.assertEquals(2, layers.size());
        Assert.assertEquals(layers.get(0).getIdStaff(), 1);
        Assert.assertEquals(layers.get(0).getLines(), 0);
        Assert.assertEquals(layers.get(1).getIdStaff(), 2);
        Assert.assertEquals(layers.get(1).getLines(), 0);
    }

  
}