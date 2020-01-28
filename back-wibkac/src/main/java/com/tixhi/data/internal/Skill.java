package com.tixhi.data.internal;

import java.io.Serializable;

import lombok.Data;

public @Data class Skill implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1549084879092445214L;

	private int id = 0;
	
	private String title = "";
	
	public Skill() {
	}
	
	public Skill(int id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	
}
