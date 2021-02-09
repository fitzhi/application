package com.fitzhi.bean.impl;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
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
 * Testing the mehods
 * <ul>
 * <li> 
 * {@link DataHandler#loadProjectBuilding(com.fitzhi.data.internal.Project)}
 * </li>
 * <li> 
 * {@link DataHandler#saveProjectBuilding(com.fitzhi.data.internal.Project, com.fitzhi.data.internal.ProjectBuilding)}
 * </li>
 * </ul>
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataHandlerSaveAndLoadProjectBuildingTest {
    
    @Autowired
    DataHandler dataHandler;

    @Autowired
    SkylineProcessor skylineProcessor;


    @Autowired
    StaffHandler staffHandler;

    @Test
    public void testSaveAndLoad() throws ApplicationException {

        staffHandler.getStaff(1).setActive(true);
        staffHandler.getStaff(2).setActive(true);

        Project project = new Project(1796, "Castiglione !");
        ProjectBuilding building = this.skylineProcessor.generateProjectBuilding(project);        
        Assert.assertTrue(building.getBuilding().size() > 0);
        int size = building.getBuilding().size();
        if (log.isDebugEnabled()) {
            log.debug(String.format("Saving a building of %d floors", size));
        }
        dataHandler.saveProjectBuilding(project, building);

        ProjectBuilding loaded = dataHandler.loadProjectBuilding(project);
        Assert.assertEquals(size, loaded.getBuilding().size());
    }
}
