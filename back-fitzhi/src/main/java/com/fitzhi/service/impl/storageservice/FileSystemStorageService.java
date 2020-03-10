package com.fitzhi.service.impl.storageservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fitzhi.service.FileType;
import com.fitzhi.service.StorageService;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileSystemStorageService implements StorageService {

	/**
	 * Logger defined for this class.
	 */
	Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class.getCanonicalName());

	/**
	 * The root location for uploading
	 */
	private final Path rootLocation;
	
    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file, String filename) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                	String.format("Cannot store file with relative path outside current directory %s", filename));
            }
            try (InputStream inputStream = file.getInputStream()) {
            	if (logger.isDebugEnabled()) {
            		logger.debug(String.format(
            			"Storing upload file to the location %s", 
            		this.rootLocation.resolve(filename)));
            	}
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                .filter(path -> !path.equals(this.rootLocation))
                .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            logger.info("directory " + rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
	public String readFileTXT(final String fileName) throws IOException {
      	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("readFileTXT (%s)", this.rootLocation.resolve(fileName)));
    	}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		br.lines().forEach(sb::append);
		br.close();
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("readFileTXT returns %d characters.", sb.toString().length()));
    	}
		return sb.toString();
	}

    @Override
	public String readFileDOC(final String fileName) throws IOException {
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("readFileDOC (%s)", this.rootLocation.resolve(fileName)));
    	}
		FileInputStream in = new FileInputStream(this.rootLocation.resolve(fileName).toString());
		HWPFDocument doc = new HWPFDocument(in);
		String content = doc.getDocumentText();
		doc.close();
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("readFileDOC returns %s characters.", content.length()));
    	}
		return content;
	}

    @Override
	public String readFileDOCX(final String fileName) throws IOException {
      	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("readFileDOCX (%s)", this.rootLocation.resolve(fileName)));
    	}
		XWPFDocument docx = new XWPFDocument(new FileInputStream(this.rootLocation.resolve(fileName).toString()));
		XWPFWordExtractor we = new XWPFWordExtractor(docx);
		String content = we.getText();
		we.close();
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("readFileDOCX returns %s characters.", content.length()));
    	}
		return content;
	}

    @Override
    public String readFilePDF(final String fileName) throws IOException {
      	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("readFilePDF (%s)", this.rootLocation.resolve(fileName)));
    	}
		PdfReader reader = new PdfReader(this.rootLocation.resolve(fileName).toString());
		final StringBuilder sb = new StringBuilder();
		for (int pageNumber = 1; pageNumber < reader.getNumberOfPages(); pageNumber++) {
			sb.append(PdfTextExtractor.getTextFromPage(reader, pageNumber));
		}
		reader.close();
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("readFilePDF returns %d characters.", sb.toString().length()));
    	}
		return sb.toString();
	}

	@Override
	public long getfileLength(String filename) throws IOException {
		return new File(this.rootLocation.resolve(filename).toString()).length();
	}

	@Override
	public String getContentType(FileType typeOfFile) {
        // Try to determine file's content type
		final String contentType;
		switch (typeOfFile) {
			case FILE_TYPE_TXT:
				contentType = "text/html;charset=UTF-8";
				break;
			case FILE_TYPE_DOC:
				contentType ="application/msword";
				break;
			case FILE_TYPE_DOCX:
				contentType ="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
				break;
			case FILE_TYPE_PDF:
				contentType ="application/pdf";
				break;
			default:
				contentType = "application/octet-stream";
				break;
		}
		return contentType;
	}

	@Override
	public boolean removeFile(String filename) {
		return this.rootLocation.resolve(filename).toFile().delete();
	}
    
}

