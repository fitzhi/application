package com.fitzhi.bean.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataHandlerLoadPathsTest {
 
    @Autowired
    DataHandler dataHandler;

    Project project;
    
    @Before
    public void before() {
        project = new Project (1571, "Lepante");
        project.setBranch("master");
    }

    /**
     * Test that we correctly load the ADDED paths into their dedicated file.
     * @throws SkillerException
     */
    @Test
    public void testLoadAddedPaths() throws SkillerException, IOException {
        List<String> paths = new ArrayList<>();
        paths.add("one");
        paths.add("two");
        dataHandler.savePaths(project, paths, PathsType.PATHS_ADDED);
        File f = new File("./src/test/resources/out_dir/pathnames-data/1571-master-pathsAdded.txt");
        if (log.isDebugEnabled()) {
            log.debug(String.format("dataHandler.savePaths into %s ", f.getAbsolutePath()));
        }

        List<String> p = dataHandler.loadPaths(project, PathsType.PATHS_ADDED);
        Assert.assertEquals("First line equal to 'one'", "one", p.get(0));
    }

    /**
     * Test that we correctly save the MODIFIED paths into their dedicated file.
     * @throws SkillerException
     */
    @Test
    public void testLoadModifiedPaths() throws SkillerException, IOException {
        List<String> paths = new ArrayList<>();
        paths.add("three");
        paths.add("four");
        dataHandler.savePaths(project, paths, PathsType.PATHS_MODIFIED);
        File f = new File("./src/test/resources/out_dir/pathnames-data/1571-master-pathsModified.txt");
        if (log.isDebugEnabled()) {
            log.debug(String.format("dataHandler.savePaths into %s ", f.getAbsolutePath()));
        }

        List<String> p = dataHandler.loadPaths(project, PathsType.PATHS_MODIFIED);
        Assert.assertEquals("First line equal to 'three'", "three", p.get(0));
    }

    /**
     * Test that we correctly save the CANDIDATE paths into their dedicated file.
     * @throws SkillerException
     */
    @Test
    public void testLoadCandidatePaths() throws SkillerException, IOException {
        List<String> paths = new ArrayList<>();
        paths.add("five");
        paths.add("six");
        dataHandler.savePaths(project, paths, PathsType.PATHS_CANDIDATE);
        File f = new File("./src/test/resources/out_dir/pathnames-data/1571-master-pathsCandidate.txt");
        if (log.isDebugEnabled()) {
            log.debug(String.format("dataHandler.savePaths into %s ", f.getAbsolutePath()));
        }

        List<String> p = dataHandler.loadPaths(project, PathsType.PATHS_CANDIDATE);
        Assert.assertEquals("First line equal to 'five'", "five", p.get(0));    }

    /**
     * Test that we correctly save all paths into their dedicated file.
     * @throws SkillerException
     */
    @Test
    public void testLoadAllPaths() throws SkillerException, IOException {
        List<String> paths = new ArrayList<>();
        paths.add("ten");
        paths.add("eleven");
        dataHandler.savePaths(project, paths, PathsType.PATHS_ALL);
        File f = new File("./src/test/resources/out_dir/pathnames-data/1571-master-pathsAll.txt");
        if (log.isDebugEnabled()) {
            log.debug(String.format("dataHandler.savePaths into %s ", f.getAbsolutePath()));
        }

        List<String> p = dataHandler.loadPaths(project, PathsType.PATHS_ALL);
        Assert.assertEquals("First line equal to 'ten'", "ten", p.get(0));
    }


}
