package com.fitzhi.bean.impl;

import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Testing the method
 * {@link SkylineProcessor#generateProjectBuilding(com.fitzhi.data.internal.Project)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkylineProcessorGenerateBuildingTest {

    @Autowired
    SkylineProcessor skylineProcessor;

    /**
     * As the name of this unit test is stating, 
     * we do throw an exception if the {@code N-project-layers.json} file
     */
    @Test
    public void testDoNotThrowAnExceptionIfNoSkylineFileExists() throws SkillerException {
        Project p = new Project(1789, "The Revolution project");
        skylineProcessor.generateProjectBuilding(p);
    }
}