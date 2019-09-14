package fr.skiller.data.internal;

import lombok.Data;

public @Data class Skill {

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
