package com.fitzhi.bean.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
public class DataHandlerSavePathsTest {
 
    @Autowired
    DataHandler dataHandler;

    Project project;
    
    @Before
    public void before() {
        project = new Project (1571, "Lepante");
        project.setBranch("master");
    }

    /**
     * Return the first line of the given file
     * @param f the given file
     * @return the first line
     * @throws IOException
     */
    private String firstLine(File f) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            return br.readLine();
        }
    }

    /**
     * Test that we correctly save the ADDED paths into their dedicated file.
     * @throws SkillerException
     */
    @Test
    public void testSaveAddedPaths() throws SkillerException, IOException {
        List<String> paths = new ArrayList<>();
        paths.add("one");
        paths.add("two");
        dataHandler.savePaths(project, paths, PathsType.PATHS_ADDED);
        File f = new File("./src/test/resources/out_dir/pathnames-data/1571-master-pathsAdded.txt");
        if (log.isDebugEnabled()) {
            log.debug(String.format("dataHandler.savePaths into %s ", f.getAbsolutePath()));
        }
        Assert.assertTrue("Test that we correctly save the added paths into their dedicated file", f.exists());
        Assert.assertEquals("First line equal to 'one'", "one", firstLine(f));
    }

    /**
     * Test that we correctly save the MODIFIED paths into their dedicated file.
     * @throws SkillerException
     */
    @Test
    public void testSaveModifiedPaths() throws SkillerException, IOException {
        List<String> paths = new ArrayList<>();
        paths.add("three");
        paths.add("four");
        dataHandler.savePaths(project, paths, PathsType.PATHS_MODIFIED);
        File f = new File("./src/test/resources/out_dir/pathnames-data/1571-master-pathsModified.txt");
        if (log.isDebugEnabled()) {
            log.debug(String.format("dataHandler.savePaths into %s ", f.getAbsolutePath()));
        }
        Assert.assertTrue("Test that we correctly save the MODIFIED paths into their dedicated file", f.exists());
        Assert.assertEquals("First line equal to 'three'", "three", firstLine(f));
    }

    /**
     * Test that we correctly save the CANDIDATE paths into their dedicated file.
     * @throws SkillerException
     */
    @Test
    public void testSaveCandidatePaths() throws SkillerException, IOException {
        List<String> paths = new ArrayList<>();
        paths.add("five");
        paths.add("six");
        dataHandler.savePaths(project, paths, PathsType.PATHS_CANDIDATE);
        File f = new File("./src/test/resources/out_dir/pathnames-data/1571-master-pathsCandidate.txt");
        if (log.isDebugEnabled()) {
            log.debug(String.format("dataHandler.savePaths into %s ", f.getAbsolutePath()));
        }
        Assert.assertTrue("Test that we correctly save the CANDIDATE paths into their dedicated file", f.exists());
        Assert.assertEquals("First line equal to 'five'", "five", firstLine(f));
    }

    /**
     * Test that we correctly save all paths into their dedicated file.
     * @throws SkillerException
     */
    @Test
    public void testSaveAllPaths() throws SkillerException, IOException {
        List<String> paths = new ArrayList<>();
        paths.add("ten");
        paths.add("eleven");
        dataHandler.savePaths(project, paths, PathsType.PATHS_ALL);
        File f = new File("./src/test/resources/out_dir/pathnames-data/1571-master-pathsAll.txt");
        if (log.isDebugEnabled()) {
            log.debug(String.format("dataHandler.savePaths into %s ", f.getAbsolutePath()));
        }
        Assert.assertTrue("Test that we correctly save the CANDIDATE paths into their dedicated file", f.exists());
        Assert.assertEquals("First line equal to 'ten'", "ten", firstLine(f));
    }


}
