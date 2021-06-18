package com.fitzhi.bean.impl.skykine;

import java.time.LocalDate;

import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SourceCodeDiffChange;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.source.crawler.git.SourceChange;

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
 * Test the {@link SkylineProcessor#actualizeStaff(com.fitzhi.data.internal.Project, com.fitzhi.data.internal.SourceControlChanges) }
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkylineProcessorActualizeStaffTest {

	@Autowired
    SkylineProcessor skylineProcessor;

    @Autowired
    StaffHandler staffHandler;
   
    @Before
    public void before() {

        staffHandler.getStaff()
            .put(1964,
            new Staff(1964, "Joe", "DALTON", "jdalton", "jdalton", "jdalton@nope.com", ""));
    }

    @Test
    public void test() {
        Project project = new Project(1789, "The revolutionary project");
        SourceControlChanges changes = new SourceControlChanges();
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 02),
            "jdalton",
            "authorEmail",
            1,
            new SourceCodeDiffChange("one", 5, 10) ));
        changes.addChange("one", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 03),
            "jdalton",
            "authorEmail",
            1,
            new SourceCodeDiffChange("two", 2, 4) ));
        changes.addChange("two", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 8),
            "joe dalton",
            "authorEmail",
            1,
            new SourceCodeDiffChange("two", 2, 4) ));
        changes.addChange("three", new SourceChange(
            "commit", 
            LocalDate.of(2020, 01, 03),
            "authorName",
            "authorEmail",
            1,
            new SourceCodeDiffChange("three", 2, 4) ));
            
        skylineProcessor.actualizeStaff(project, changes);
        changes.getChanges().values().stream().flatMap(sfh -> sfh.getChanges().stream()).
        forEach(sc -> {
            if ("one".equals(sc.getDiff().getFilename())) {
                Assert.assertEquals(1964, sc.getIdStaff());
            }
            if ("two".equals(sc.getDiff().getFilename())) {
                Assert.assertEquals(1964, sc.getIdStaff());
            }
            if ("three".equals(sc.getDiff().getFilename())) {
                Assert.assertEquals(-1, sc.getIdStaff());
            }
        });
    }

    @After
    public void after() {
        staffHandler.getStaff().remove(1964);
    }
}
