package com.tixhi.data.internal;

import com.tixhi.bean.ProjectAuditHandler;

import lombok.Data;

/**
 * <p>
 * This object is used as parameter by {@link ProjectAuditHandler#setWeights(int, java.util.List)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class TopicWeight {

	private int idTopic;
	private int weight;
	
	public TopicWeight(int idTopic, int weight) {
		this.idTopic = idTopic;
		this.weight = weight;
	}
}
