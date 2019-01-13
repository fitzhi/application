package fr.skiller.data.internal;

public class Skill {

	public int id = 0;
	
	public String title = "";
	
	public Skill() {
	}
	
	public Skill(int id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	@Override
	public String toString() {
		return "Skill [id=" + id + ", title=" + title + "]";
	}

	
}
