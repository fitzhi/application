/**
 * 
 */
package fr.skiller.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import fr.skiller.data.internal.DeclaredExperience;

/**
 * This service is in charge of parsing a source of information containing the skills of an employee.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface ResumeParserService {

	/**
	 * Extract the skills from the resume file.
	 * @param filename Name of the application file
	 * @param typeFile Type of file. (3 formats are supported TXT, DOC, DOCX and PDF)
	 * @return the skills extracted.
	 * @throws IOException IO problem occurs when retrieving the application file
	 */
	DeclaredExperience extract (final String filename, final int fileType) throws IOException;
}
