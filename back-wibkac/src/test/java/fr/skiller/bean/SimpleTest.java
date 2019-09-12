package fr.skiller.bean;

import java.time.LocalDate;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.controller.util.LocalDateAdapter;
import fr.skiller.data.internal.Mission;

/**
 * <p>Simple class used for simple test.</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class SimpleTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();
	
	@Test
	public void test() throws Exception {

		String s = "{\"idStaff\":1,\"firstName\":\"Frédéric\",\"lastName\":\"VIDAL\",\"nickName\":\"fvidal\",\"login\":\"frvidal\",\"email\":\"frederic.vidal.pro@gmail.com\",\"level\":null,\"password\":\"thePassword\",\"active\":false,\"dateInactive\":null,\"application\":null,\"typeOfApplication\":0,\"external\":false,\"missions\":[{\"idStaff\":1,\"idProject\":777,\"name\":\"First test\","+
		"\"firstCommit\":\"2019-06-19\",\"lastCommit\":\"2019-06-27\",\"numberOfCommits\":6,\"numberOfFiles\":9}],\"experiences\":[],\"authorities\":[{\"authority\":\"ROLE_USER\"}],\"empty\":false,\"username\":\"frvidal\",\"enabled\":false,\"accountNonExpired\":false,\"accountNonLocked\":false,\"credentialsNonExpired\":false}";

		Mission m = new Mission(1, 1, "test");
		m.setFirstCommit(LocalDate.of(2019, 9, 7));
		System.out.println(gson.toJson(m));
	}
}
	
