/**
 * 
 */
package fr.skiller.service;

import fr.skiller.data.internal.Resume;
import fr.skiller.exception.SkillerException;

/**
 * This service is in charge of parsing a source of information containing the skills of an employee.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface ResumeParserService {

	/**
	 * Extract the skills from the resume file.
	 * @param filename Name of the application file
	 * @param typeOfApplication Type of file. (3 formats are supported TXT, DOC, DOCX and PDF)
	 * @return the skills extracted.
	 * @throws SkillerException problem occurs when retrieving the application file
	 */
	Resume extract (final String filename, final FileType typeOfApplication) throws SkillerException;
}
