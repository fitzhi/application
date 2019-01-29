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
	
}
