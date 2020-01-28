package com.tixhi.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * File system storage service.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface StorageService {
	
	/**
	 * Initialize the service.
	 */
    void init();

    /**
     * Store a file inside the uploading directory.
     * @param file the input file uploaded by the user
     * @param destination the path of the destination file
     */
    void store(MultipartFile file, String destination);

    /**
     * @return all files located from starting location, in a <code>stream</code> of <code>Path</code>
     */
    Stream<Path> loadAll();

    /**
     * Resolve a filename starting from the resolved starting location 
     * @see com.tixhi.service.impl.storageservice.StorageProperties
     * @param filename the passed filename
     * @return the <code>Path</code> corresponding to the passed filename
     */
    Path load(String filename);

    /**
     * @param filename the passed filename
     * @return the resource loaded
     */
    Resource loadAsResource(String filename);

    /**
     * Delete all the files stored in the uploading directory.
     */
    void deleteAll();
    
	/**
	 * @param fileName the TXT file to be read
	 * @return the content of the file
	 * @throws IOException if any IO exception occurs
	 */
	String readFileTXT(String fileName) throws IOException;
	
	/**
	 * @param fileName the DOC file to be read
	 * @return the content of the file
	 * @throws IOException if any IO exception occurs
	 */
	String readFileDOC(String fileName) throws IOException;

	/**
	 * @param fileName the DOCX file to be read
	 * @return the content of the file
	 * @throws IOException
	 */
	String readFileDOCX(String fileName) throws IOException;

	/**
	 * @param fileName the PDF file to be read
	 * @return the content of the file
	 * @throws IOException exception occurs when reading the file system
	 */
	String readFilePDF(String filename) throws IOException;
   
	/**
	 * @param fileName the concerned file
	 * @return the file size as retrieved from the file system
	 * @throws IOException exception occurs when reading the file system
	 */
	long getfileLength(String filename) throws IOException;
	
	/**
	 * @param typeOfFile the type of file (DOC, DOCX, PDF...)
	 * @return the content type related to this type of file.
	 */
	String getContentType(FileType typeOfFile);

	/**
	 * Remove a file from the <b>server upload</b> directory.<br/>
	 * This method wont throw an exception if an error occurs during the deletion. The file might not exist for instance.
	 * @param filename the given filename to be deleted
	 * @return {@code true} if the deletion succeeds, {@code false} otherwise
	 */
	boolean removeFile(String filename);
}
