package com.fitzhi.bean.impl;

import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.exception.SkillerException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * We test here the methods
 * <ul>
 * <li> 
 * {@link DataHandler#loadSkylineLayers(com.fitzhi.data.internal.Project)}.
 * </li>
 * <Li>
 * {@link DataHandler#saveSkylineLayers(com.fitzhi.data.internal.Project, java.util.List)}
 * </li>
 * </ul>
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataHandlerLoadAndSaveSkylineLayersTest {
   
    @Autowired
	DataHandler dataHandler;

    @Autowired
	ProjectHandler projectHandler;
   
    @Before
    public void before() throws SkillerException {
        Project project = new Project(1214, "Bouvines");
        projectHandler.getProjects().put(1214, project);
    }

    @Test
    public void testHasSavedSkylineLayers() throws SkillerException {
        boolean exist = dataHandler.hasSavedSkylineLayers(new Project(1789, "Revolution"));
        Assert.assertFalse("dataHandler.hasSavedSkylineLayers returns FALSE for new project", exist);
    }

    @Test
    public void testSavingSkylineLayers() throws SkillerException {

        List<ProjectLayer> layers = new ArrayList<>(); 
        
        ProjectLayer p = new ProjectLayer(1214, 2020, 20, 10, 1);
        layers.add(p);
        layers.add(new ProjectLayer(1214, 2020, 20, 30, 2));
        layers.add(new ProjectLayer(1214, 2020, 21, 44, 1));        
        dataHandler.saveSkylineLayers(projectHandler.get(1214), new ProjectLayers(projectHandler.get(1214), layers));

        Assert.assertTrue(
                "dataHandler.hasSavedSkylineLayers returns TRUE for saved project", 
                dataHandler.hasSavedSkylineLayers(projectHandler.get(1214)));

        ProjectLayers pl = dataHandler.loadSkylineLayers(projectHandler.get(1214));
        Assert.assertEquals(3, pl.getLayers().size());
        Assert.assertEquals(1214, pl.getLayers().get(0).getIdProject());
        Assert.assertEquals(2020, pl.getLayers().get(0).getYear());
        Assert.assertEquals(20, pl.getLayers().get(0).getWeek());
        Assert.assertEquals(10, pl.getLayers().get(0).getLines());

        Assert.assertEquals(1214, pl.getLayers().get(2).getIdProject());
        Assert.assertEquals(44, pl.getLayers().get(2).getLines());
    }

    @After
    public void after() throws SkillerException {
        projectHandler.getProjects().remove(1214);
    }

}
