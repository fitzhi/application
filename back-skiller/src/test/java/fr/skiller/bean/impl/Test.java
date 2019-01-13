package fr.skiller.bean.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.skiller.bean.DataSaver;
import fr.skiller.data.internal.Staff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class Test {

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	@Autowired
	DataSaver dataSaver;

	@org.junit.Test
	public void generateNewFile() throws Exception {
		Map<Integer, Staff> company = new HashMap<Integer, Staff>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File("src/main/resources/staff.json")));
		StringBuilder sbContent = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
		Type listType = new TypeToken<ArrayList<Staff>>() {}.getType();
		List<Staff> staffsRead = gson.fromJson(sbContent.toString(), listType);
		for (Staff staffRead : staffsRead) {
			System.out.println(staffRead.lastName);
			company.put(staffRead.idStaff, staffRead);
		}
		dataSaver.saveStaff(company);

	}
}
