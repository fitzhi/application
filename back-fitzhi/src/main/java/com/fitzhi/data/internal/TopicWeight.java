package com.fitzhi.data.internal;

import java.io.Serializable;

import com.fitzhi.bean.ProjectAuditHandler;

import lombok.Data;

/**
 * <p>
 * This object is used as parameter by {@link ProjectAuditHandler#setWeights(int, java.util.List)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class TopicWeight  implements Serializable {

	private int idTopic;
	private int weight;

	/**
	 * Empty constructor for serializatoin purpose.
	 */
	public TopicWeight() {
	}

	public TopicWeight(int idTopic, int weight) {
		this.idTopic = idTopic;
		this.weight = weight;
	}
}
