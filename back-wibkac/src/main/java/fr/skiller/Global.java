package fr.skiller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Global {


	public static final int LIBRARY_DETECTED = 1;
	public static final int LIBRARY_DECLARED = 2;
	public static final int LIBRARY_REMOVED = -1;
	
	public static final String INTERNAL_FILE_SEPARATOR = "/";

	public static final char INTERNAL_FILE_SEPARATORCHAR = '/';

	public static final String LN = System.getProperty("line.separator");

	public static final String BACKEND_RETURN_CODE = "backend.return_code";
	
	public static final String BACKEND_RETURN_MESSAGE = "backend.return_message";

	private Global() {
	}

	/**
	 * Value of a key when no record are found.<BR/>
	 * The default value is equal to -1.
	 */
	public static final int UNKNOWN = -1;

	public static Date now() {
		return new Date();
	}

	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

}
