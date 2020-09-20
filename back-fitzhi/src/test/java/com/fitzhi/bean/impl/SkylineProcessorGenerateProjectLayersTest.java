package com.fitzhi.bean.impl;

import java.time.LocalDate;

import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.SourceCodeDiffChange;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.source.crawler.git.SourceChange;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test the {@link SkylineProcessor#generateProjectLayers(com.fitzhi.data.internal.Project, com.fitzhi.data.internal.SourceControlChanges) Skyline layers generation}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkylineProcessorGenerateProjectLayersTest {
	
	@Autowired
    SkylineProcessor skylineProcessor;
    
    @Test
    public void testOneSingleChange() {
        Project project = new Project(1789, "The revolutionary project");
        SourceControlChanges changes = new SourceControlChanges();
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 02),
            "authorName",
            "authorEmail",
            1,
            new SourceCodeDiffChange("one", 5, 10) ));

        ProjectLayers layers = skylineProcessor.generateProjectLayers(project, changes);
        Assert.assertEquals(1, layers.getLayers().size());
        Assert.assertEquals(1789, layers.getLayers().get(0).getIdProject());
        Assert.assertEquals(2020, layers.getLayers().get(0).getYear());
        Assert.assertEquals(1, layers.getLayers().get(0).getWeek());
        Assert.assertEquals(5, layers.getLayers().get(0).getLines());
    }

    @Test
    public void testTwoChangesSameWeek() {
        Project project = new Project(1789, "The revolutionary project");
        SourceControlChanges changes = new SourceControlChanges();
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 02),
            "authorName",
            "authorEmail",
            1,
            new SourceCodeDiffChange("one", 5, 10) ));
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 03),
            "authorName",
            "authorEmail",
            1,
            new SourceCodeDiffChange("two", 2, 4) ));
    
        ProjectLayers layers = skylineProcessor.generateProjectLayers(project, changes);
        Assert.assertEquals(1, layers.getLayers().size());
        Assert.assertEquals(1789, layers.getLayers().get(0).getIdProject());
        Assert.assertEquals(2020, layers.getLayers().get(0).getYear());
        Assert.assertEquals(1, layers.getLayers().get(0).getWeek());
        Assert.assertEquals(7, layers.getLayers().get(0).getLines());

    }

    @Test
    public void testTwoWeek() {
        Project project = new Project(1789, "The revolutionary project");
        SourceControlChanges changes = new SourceControlChanges();
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 02),
            "authorName",
            "authorEmail",
            1,
            new SourceCodeDiffChange("one", 5, 10) ));
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 03),
            "authorName",
            "authorEmail",
            1,
            new SourceCodeDiffChange("two", 2, 4) ));
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 8),
            "authorName",
            "authorEmail",
            1,
            new SourceCodeDiffChange("two", 2, 4) ));
        
        ProjectLayers layers = skylineProcessor.generateProjectLayers(project, changes);
        Assert.assertEquals(2, layers.getLayers().size());
        Assert.assertEquals(1789, layers.getLayers().get(0).getIdProject());
        Assert.assertEquals(2020, layers.getLayers().get(0).getYear());

        Assert.assertEquals(1, layers.getLayers().get(0).getWeek());
        Assert.assertEquals(7, layers.getLayers().get(0).getLines());

        Assert.assertEquals(2, layers.getLayers().get(1).getWeek());
        Assert.assertEquals(2, layers.getLayers().get(1).getLines());

    }

    @Test
    public void testTwoWeeksTwoStaff() {
        Project project = new Project(1789, "The revolutionary project");
        SourceControlChanges changes = new SourceControlChanges();
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 02),
            "authorName",
            "authorEmail",
            1,
            new SourceCodeDiffChange("one", 5, 10) ));
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 03),
            "authorName",
            "authorEmail",
            2,
            new SourceCodeDiffChange("two", 2, 4) ));
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 8),
            "authorName",
            "authorEmail",
            1,
            new SourceCodeDiffChange("two", 2, 4) ));
        
        ProjectLayers layers = skylineProcessor.generateProjectLayers(project, changes);
        Assert.assertEquals(2, layers.getLayers().size());
        Assert.assertEquals(1789, layers.getLayers().get(0).getIdProject());
        Assert.assertEquals(2020, layers.getLayers().get(0).getYear());

        Assert.assertEquals(1, layers.getLayers().get(0).getWeek());
        Assert.assertEquals(7, layers.getLayers().get(0).getLines());
        Assert.assertEquals(1, layers.getLayers().get(0).getIdStaffs().get(0).intValue());
        Assert.assertEquals(2, layers.getLayers().get(0).getIdStaffs().get(1).intValue());

        Assert.assertEquals(2, layers.getLayers().get(1).getWeek());
        Assert.assertEquals(2, layers.getLayers().get(1).getLines());
        Assert.assertEquals(1, layers.getLayers().get(1).getIdStaffs().get(0).intValue());

    }

}
