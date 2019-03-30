package fr.skiller.data.internal;

/**
 * Repository ghosts. Unregistered committers in the repository. 
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class Ghost {

	public final static int NULL = -1;
	
	/**
	 * Trace attached to a commit and not linked to a real, registered staff member. <br/> 
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo 
	 */
	public String pseudo;
	
	/**
	 * Staff id associated to the {@code pseudo} property.
	 */
	public int idStaff;
	
	/**
	 * <p>
	 * If {@code technical} is equal to {@code true}, this pseudo does not correspond to a human being. 
	 * It is a technical pseudo, used for administrative task. <i>(Sonar, for instance, might need an administrative user)</i>
	 * </p>
	 * <p>
	 * If {@code false}, there is a staff member behind this pseudo. 
	 * </p>
	 */
	public boolean technical;

	/**
	 * Empty constructor for serialization purpose
	 */
	public Ghost() {
	}

	/**
	 * @param pseudo the committer's pseudo.
	 * @param idStaff the staff identifier related to this pseudo.
	 * @param technical technical or human being pseudo, boolean 
	 * <ul>
	 * <li>{@code true} this pseudo is a technical user. The {@code idStaff} is there for equal to {@code NULL}</li>
	 * <li>{@code false} this is pseudo of a real developer.</li>
	 * </ul>
	 */
	public Ghost(String pseudo, int idStaff, boolean technical) {
		super();
		this.pseudo = pseudo;
		this.idStaff = idStaff;
		this.technical = technical;
	}
	
	/**
	 * @param pseudo the committer's pseudo.
	 * @param technical technical or human being pseudo, boolean 
	 * <ul>
	 * <li>{@code true} this pseudo is a technical user. The {@code idStaff} is there for equal to {@code NULL}</li>
	 * <li>{@code false} this is pseudo of a real developer.</li>
	 * </ul>
	 */
	public Ghost(String pseudo, boolean technical) {
		this (pseudo, NULL, technical);
	}

	@Override
	public String toString() {
		return "Ghost [pseudo=" + pseudo + ", idStaff=" + idStaff + ", technical=" + technical + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idStaff;
		result = prime * result + ((pseudo == null) ? 0 : pseudo.hashCode());
		result = prime * result + (technical ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ghost other = (Ghost) obj;
		if (idStaff != other.idStaff)
			return false;
		if (pseudo == null) {
			if (other.pseudo != null)
				return false;
		} else if (!pseudo.equals(other.pseudo))
			return false;
		if (technical != other.technical)
			return false;
		return true;
	}	
	
}
