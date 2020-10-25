package com.fitzhi;

import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;

public class SimpleTest {
    
    @Test
    public void test() {
        /*
        for (Locale local : Locale.getAvailableLocales()) {
            System.out.println(local);
        }
        */
        System.out.println(Locale.getDefault());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.WEEK_OF_YEAR, 10);
        System.out.println(calendar.getTime());
    }
}
