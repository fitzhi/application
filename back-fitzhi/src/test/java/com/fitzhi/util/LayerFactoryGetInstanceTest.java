package com.fitzhi.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;

import com.fitzhi.data.internal.Layer;
import com.fitzhi.source.crawler.git.SourceChange;

import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Testing the method {@link LayerFactory#getInstance(com.fitzhi.source.crawler.git.SourceChange)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
public class LayerFactoryGetInstanceTest {

    Calendar calendar = Calendar.getInstance();
    
    @Test
    public void testLastWeek() {

        log.info(String.format("Locale %s", Locale.getDefault().toString()));

        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);     
        calendar.set(Calendar.DAY_OF_MONTH, 29);

//        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZoneId zoneId = ZoneId.of("Etc/UTC");
        log.info(String.format("System ZoneId %s", ZoneId.systemDefault().toString()));
        log.info(String.format("Application ZoneId %s", zoneId.toString()));
        
        LocalDate date = calendar.getTime().toInstant().atZone(zoneId).toLocalDate();
        Layer layer = LayerFactory.getInstance(new SourceChange(date, 1));
        Assert.assertEquals("Testing the year for date " + date, 2019, layer.getYear());
        Assert.assertEquals("Testing the week for date " + date, 52, layer.getWeek());

        calendar.set(Calendar.DAY_OF_MONTH, 30);
        date = calendar.getTime().toInstant().atZone(zoneId).toLocalDate();
        layer = LayerFactory.getInstance(new SourceChange(date, 1));
        Assert.assertEquals("Testing the year for date " + date, 2020, layer.getYear());
        Assert.assertEquals("Testing the week for date " + date, 1, layer.getWeek());
    }

}
