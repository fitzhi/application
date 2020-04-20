/**
 * 
 */
package com.fitzhi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fitzhi.data.internal.Staff;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoadedTest {

	private final String STAFF_SAVE = "/api/staff/save";
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	/**
	 * Staff file to load.
	 */
	final String STAFF_CSV_FILE_PATH = "../staff-members-to-load.csv";
		
	@Autowired
	private MockMvc mvc;
	
//	@Test
	@WithMockUser
	public void internalLoadStaff() throws Exception {
		try (Reader reader = Files.newBufferedReader(Paths.get(STAFF_CSV_FILE_PATH));) {
			CsvToBean<LoadedStaffMember> csvToBean = 
				new CsvToBeanBuilder<LoadedStaffMember>(reader).withType(LoadedStaffMember.class)
				.withIgnoreLeadingWhiteSpace(true).withSeparator(';').build();

			Iterator<LoadedStaffMember> csvStaffIterator = csvToBean.iterator();
			csvStaffIterator.next();
			while (csvStaffIterator.hasNext()) {
				LoadedStaffMember staff = csvStaffIterator.next();
				Staff st = new Staff(-1, staff.firstName, staff.lastName, staff.nickname , staff.login, staff.email, staff.level);
				this.mvc.perform(post(STAFF_SAVE)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.content(gson.toJson(st)))
					.andExpect(status().isOk());		
			}
		}
	}
	
	@Test
	public void externalLoadStaff() throws Exception {
		RestTemplate template = new RestTemplate();
		
		try (Reader reader = Files.newBufferedReader(Paths.get(STAFF_CSV_FILE_PATH));) {
			CsvToBean<LoadedStaffMember> csvToBean = 
				new CsvToBeanBuilder<LoadedStaffMember>(reader).withType(LoadedStaffMember.class)
				.withIgnoreLeadingWhiteSpace(true).withSeparator(';').build();

			Iterator<LoadedStaffMember> csvStaffIterator = csvToBean.iterator();
			csvStaffIterator.next();
			while (csvStaffIterator.hasNext()) {
				LoadedStaffMember loadedStaff = csvStaffIterator.next();
				Staff st = new Staff(-1, loadedStaff.firstName, loadedStaff.lastName, loadedStaff.nickname , loadedStaff.login, loadedStaff.email, loadedStaff.level);
				st.setExternal( ("1".equals(loadedStaff.external) || "true".equals(loadedStaff.external)) );
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
				HttpEntity<String> entity = new HttpEntity<String>(gson.toJson(st), headers);
				template.postForLocation("http://localhost:8080" + STAFF_SAVE, entity);
			}
		}
		
	}
	
}
