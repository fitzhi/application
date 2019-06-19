package fr.skiller.bean.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

/**
 * Class in charge of testing the saveStaffMember method inside StaffHandler.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerSaveStaffMemberTest {

	private static final String AHMINE = "Ahmine";
	private static final String MOUAAMOU_VOID_COM = "mouaamou@void.com";
	private static final String MOUAAMOU = "mouaamou";
	private static final String OUAAMOU = "Ouaamou";
	@Autowired
	private StaffHandler staffHandler;

	@Before
	public void before() {
		staffHandler.getStaff().put(1000, 
				new Staff(1000,"Christian Aligato", "Chavez Tugo", "cact" , "cact", "cact@void.com", ""));
		staffHandler.getStaff().put(1001, 
				new Staff(1001, OUAAMOU, "Mohammed", MOUAAMOU , MOUAAMOU, MOUAAMOU_VOID_COM, ""));
	}

	@Test
	public void testSaveStaffMemberOk() throws SkillerException {
		Staff st = new Staff(1001, OUAAMOU, AHMINE, MOUAAMOU , MOUAAMOU, MOUAAMOU_VOID_COM, "");
		staffHandler.saveStaffMember(st);
		Staff s = staffHandler.getStaff().get(1001);
		Assert.assertEquals (AHMINE, s.getLastName());
	}

	
	@Test (expected=SkillerException.class)
	public void testSaveStaffMemberKoNonUniqueLogin() throws SkillerException {
		// 
		Staff st = new Staff(1001, OUAAMOU, AHMINE, MOUAAMOU , "cact", MOUAAMOU_VOID_COM, "");
		staffHandler.saveStaffMember(st);
	}

	@Test (expected=SkillerException.class)
	public void testSaveStaffMemberKoUnregisteredStaff() throws SkillerException {
		// 
		Staff st = new Staff(0, OUAAMOU, AHMINE, MOUAAMOU , "cact", MOUAAMOU_VOID_COM, "");
		staffHandler.saveStaffMember(st);
	}

	@After
	public void after() {
		staffHandler.getStaff().remove(1000);
	}
}
