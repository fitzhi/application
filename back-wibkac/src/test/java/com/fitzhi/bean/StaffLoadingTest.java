/**
 * 
 */
package com.fitzhi.bean;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffLoadingTest {

	@Autowired
	StaffHandler staffHandler;

	@Test
	public void test() throws Exception {
		assert(staffHandler != null);
		Map<Integer, Staff> staff = staffHandler.getStaff();
		assert(staff != null);
	}
}
