/**
 * 
 */
package com.tixhi.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import com.tixhi.data.internal.Experience;
import com.tixhi.data.internal.Staff;

import junit.framework.TestCase;




/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class CsvTest extends TestCase {

	private Map<String, String> staffLevels = new HashMap<>();
	Map<String, Integer> skillLevels = new HashMap<>();

	private static File resourcesDirectory = new File("src/test/resources");

	@Test
	//FIXME May be there will be some enhancements in the test below
	public void testSimple() throws IOException {

		if (true) return;
		
		final String SAMPLE_CSV_FILE_PATH = resourcesDirectory.getAbsolutePath() + "/effectifISC.csv";

		skillLevels.put("", 1);
		skillLevels.put("Starting block", 1);
		skillLevels.put("Bronze", 2);
		skillLevels.put("Argent", 3);
		skillLevels.put("Or", 4);
		skillLevels.put("Platine", 5);

		staffLevels.put("ING. CONCEPTEUR DEVELOPPEUR", "ICD 3");
		staffLevels.put("CONCEPTEUR DEVELOPPEUR", "ICD 2");
		staffLevels.put("DEVELOPPEUR", "ICD 1");
		staffLevels.put("REFERENT EXPERTISE ET DEVELOPPEMENT", "ICD 4");
		
		
		staffLevels.put("CHEF DE PROJECT", "CPT 2");
		staffLevels.put("EXPERT TECHNIQUE", "ET 2");
		staffLevels.put("ARCHITECTE TECHNIQUE", "ET 4");
		
		Map<Integer, Staff> theStaff = new HashMap<>();
		
		try (Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));) {
			CsvToBean<CSVStaffMember> csvToBean = 
					new CsvToBeanBuilder<CSVStaffMember>(reader).withType(CSVStaffMember.class)
					.withIgnoreLeadingWhiteSpace(true).withSeparator(';').build();

			Iterator<CSVStaffMember> csvStaffIterator = csvToBean.iterator();
			csvStaffIterator.next();
			while (csvStaffIterator.hasNext()) {
				CSVStaffMember csvStaffMember = csvStaffIterator.next();
				Staff staff = new Staff (theStaff.size()+1, csvStaffMember.firstName, csvStaffMember.lastName, csvStaffMember.login, csvStaffMember.login,
						csvStaffMember.email, staffLevels.get(csvStaffMember.poste),
						(csvStaffMember.actif == null) ? true : false, false );
				
				if ((csvStaffMember.skill_java != null) && (skillLevels.containsKey(csvStaffMember.skill_java.trim()))) {
					staff.getExperiences().add(new Experience(1, skillLevels.get(csvStaffMember.skill_java.trim()) ));
				}
				if ((csvStaffMember.skill_dotNet != null) && (skillLevels.containsKey(csvStaffMember.skill_dotNet.trim()))) {
					staff.getExperiences().add(new Experience(6, skillLevels.get(csvStaffMember.skill_dotNet.trim())));
				}
				if (staff.getLastName() != null) {
					theStaff.put(staff.getIdStaff(), staff);
				}
			}
			Gson g = new Gson();
			final BufferedWriter bw = new BufferedWriter(new FileWriter(new File("staff.json")));
			bw.write(g.toJson(theStaff.values()));
			bw.close();
		}
	}

}
