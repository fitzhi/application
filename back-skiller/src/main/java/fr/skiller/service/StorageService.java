package fr.skiller.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

	final static int FILE_TYPE_PDF = 0;
	final static int FILE_TYPE_DOCX = 1;
	final static int FILE_TYPE_DOC = 2;
	final static int FILE_TYPE_TXT = 3;
	
	/**
	 * Initialize the service.
	 */
    void init();

    /**
     * Store a file inside the uploading directory.
     * @param file the file
     */
    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    /**
     * Delete all the files stored in the uploading directory.
     */
    void deleteAll();

}
