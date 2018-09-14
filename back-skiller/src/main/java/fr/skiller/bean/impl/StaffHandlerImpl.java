package fr.skiller.bean.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;
import com.google.gson.reflect.TypeToken;


/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Component("mock.Staff")
public class StaffHandlerImpl implements StaffHandler {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	/**
	 * The Project collection.
	 */
	private HashMap<Integer, Staff> staff;

	public void init() {
		staff = null;
	}
	
	/**
	 * @return the Project collection.
	 */
	public Map<Integer, Staff> getStaff() {
		
		if (this.staff != null) {
			return this.staff;
		}
		
		File resourcesDirectory = new File("src/main/resources");
		String STAFF_JSON_FILE_PATH = resourcesDirectory.getAbsolutePath() + "/staff.json";
		
		this.staff = new HashMap<Integer, Staff>();
		try {
			StringBuilder sbContent = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(STAFF_JSON_FILE_PATH));
			String str;
			while ( (str = br.readLine()) != null) {
				sbContent.append(str);
			}
			Type listType = new TypeToken<ArrayList<Staff>>(){}.getType();
			List<Staff> staffsRead = gson.fromJson(sbContent.toString(), listType);
			for ( Staff staffRead : staffsRead) {
				this.staff.put(staffRead.idStaff, staffRead);
			}
			return this.staff;
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			return this.staff;
		} 
	}

}
