package com.fitzhi.bean.impl;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectFloor;
import com.fitzhi.exception.SkillerException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Testing 2 methods inside {@link SkylineProcessor}
 * <ul>
 * <Li>
 * {@link SkylineProcessor#generateProjectBuilding(com.fitzhi.data.internal.Project)}
 * </li>
 * <li>
 * {@link SkylineProcessor#generateProjectBuilding(java.util.List) }
 * </li>
 * </ul>
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkylineProcessorGenerateProjectBuildingTest {

    @Autowired
    SkylineProcessor skylineProcessor;

    @Autowired
    DataHandler dataHandler;
   
    @Autowired
    StaffHandler staffHandler;

    @Test
    public void testGenerateBuildingForAGivenProject() throws SkillerException {

        staffHandler.getStaff(1).setActive(true);
        staffHandler.getStaff(2).setActive(false);

        Project project = new Project(1515, "Marignan !");
        ProjectBuilding building = this.skylineProcessor.generateProjectBuilding(project);        
        Assert.assertNotNull(building);
        ProjectFloor floor = building.getProjectFloor(2020, 20);
        Assert.assertEquals(10, floor.getNumberOfLinesByActiveDevelopers());
        Assert.assertEquals(30, floor.getNumberOfLinesByInactiveDevelopers());
        floor = building.getProjectFloor(2020, 21);
        Assert.assertEquals(44, floor.getNumberOfLinesByActiveDevelopers());
    }

    @Test
    public void anotherTestGenerateBuildingForAGivenProject() throws SkillerException {

        staffHandler.getStaff(1).setActive(true);
        staffHandler.getStaff(2).setActive(true);

        Project project = new Project(1515, "Marignan !");
        ProjectBuilding building = this.skylineProcessor.generateProjectBuilding(project);        
        Assert.assertNotNull(building);
        ProjectFloor floor = building.getProjectFloor(2020, 20);
        Assert.assertEquals(40, floor.getNumberOfLinesByActiveDevelopers());
        Assert.assertEquals(0, floor.getNumberOfLinesByInactiveDevelopers());
        floor = building.getProjectFloor(2020, 21);
        Assert.assertEquals(44, floor.getNumberOfLinesByActiveDevelopers());
    }

    @Test
    public void oneAnotherTestGenerateBuildingForAGivenProject() throws SkillerException {

        staffHandler.getStaff(1).setActive(false);
        staffHandler.getStaff(2).setActive(false);

        Project project = new Project(1515, "Marignan !");
        ProjectBuilding building = this.skylineProcessor.generateProjectBuilding(project);        
        Assert.assertNotNull(building);
        ProjectFloor floor = building.getProjectFloor(2020, 20);
        Assert.assertEquals(0, floor.getNumberOfLinesByActiveDevelopers());
        Assert.assertEquals(40, floor.getNumberOfLinesByInactiveDevelopers());
        floor = building.getProjectFloor(2020, 21);
        Assert.assertEquals(44, floor.getNumberOfLinesByInactiveDevelopers());
    }

}