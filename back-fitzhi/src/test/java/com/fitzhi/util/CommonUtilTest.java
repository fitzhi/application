package com.fitzhi.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class CommonUtilTest {
    
    @Test
    public void extractProjectFromUrlDotGit() {
        Assert.assertEquals("spring-framework", CommonUtil.extractProjectNameFromUrl("https://github.com/spring-projects/spring-framework.git"));
    }

    @Test
    public void extractProjectFromUrl() {
        Assert.assertEquals("spring-framework", CommonUtil.extractProjectNameFromUrl("https://github.com/spring-projects/spring-framework"));
    }

}
