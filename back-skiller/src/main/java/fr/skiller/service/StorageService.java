package fr.skiller.service;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
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
    
	/**
	 * @param fileName the TXT file to be read
	 * @return the content of the file
	 * @throws IOException
	 */
	String readFileTXT(final String fileName) throws IOException;
	
	/**
	 * @param fileName the DOC file to be read
	 * @return the content of the file
	 * @throws IOException
	 */
	String readFileDOC(final String fileName) throws IOException;

	/**
	 * @param fileName the DOCX file to be read
	 * @return the content of the file
	 * @throws IOException
	 */
	String readFileDOCX(final String fileName) throws IOException;

	/**
	 * @param fileName the PDF file to be read
	 * @return the content of the file
	 * @throws IOException
	 */
	String readFilePDF(final String filename) throws IOException;
   

}
