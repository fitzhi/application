package fr.skiller.controller;

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

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.Action;
import fr.skiller.data.external.PseudoListDTO;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Pseudo;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControlerSaveGhostsTest {

	private static final String FOURTH_PSEUDO = "fourthPseudo";

	private static final String THIRD_PSEUDO = "thirdPseudo";

	private static final String TWO_PSEUDO = "twoPseudo";

	private static final String ONE_PSEUDO = "onePseudo";

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
	public void before() throws SkillerException {
		first = (Staff) staffHandler.getStaff().values().toArray()[0];
		second = (Staff) staffHandler.getStaff().values().toArray()[1];
		third = (Staff) staffHandler.getStaff().values().toArray()[2];
		
		Project p = new Project(8021964, "testingGhost");
		projectHandler.addNewProject(p);
		p.getGhosts().add( new Ghost(ONE_PSEUDO, first.getIdStaff(), false) );
		p.getGhosts().add(new Ghost(TWO_PSEUDO, true));
		p.getGhosts().add(new Ghost(THIRD_PSEUDO, second.getIdStaff(), false));
		p.getGhosts().add(new Ghost(FOURTH_PSEUDO, true));
		p.getGhosts().add( new Ghost("fifthPseudo", third.getIdStaff(), false) );
		projectHandler.addNewProject(p);
	}
	
	@Test
	public void test() throws Exception {
		
		List<Pseudo> pseudos = new ArrayList<>();
		List<Pseudo> expectedPseudos = new ArrayList<>();

		// We don't change the value
		pseudos.add(new Pseudo(ONE_PSEUDO, first.getLogin()));
		expectedPseudos.add(new Pseudo(ONE_PSEUDO, first.getIdStaff(), first.fullName(), first.getLogin(), false, Action.N));
		
		// We don't change the value
		pseudos.add(new Pseudo(TWO_PSEUDO, true));
		expectedPseudos.add(new Pseudo(TWO_PSEUDO, true, Action.N));

		// We change the staff member from the second to the third
		pseudos.add(new Pseudo(THIRD_PSEUDO, third.getLogin()));
		expectedPseudos.add(new Pseudo(THIRD_PSEUDO, third.getIdStaff(), third.fullName(), third.getLogin(), false, Action.U));
		
		// We reinitialize this entry. No login, no automatic
		pseudos.add(new Pseudo(FOURTH_PSEUDO, false));
		expectedPseudos.add(new Pseudo(FOURTH_PSEUDO, false, Action.D));
		
		// the fifthPseudo is supposed to disappear.
		// fifthPseudo disappear from the resulting list.
		
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
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(8021964);
	}
}
