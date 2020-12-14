package com.fitzhi.source.crawler.git;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing the method {@link GitCrawler#progressionPercentage(int, int)}
 */
public class GitCrawlerProgressionPercentageTest {
    
    @Test
    public void test() {
        int p = GitCrawler.progressionPercentage(100, 832);
        Assert.assertEquals(38, p);
        p = GitCrawler.progressionPercentage(200, 832);
        Assert.assertEquals(46, p);
        p = GitCrawler.progressionPercentage(800, 832);
        Assert.assertEquals(97, p);
    }
}
