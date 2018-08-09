package fr.skiller.data;

public class Project {

	public int id;
	public String name;
	
	public Project() { }
	
	/**
	 * @param id
	 * @param name
	 */
	public Project(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + "]";
	}

}
