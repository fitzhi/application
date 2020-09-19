/**
 * 
 */
package com.fitzhi.bean.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.data.internal.DataChart;

/**
 * <p>We test here the class <code>DataChartHandlerImpl</code>.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataChartHandlerTest {

	@Autowired
	DataChartHandler dataChartHandler;
	
	@Test
	public void testAggregateDatachart()  {
		DataChart root = new DataChart("root");

		DataChart fr = new DataChart("fr");
		DataChart com = new DataChart("com");

		DataChart frEmptyOne = new DataChart("one");
		DataChart comNotEmptyOne = new DataChart("oneNotEmpty");
		DataChart comEmptyOne = new DataChart("oneEmpty");
		
		DataChart[] subOrg = new DataChart[10];
		subOrg[0] = new DataChart("org-0");
		
		int[] dev = new int[1];

		// One source file in fr, one sub-directory.
		root.addSubDir(fr);
		fr.addSource("fr/file", 1, LocalDate.now(), dev);
		fr.addSubDir(frEmptyOne);
		
		// No source file neither in com, nor com_empty_one, but ONE file in COM_NOT_EMPTY_ONE
		root.addSubDir(com);
		com.addSubDir(comEmptyOne);
		com.addSubDir(comNotEmptyOne);
		comNotEmptyOne.addSource("oneNotEmpty/file", 1, LocalDate.now(), dev);
		
		root.addSubDir(subOrg[0]);
		for (int i=1; i<10; i++) {
			subOrg[i] = new DataChart("org-"+i);
			subOrg[i-1].addSubDir(subOrg[i]);
		}
		subOrg[9].addSource("file", 1, LocalDate.now(), dev);
		
		dataChartHandler.aggregateDataChart(root);
		
		Assert.assertEquals(3, root.getChildren().size());

		Set<String> set = root.getChildren().stream().map(dc -> dc.getLocation()).collect(Collectors.toSet());

		Assert.assertTrue(set.contains("fr"));
		Assert.assertTrue(set.contains("com"));
//		Assert.assertTrue(set.contains("org-0/org-1/org-2/org-3/org-4/org-5/org-6/org-7/org-8/org-9"));
		
		StringBuilder sb = new StringBuilder();
		root.dump(sb, "");
		System.out.println(sb);
	}
	
	@Test
	public void testClone() {
		int[] dev = new int[1];
		dev[0]=1;
		DataChart dataChart = new DataChart("location");
		dataChart.addSource("filename", 1, LocalDate.now(), dev);
		dataChart.setColor("color");
		dataChart.addSubDir(new DataChart("inner"));
		
		DataChart clon = (DataChart) deepClone(dataChart);

		Assert.assertNotNull(clon);
		if (clon != null) {
			Assert.assertEquals(dataChart.getLocation(), clon.getLocation());
			Assert.assertEquals(dataChart.getColor(), clon.getColor());
			Assert.assertEquals(dataChart.getClassnames().size(), clon.getClassnames().size());
			Assert.assertEquals(dataChart.getClassnames().toArray()[0], clon.getClassnames().toArray()[0]);
		}
	}

	 public Object deepClone(Object object) {
	   try {
	     ByteArrayOutputStream baos = new ByteArrayOutputStream();
	     ObjectOutputStream oos = new ObjectOutputStream(baos);
	     oos.writeObject(object);
	     ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	     ObjectInputStream ois = new ObjectInputStream(bais);
	     return ois.readObject();
	   }
	   catch (Exception e) {
	     log.error("error", e);
	     return null;
	   }
	 }
}
