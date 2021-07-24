package com.fitzhi.bean.impl.skykine;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.SourceControlChanges;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test the
 * {@link SkylineProcessor#generateProjectLayers(com.fitzhi.data.internal.Project, com.fitzhi.data.internal.SourceControlChanges)
 * Skyline layers generation}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkylineProcessorGenerateProjectLayersFontenoyTest {

    @Autowired
    SkylineProcessor skylineProcessor;

    @Autowired
    DataHandler dataHandler;

    @Test
    public void testFontenoy() throws Exception {

        Project project = new Project(841, "Fontenoy");
        project.add(new Library("/abcde/fghijk", 2));
        
        SourceControlChanges scc = dataHandler.loadChanges(project);

        ProjectLayers pl = skylineProcessor.generateProjectLayers(project, scc);
        int sum = pl.getLayers().stream().map(ProjectLayer::getLines).reduce(0, Integer::sum);
        Assert.assertEquals(18, sum);
        
}}
