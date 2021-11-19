package com.fitzhi.data.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * This class contains all information regarding a topic of an audit of the project
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class AuditTopic implements Serializable {
	
	/**
	 * serialVersionUID for serialization purpose.
	 */
	private static final long serialVersionUID = -2060545843809958388L;

	/**
	 * Topic identifier involved in the audit.
	 */
	int idTopic;

	/**
	 * Topic audit evaluation given by an expert.
	 */
	int evaluation;
	
	/**
	 * The weight of topic note inside the global audit evaluation.
	 */
	int weight;
	
	/**
	 * Text containing the executive summary for the audit.
	 */
	String report;

	/**
	 * List of attachment files associated to this topic.
	 */
	List<AttachmentFile> attachmentList = new ArrayList<>();
	
	/**
	 * Empty constructor for <u>serialization</u> / <u>de-serialization</u> purpose
	 */
	public AuditTopic() {
		// Empty constructor for serialization / de-serialization purpose
	}

	/**
	 * AuditTopic constructor with some default values (such as the weight equal to <b>5</b>).
	 * @param idTopic identifier of the sonar project entry.
	 */
	public AuditTopic(int idTopic) {
		this(idTopic, 0, 5, null);
	}
	
	/**
	 * AuditTopic constructor.
	 * @param idTopic identifier of the sonar project entry.
	 * @param evaluation the audit evaluation given by an expert.
	 * @param weight of topic note inside the global audit evaluation.
	 */
	public AuditTopic(int idTopic, int evaluation, int weight) {
		this(idTopic, evaluation, weight, null);
	}
	
	/**
	 * AuditTopic constructor.
	 * @param idTopic identifier of the sonar project entry.
	 * @param evaluation the audit evaluation given by an expert.
	 * @param weight of topic note inside the global audit evaluation.
	 * @param report executive summary
	 */
	public AuditTopic(int idTopic, int evaluation, int weight, String report) {
		this.idTopic = idTopic;
		this.evaluation = evaluation;
		this.weight = weight;
		this.report = report;
	}
	
}
