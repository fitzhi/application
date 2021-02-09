package com.fitzhi.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Map;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectFloor;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing the method {@link ProjectBuildingFactory#getInstance(Project, ProjectLayers)}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectBuildingFactoryTest {

    @Autowired
    DataHandler dataHandler;

    /**
     * Testing the getInstance method with a very simple set of data.
     */
    @Test
    public void testGetInstanceSimple() {
        Project project = new Project(1789, "Revolution");
        ProjectLayers pl = new ProjectLayers(project);
        pl.getLayers().add(new ProjectLayer(1789, 2019, 2, 10, 1));
        ProjectBuilding pb = ProjectBuildingFactory.getInstance(project, pl);
        Assert.assertNotNull(pb);
        Assert.assertNotNull(pb.getBuilding());
        final Map<ProjectBuilding.YearWeek, ProjectFloor> buildings = pb.getBuilding();
        Assert.assertTrue(buildings.containsKey(pb.yearWeek(2019, 2)));
        Assert.assertTrue(buildings.containsKey(pb.yearWeek(2020, 2)));
                
        int delta =  (int) ChronoUnit.WEEKS.between(LocalDate.of(2019, 1, 8), LocalDate.now()) + 1;
        Assert.assertEquals(delta, pb.getBuilding().keySet().size());
   
        // This temporalField is used to retrieve the week number of the date into the year
        final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        Assert.assertNotNull(pb.yearWeek(LocalDate.now().getYear(), LocalDate.now().get(woy)));
        
    }

    /**
     * Testing the getInstance method from the Fitzhi repository.
     * This test has been added to fix a bug arout the new eve boundary with the week.
     */
    @Test
    public void testGetInstanceFromFitzhi() throws ApplicationException {
        Project p = new Project(2, "Project number 2");
        ProjectLayers pl = dataHandler.loadSkylineLayers(p);
        ProjectBuilding pb = ProjectBuildingFactory.getInstance(p, pl);
        Assert.assertFalse(pb.hasFloor(2018, 1));
    }

}
