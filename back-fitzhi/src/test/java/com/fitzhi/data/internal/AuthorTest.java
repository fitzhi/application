package com.fitzhi.data.internal;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing the {@link Author} class.
 */
public class AuthorTest {

    @Test
    public void test() {
        Author authorOne = new Author("Frédéric VIDAL", "frederic.vidal@fitzhi.com");
        Assert.assertEquals("Author(name=Frédéric VIDAL, email=frederic.vidal@fitzhi.com)", authorOne.toString());

        Author authorTwo = new Author("Frédéric VIDAL", "frederic.vidal@fitzhi.com");
        Assert.assertTrue(authorOne.equals(authorTwo));        
        Assert.assertEquals(authorOne.hashCode(), authorTwo.hashCode());        
    }
    
}
