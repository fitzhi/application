/**
 * 
 */
package com.fitzhi.controller.util;

import static com.fitzhi.Error.getStackTrace;
import static com.fitzhi.Global.BACKEND_RETURN_CODE;
import static com.fitzhi.Global.BACKEND_RETURN_MESSAGE;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * This class is an utility class in charge of loading the project
 * At this level of implementation, we do not create a specific bean just to deliver this feature.<br/>
 * Therefore, this class will be initialized directly during the <code>@PostConstruct</code> init method.<br/>
 * e.g. {@link com.fitzhi.controller.ProjectController#init() }
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
public class ProjectLoader {

	final ProjectHandler projectHandler;
	
	public ProjectLoader(ProjectHandler projectHandler) {
		super();
		this.projectHandler = projectHandler;
	}
	
	/**
	 * <p>Class used as a passed reference to a method in order to change it. The class will be used to setup a response entity.</p>
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 * @param <T> the type of variable on boarded within this reference
	 */
	public @Data class MyReference<T> {
		private T response;
		public MyReference() {
			// Empty constructor for serialization / deserialization purpose
		}
	}
	
	/**
	 * Read the project
	 * @param <T> The class of instance which will be sent within the envelop of the ResponseEntity
	 * @param idProject project identifier
	 * @param t the object to be sent back inside the ResponseEntit.
	 * @param refResponse the response to be returned to the front if the search is unsuccessful.<br/>
	 * 			<b>This parameter is not final. This method might change its value.</b>
	 * @return the retrieved project, or {@code null} if none's found.
	 */
	public <T> Project getProject(final int idProject, final T t, MyReference<ResponseEntity<T>> refResponse) {

		Project project = null;
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.set(BACKEND_RETURN_CODE, "O");
		headers.set(BACKEND_RETURN_MESSAGE, "No project found for the identifier " + idProject);

		try {
			project = projectHandler.lookup(idProject);
			if (project == null) {
				refResponse.response = new ResponseEntity<T>(t, headers, HttpStatus.NOT_FOUND);			
			} 
		} catch (final ApplicationException e) {
			log.error(getStackTrace(e));
			refResponse.response = new ResponseEntity<T>(t, headers, HttpStatus.BAD_REQUEST);
		}
		
		return project;
	}

}