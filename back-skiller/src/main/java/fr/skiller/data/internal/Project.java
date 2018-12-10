package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.List;

public class Project {

	public int id;
	public String name;
	public List<Skill> skills;
	
	public Project() { }
	
	/**
	 * @param id Project identifier
	 * @param name Name of the project
	 */
	public Project(int id, String name) {
		super();
		this.id = id;
		this.name = name;
		skills = new ArrayList<Skill>();
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", skills=" + skills + "]";
	}

}
