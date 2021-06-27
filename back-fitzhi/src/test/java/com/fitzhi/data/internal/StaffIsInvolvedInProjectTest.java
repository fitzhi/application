package com.fitzhi.data.internal;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing the method {@link Staff#isInvolvedInProject(int)}
 */
public class StaffIsInvolvedInProjectTest {

    @Test
    public void found() {
        Staff staff = new Staff(1789, "frvidal", "");
        staff.addMission(new Mission(1789, 1, "one"));
        Assert.assertTrue(staff.isInvolvedInProject(1));
    }

    @Test
    public void notFound() {
        Staff staff = new Staff(1789, "frvidal", "");
        staff.addMission(new Mission(1789, 1, "one"));
        Assert.assertFalse(staff.isInvolvedInProject(2));
    }
    
}
