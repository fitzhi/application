package fr.skiller.data.internal;
import lombok.Data;

/**
 * <p>
 * Ghost identified in the repository.<br/>
 * Ghost are unregistered committers in the repository.<br/>
 * System failed to retrieve the read developer behind a {@link #pseudo pseudo}.<br/>
 * A ghost can be 
 * <ul>
 * <li>{@link #technical technical}</li>
 * <li>{@link #idStaff associate to a staff identifier}</li>
 * </ul>
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Ghost {

	public static final int NULL = -1;
	
	/**
	 * Pseudo attached to a commit and not linked to a real and registered staff member. <br/> 
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo.
	 */
	private String pseudo;
	
	/**
	 * Staff id associated to the {@link fr.skiller.data.internal.Ghost#pseudo pseudo} property.
	 */
	private int idStaff;
	
	/**
	 * <p>
	 * If {@code technical} is equal to {@code true}, this pseudo does not correspond to a human being. 
	 * It is a technical pseudo, used for administrative task. <i>(Sonar, for instance, might need an administrative user)</i>
	 * </p>
	 * <p>
	 * If {@code false}, there is a staff member behind this pseudo. 
	 * </p>
	 */
	private boolean technical;

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
		this.setPseudo(pseudo);
		this.setIdStaff(idStaff);
		this.setTechnical(technical);
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


	/**
	 * @return the alleged pseudo for this ghost
	 * <p>
	 * Trace attached to a commit and not linked to a real, registered staff member. <br/> 
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo 
	 * </p>
	 */
	public String getPseudo() {
		return pseudo;
	}

	/**
	 * @param pseudo the pseudo to set
	 */
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	/**
	 * Staff id associated to the {@code pseudo} property.
	 * @return the idStaff
	 */
	public int getIdStaff() {
		return idStaff;
	}

	/**
	 * @param idStaff the idStaff to set
	 */
	public void setIdStaff(int idStaff) {
		this.idStaff = idStaff;
	}

	/**
	 * @return the technical
	 * <p>
	 * If {@code technical} is equal to {@code true}, this pseudo does not correspond to a human being. 
	 * It is a technical pseudo, used for administrative task. <i>(Sonar, for instance, might need an administrative user)</i>
	 * </p>
	 * <p>
	 * If {@code false}, there is a staff member behind this pseudo. 
	 * </p>
	 */
	public boolean isTechnical() {
		return technical;
	}

	/**
	 * @param technical the technical to set
	 * <p>
	 * If {@code technical} is equal to {@code true}, this pseudo does not correspond to a human being. 
	 * It is a technical pseudo, used for administrative task. <i>(Sonar, for instance, might need an administrative user)</i>
	 * </p>
	 * <p>
	 * If {@code false}, there is a staff member behind this pseudo. 
	 * </p>
	 */
	public void setTechnical(boolean technical) {
		this.technical = technical;
	}	
		
}
