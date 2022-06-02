package com.fitzhi.bean.impl.DataHandler;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.DataHandler.PathsType;
import com.fitzhi.data.internal.Project;
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
 * We test in this Junit class the method {@link DataHandler#generatePathnamesFile(com.fitzhi.data.internal.Project, com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType)}.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataHandlerGeneratePathnamesFileTest {

    @Autowired
    DataHandler dataHandler;

    @Test
    public void testSimpleProject() throws ApplicationException {

        final Project p = new Project(1789, "TheFrenchRevolution");
        p.setBranch("branchName");

        String filename = dataHandler.generatePathnamesFile(p, PathsType.PATHS_ALL);
        log.debug(String.format("Filename %s", filename));
        Assert.assertEquals("pathnames-data/1789-branchName-pathsAll.txt", filename);
    }

    @Test
    public void testSimpleProjectWithBlanks() throws ApplicationException {

        final Project p = new Project(1789, "The French Revolution");
        p.setBranch("branch name");

        String filename = dataHandler.generatePathnamesFile(p, PathsType.PATHS_ALL);
        log.debug(String.format("Filename %s", filename));
        Assert.assertEquals("pathnames-data/1789-branch_name-pathsAll.txt", filename);
    }

    @Test
    public void testSimpleProjectWithSlashes() throws ApplicationException {

        final Project p = new Project(1789, "The French Revolution");
        p.setBranch("branch/name/1.2.3");

        String filename = dataHandler.generatePathnamesFile(p, PathsType.PATHS_ALL);
        log.debug(String.format("Filename %s", filename));
        Assert.assertEquals("pathnames-data/1789-branch_name_1.2.3-pathsAll.txt", filename);
    }

    /**
     * If the project has no branch name set, generatePathnamesFile should sthrow an {@link ApplicationException}.
     * @throws ApplicationException
     */
    @Test(expected=ApplicationException.class)
    public void testBranchNameIsMandatory() throws ApplicationException {
        final Project p = new Project(1789, "The French Revolution");
        // method should trow a ApplicationException.
        dataHandler.generatePathnamesFile(p, PathsType.PATHS_ALL);
    }

}

