package com.fitzhi.bean.impl;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectFloor;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class SkylineProcessorGenerateProjectBuildingTest {

    static{
        Locale.setDefault(Locale.UK);
    }
    
    @Autowired
    SkylineProcessor skylineProcessor;

    @Autowired
    DataHandler dataHandler;
   
    @Autowired
    StaffHandler staffHandler;

    // This temporalField is used to retrieve the week number of the date into the year
    private final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
   
    @Test
    public void testGenerateBuildingForAOneSingleLineProject() throws ApplicationException {

        Staff staff = staffHandler.lookup(1);
        staff.setActive(false);
        // Starting from week 35, this developer is inactive.
        staff.setDateInactive(LocalDate.of(2020, 8, 30));

        if (log.isDebugEnabled()) {
            log.debug(String.format("Staff %s %s is inactive since week %d", staff.getFirstName(), staff.getLastName(), staff.getDateInactive().get(woy)));
        }

        Project project = new Project(1796, "Castiglione !");
        ProjectBuilding building = this.skylineProcessor.generateProjectBuilding(project);        
        Assert.assertNotNull(building);
        
        ProjectFloor floor = building.getFloor(2020, 20);
        Assert.assertEquals(10, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 35);
        Assert.assertEquals(10, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 36);
        Assert.assertEquals(0, floor.getLinesActiveDevelopers());
        Assert.assertEquals(10, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 43);
        Assert.assertEquals(0, floor.getLinesActiveDevelopers());
        Assert.assertEquals(10, floor.getLinesInactiveDevelopers());

    }

    @Test
    public void testGenerateBuildingForAGivenProject() throws ApplicationException {

        staffHandler.lookup(1).setActive(true);
        staffHandler.lookup(2).setActive(false);
        // Week 5
        staffHandler.lookup(2).setDateInactive(LocalDate.of(2020, 8, 30));

        if (log.isDebugEnabled()) {
            Staff staff = staffHandler.lookup(2);
            log.debug(String.format("Staff %s %s is inactive since week %d", staff.getFirstName(), staff.getLastName(), staff.getDateInactive().get(woy)));
        }

        Project project = new Project(1515, "Marignan !");
        ProjectBuilding building = this.skylineProcessor.generateProjectBuilding(project);        
        Assert.assertNotNull(building);
        ProjectFloor floor = building.getFloor(2020, 20);
        Assert.assertEquals(40, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 25);
        Assert.assertEquals(84, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 36);
        Assert.assertEquals(54, floor.getLinesActiveDevelopers());
        Assert.assertEquals(30, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 43);
        Assert.assertEquals(54, floor.getLinesActiveDevelopers());
        Assert.assertEquals(30, floor.getLinesInactiveDevelopers());

    }

    @Test
    public void anotherTestGenerateBuildingForAGivenProject() throws ApplicationException {

        staffHandler.lookup(1).setActive(true);
        staffHandler.lookup(2).setActive(true);

        Project project = new Project(1515, "Marignan !");
        ProjectBuilding building = this.skylineProcessor.generateProjectBuilding(project);        
        Assert.assertNotNull(building);
        ProjectFloor floor = building.getFloor(2020, 20);
        Assert.assertEquals(40, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 24);
        Assert.assertEquals(40, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 25);
        Assert.assertEquals(84, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 43);
        Assert.assertEquals(84, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

    }

    @Test
    public void oneAnotherTestGenerateBuildingForAGivenProject() throws ApplicationException {

        staffHandler.lookup(1).setActive(false);
        staffHandler.lookup(1).setDateInactive(LocalDate.of(2020, 7, 14));
        staffHandler.lookup(2).setActive(false);
        staffHandler.lookup(2).setDateInactive(LocalDate.of(2020, 8, 14));

        if (log.isDebugEnabled()) {
            Staff staff = staffHandler.lookup(1);
            log.debug(String.format("Staff %s %s is inactive since week %d", staff.getFirstName(), staff.getLastName(), staff.getDateInactive().get(woy)));
            staff = staffHandler.lookup(2);
            log.debug(String.format("Staff %s %s is inactive since week %d", staff.getFirstName(), staff.getLastName(), staff.getDateInactive().get(woy)));
        }
       
        Project project = new Project(1515, "Marignan !");
        ProjectBuilding building = this.skylineProcessor.generateProjectBuilding(project);        
        Assert.assertNotNull(building);
        
        ProjectFloor floor = building.getFloor(2020, 20);
        Assert.assertEquals(40, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 25);
        Assert.assertEquals(84, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 29);
        Assert.assertEquals(84, floor.getLinesActiveDevelopers());
        Assert.assertEquals(0, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 30);
        Assert.assertEquals(30, floor.getLinesActiveDevelopers());
        Assert.assertEquals(54, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 33);
        Assert.assertEquals(30, floor.getLinesActiveDevelopers());
        Assert.assertEquals(54, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 34);
        Assert.assertEquals(0, floor.getLinesActiveDevelopers());
        Assert.assertEquals(84, floor.getLinesInactiveDevelopers());

        floor = building.getFloor(2020, 43);
        Assert.assertEquals(0, floor.getLinesActiveDevelopers());
        Assert.assertEquals(84, floor.getLinesInactiveDevelopers());

    }

}