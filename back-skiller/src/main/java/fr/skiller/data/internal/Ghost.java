package fr.skiller.data.internal;

import javax.annotation.Generated;

/**
 * Repository ghosts. Unregistered committers in the repository. 
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class Ghost {

	public static final int NULL = -1;
	
	/**
	 * Trace attached to a commit and not linked to a real, registered staff member. <br/> 
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo 
	 */
	private String pseudo;
	
	/**
	 * Staff id associated to the {@code pseudo} property.
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

	@Override
	public String toString() {
		return "Ghost [pseudo=" + getPseudo() + ", idStaff=" + getIdStaff() + ", technical=" + isTechnical() + "]";
	}

	@Override
	@Generated("eclipse")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdStaff();
		result = prime * result + ((getPseudo() == null) ? 0 : getPseudo().hashCode());
		result = prime * result + (isTechnical() ? 1231 : 1237);
		return result;
	}

	@Override
	@Generated("eclipse")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ghost other = (Ghost) obj;
		if (getIdStaff() != other.getIdStaff())
			return false;
		if (getPseudo() == null) {
			if (other.getPseudo() != null)
				return false;
		} else if (!getPseudo().equals(other.getPseudo()))
			return false;
		if (isTechnical() != other.isTechnical())
			return false;
		return true;
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
