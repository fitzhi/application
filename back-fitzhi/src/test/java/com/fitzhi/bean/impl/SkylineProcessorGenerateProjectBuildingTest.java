package com.fitzhi.bean.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.exception.SkillerException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
   
    @Test
    public void testGenerateBuildingForAGivenProject() throws SkillerException {

        Project project = new Project(1515, "Marignan !");
        ProjectBuilding pb = this.skylineProcessor.generateProjectBuilding(project);        
        Assert.assertNotNull(pb);
        
    }
}