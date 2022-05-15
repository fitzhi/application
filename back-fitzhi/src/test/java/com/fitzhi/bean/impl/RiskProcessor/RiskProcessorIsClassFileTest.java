package com.fitzhi.bean.impl.RiskProcessor;

import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p> Test of the method {@link RiskCommitAndDevActiveProcessorImpl.isClassFile} </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskProcessorIsClassFileTest {
    
    @Test
    public void found() {
        Assert.assertTrue(RiskCommitAndDevActiveProcessorImpl.isClassFile("a/very/path/ClassA", "ClassA"));
    }

    @Test
    public void notFound1() {
        Assert.assertFalse(RiskCommitAndDevActiveProcessorImpl.isClassFile("a/alassA/path/ClassB", "ClassA"));
    }

    @Test
    public void notFound2() {
        Assert.assertFalse(RiskCommitAndDevActiveProcessorImpl.isClassFile("a/very/path/ClassA.class", "ClassB"));
    }
}
