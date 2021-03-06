package com.fitzhi;

import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTest {
    
    @Test
    public void test() {
        /*
        for (Locale local : Locale.getAvailableLocales()) {
            System.out.println(local);
        }
        */
        log.debug(Locale.getDefault().toString());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.WEEK_OF_YEAR, 10);
        log.debug(calendar.getTime().toString());
    }
}
