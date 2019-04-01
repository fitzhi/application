package fr.skiller.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.SkillHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.Action;
import fr.skiller.data.external.PseudoListDTO;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Pseudo;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControler_saveGhosts_Test {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProjectHandler projectHandler;
	
	@Autowired
	private StaffHandler staffHandler;
		
	
	Staff first = null;
	Staff second = null;
	Staff third = null;
	
	@Before
	public void before() throws Exception {
		first = (Staff) staffHandler.getStaff().values().toArray()[0];
		second = (Staff) staffHandler.getStaff().values().toArray()[1];
		third = (Staff) staffHandler.getStaff().values().toArray()[2];
		
		Project p = new Project(8021964, "testingGhost");
		projectHandler.addNewProject(p);
		p.getGhosts().add( new Ghost("onePseudo", first.getIdStaff(), false) );
		p.getGhosts().add(new Ghost("twoPseudo", true));
		p.getGhosts().add(new Ghost("thirdPseudo", second.getIdStaff(), false));
		p.getGhosts().add(new Ghost("fourthPseudo", true));
		p.getGhosts().add( new Ghost("fifthPseudo", third.getIdStaff(), false) );
		projectHandler.addNewProject(p);
	}
	
	@Test
	public void test() throws Exception {
		
		List<Pseudo> pseudos = new ArrayList<>();
		List<Pseudo> expectedPseudos = new ArrayList<>();

		// We don't change the value
		pseudos.add(new Pseudo("onePseudo", first.getLogin()));
		expectedPseudos.add(new Pseudo("onePseudo", first.getIdStaff(), first.fullName(), first.getLogin(), false, Action.N));
		
		// We don't change the value
		pseudos.add(new Pseudo("twoPseudo", true));
		expectedPseudos.add(new Pseudo("twoPseudo", true, Action.N));

		// We change the staff member from the second to the third
		pseudos.add(new Pseudo("thirdPseudo", third.getLogin()));
		expectedPseudos.add(new Pseudo("thirdPseudo", third.getIdStaff(), third.fullName(), third.getLogin(), false, Action.U));
		
		// We reinitiliaze this entry. No login, no automatic
		pseudos.add(new Pseudo("fourthPseudo", false));
		expectedPseudos.add(new Pseudo("fourthPseudo", false, Action.D));
		
		// the fifthPseudo is supposed to disappear.
		// fifthPseudo disapear from the resulting list.
		
		final PseudoListDTO expectedResult = new PseudoListDTO(8021964, expectedPseudos);
		
		PseudoListDTO input = new PseudoListDTO(8021964, pseudos);

		String jsonInput = gson.toJson(input);
		this.mvc.perform(post("/project/api-ghosts")
		.content(jsonInput))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().json(gson.toJson(expectedResult)));
	}
	
	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(8021964);
	}
}
