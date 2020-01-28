/**
 * 
 */
package com.tixhi.bean.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.tixhi.bean.impl.PropectDashboardCustomizerImpl;
import com.tixhi.data.source.Operation;

/**
 * <p>Testing the method {@link PropectDashboardCustomizerImpl#removeDuplicate(List, int, java.time.LocalDate, int)}</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
public class ProjectDashboardCustomizerRemoveDuplicateTest {

	@Test
	public void testOne() {
		
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(2, "one", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 8)));
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 9)));
		operations.add(new Operation(1, "onebis", LocalDate.of(2019, 12, 7)));

		PropectDashboardCustomizerImpl.removeDuplicateEntries(operations, 1, LocalDate.of(2019, 12, 7), 1);
		Assert.assertEquals(4, operations.size());
	}

	@Test
	public void testTwo() {
		
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(2, "one", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 8)));
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 9)));
		operations.add(new Operation(1, "onebis", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(1, "oneter", LocalDate.of(2019, 12, 7)));

		PropectDashboardCustomizerImpl.removeDuplicateEntries(operations, 1, LocalDate.of(2019, 12, 7), 2);
		Assert.assertEquals(4, operations.size());
	}
	
	@Test
	public void testOneBis() {
		
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(2, "one", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 8)));
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 9)));
		operations.add(new Operation(1, "onebis", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(1, "oneter", LocalDate.of(2019, 12, 7)));

		PropectDashboardCustomizerImpl.removeDuplicateEntries(operations);
		Assert.assertEquals(4, operations.size());
	}

	@Test
	public void testTwoBis() {
		
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(2, "one", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 8)));
		operations.add(new Operation(1, "one", LocalDate.of(2019, 12, 9)));
		operations.add(new Operation(1, "onebis", LocalDate.of(2019, 12, 7)));
		operations.add(new Operation(1, "oneter", LocalDate.of(2019, 12, 7)));

		PropectDashboardCustomizerImpl.removeDuplicateEntries(operations);
		Assert.assertEquals(4, operations.size());
	}


}


