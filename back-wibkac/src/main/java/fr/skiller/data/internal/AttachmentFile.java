package fr.skiller.data.internal;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * <p>
 * Attachment file.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class AttachmentFile {

	/**
	 * File identifier of the attachment inside an audit topic.
	 */
	private int fileIdentifier;
	
	/**
	 * File name of the attachment file.
	 */
	private String fileName;
	
	/**
	 * Label of the filename.
	 */
	private String fileLabel;
	
	/**
	 * Type of file (Word, PDF...)
	 */
	private int typeOfFile;
	
	/**
	 * Public empty constructor for serialization purpose.
	 */
	public AttachmentFile() {}
	
	/**
	 * Attachment file construction.
	 * @param fileIdentifier File identifier of the attachment inside an audit topic.
	 * @param fileName File name of the attachment file.
	 * @param fileLabel Label of the filename.
	 * @param typeOfFile Type of file (Word, PDF...)
	 */
	public AttachmentFile(int fileIdentifier, String fileName, String fileLabel, int typeOfFile) {
		this.fileIdentifier = fileIdentifier;
		this.fileName = fileName;
		this.fileLabel = fileLabel;
		this.typeOfFile = typeOfFile;
	}
}
