package com.fitzhi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

	/**
	 * Constant representing one the 2 models of connection settings for project : <br/>
	 * This one if for the direct access : url repository / user / password
	 */
	public static final int USER_PASSWORD_ACCESS = 1;
	
	/**
	 * Constant representing one the 3 models of connection settings : <br/>
	 * This one if for the indirect access : url repository / remote filename with connection parameters.
	 */
	public static final int REMOTE_FILE_ACCESS = 2;

	/**
	 * Constant representing one the 3 models of connection settings : <br/>
	 * This one if for the indirect access : url repository / remote filename with connection parameters.
	 */
	public static final int NO_USER_PASSWORD_ACCESS = 3;
	
	
	/** 
	 * Operation.
	 */
	public static final String DASHBOARD_GENERATION = "dashboardGeneration";

	/**
	 * Title possible for an operation.
	 */
	public static final String PROJECT = "project";

	/**
	 * This progression value indicates that the task did not progess. 
	 */
	public static final int NO_PROGRESSION = Integer.MIN_VALUE;


	private Global() {
	}

	/**
	 * Value of a key when no record are found.<BR/>
	 * The default value is equal to -1.
	 */
	public static final int UNKNOWN = -1;

	/**
	 * Deep cloning of an object by serialization.
	 * @param object the given object to be cloned
	 * @return a new deep copy of the passed object
	 */
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
