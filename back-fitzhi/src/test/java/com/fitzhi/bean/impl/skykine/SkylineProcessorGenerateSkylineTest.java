package com.fitzhi.bean.impl.skykine;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Skyline;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test the {@link SkylineProcessor#generateSkyline()}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkylineProcessorGenerateSkylineTest {
    
    Map<Integer, Project> projects = new HashMap<>();
    
    @Autowired
    SkylineProcessor skylineProcessor;

    @MockBean
    ProjectHandler projectHandler;
    
    @Before
    public void before() {
    }
    
    @Test
    public void testAProjectWithoutSkyline() throws ApplicationException {
        projects.put(1, new Project(1, "One"));
        when(projectHandler.getProjects()).thenReturn(projects);
        Skyline skyline = skylineProcessor.generateSkyline();
        Assert.assertTrue("skyline is empty", skyline.isEmpty());
    }

    @Test
    public void testAProjectWithARegisteredSkyline() throws ApplicationException {
        projects.put(1, new Project(1796, "One"));
        when(projectHandler.getProjects()).thenReturn(projects);
        Skyline skyline = skylineProcessor.generateSkyline();
        Assert.assertFalse("skyline is empty", skyline.isEmpty());
    }

}
