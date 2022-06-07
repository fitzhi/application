package com.fitzhi.bean.impl.StaffHandler;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

/**
 * Testing the method {@link StaffHandler#hasMission(int, int)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerHasMissionTest {

	@Autowired
	private StaffHandler staffHandler;

	@MockBean
	private DataHandler dataHandler;

	private Staff staff() {
		Staff staff = new Staff(1789, "user", "password");
		Mission m = new Mission(1789, 1, "Nope");
		staff.addMission(m);
		return staff;
	}

	private Map<Integer, Staff> mapStaff() {
		Map<Integer, Staff> map = new HashMap<>();
		map.put(1789, staff());
		return map;
	}

	@Test (expected = NotFoundException.class)
	public void staffNotFound() throws ApplicationException {
		staffHandler.hasMission(1789, 1);
	}

	@Test
	public void nominal() throws ApplicationException {
		staffHandler.init();
		when(dataHandler.loadStaff()).thenReturn(mapStaff());
		Assert.assertTrue(staffHandler.hasMission(1789, 1));
		Assert.assertFalse(staffHandler.hasMission(1789, 2));
	}
}
