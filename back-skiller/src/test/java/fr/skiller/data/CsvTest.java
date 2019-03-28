/**
 * 
 */
package fr.skiller.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;
import com.google.gson.Gson;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import fr.skiller.data.internal.Experience;
import fr.skiller.data.internal.Staff;
import junit.framework.TestCase;




/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class CsvTest extends TestCase {

	Map<String, String> STAFF_LEVELS = new HashMap<String, String>();
	Map<String, Integer> SKILLS_LEVELS = new HashMap<String, Integer>();

	private static File resourcesDirectory = new File("src/test/resources");

	@Test
	// May be there will be some enhancements in the test below
	public void emptyTest() throws Exception {
		
	}
	
	public void testSimple() throws Exception {

		final String SAMPLE_CSV_FILE_PATH = resourcesDirectory.getAbsolutePath() + "/effectifISC.csv";

		SKILLS_LEVELS.put("", 1);
		SKILLS_LEVELS.put("Starting block", 1);
		SKILLS_LEVELS.put("Bronze", 2);
		SKILLS_LEVELS.put("Argent", 3);
		SKILLS_LEVELS.put("Or", 4);
		SKILLS_LEVELS.put("Platine", 5);

		STAFF_LEVELS.put("ING. CONCEPTEUR DEVELOPPEUR", "ICD 3");
		STAFF_LEVELS.put("CONCEPTEUR DEVELOPPEUR", "ICD 2");
		STAFF_LEVELS.put("DEVELOPPEUR", "ICD 1");
		STAFF_LEVELS.put("REFERENT EXPERTISE ET DEVELOPPEMENT", "ICD 4");
		
		
		STAFF_LEVELS.put("CHEF DE PROJECT", "CPT 2");
		STAFF_LEVELS.put("EXPERT TECHNIQUE", "ET 2");
		STAFF_LEVELS.put("ARCHITECTE TECHNIQUE", "ET 4");
		
		Map<Integer, Staff> STAFF = new HashMap<Integer, Staff>();
		
		try (Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));) {
			CsvToBean<CSVStaffMember> csvToBean = 
					new CsvToBeanBuilder<CSVStaffMember>(reader).withType(CSVStaffMember.class)
					.withIgnoreLeadingWhiteSpace(true).withSeparator(';').build();

			Iterator<CSVStaffMember> csvStaffIterator = csvToBean.iterator();
			csvStaffIterator.next();
			while (csvStaffIterator.hasNext()) {
				CSVStaffMember csvStaffMember = csvStaffIterator.next();
				Staff staff = new Staff (STAFF.size()+1, csvStaffMember.firstName, csvStaffMember.lastName, csvStaffMember.login, csvStaffMember.login,
						csvStaffMember.email, STAFF_LEVELS.get(csvStaffMember.poste),
						(csvStaffMember.actif == null) ? true : false, false );
				
				if ((csvStaffMember.skill_java != null) && (SKILLS_LEVELS.containsKey(csvStaffMember.skill_java.trim()))) {
					staff.experiences.add(new Experience(1, "Java", SKILLS_LEVELS.get(csvStaffMember.skill_java.trim()) ));
				}
				if ((csvStaffMember.skill_dotNet != null) && (SKILLS_LEVELS.containsKey(csvStaffMember.skill_dotNet.trim()))) {
					staff.experiences.add(new Experience(6, ".Net", SKILLS_LEVELS.get(csvStaffMember.skill_dotNet.trim())));
				}
				if (staff.lastName != null) {
					STAFF.put(staff.idStaff, staff);
				}
			}
			Gson g = new Gson();
			final BufferedWriter bw = new BufferedWriter(new FileWriter(new File("staff.json")));
			bw.write(g.toJson(STAFF.values()));
			bw.close();
		}
	}

	// LoggerFactory.getLogger(JsonTest.class).debug(g.toJson(collab));

}
