/**
 * 
 */
package fr.skiller.data.internal;

import fr.skiller.data.external.Action;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * This class is exchanged between the back-end and the front-end in the dialog project-ghosts.
 */
public class Pseudo {

	/**
	 * Trace attached to a commit and not linked to a real, registered staff
	 * member. <br/>
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo
	 */
	public String pseudo;

	/**
	 * Staff id associated to the {@code pseudo} property.
	 */
	public int idStaff;

	/**
	 * Staff full name associated to the {@code pseudo} property.
	 */
	public String fullName;
	
	/**
	 * Login associated to the {@code pseudo} property.
	 */
	public String login;

	/**
	 * <p>
	 * If {@code technical} is equal to {@code true}, this pseudo does not
	 * correspond to a human being. It is a technical pseudo, used for
	 * administrative task. <i>(Sonar, for instance, might need an
	 * administrative user)</i>
	 * </p>
	 * <p>
	 * If {@code false}, there is a staff member behind this pseudo.
	 * </p>
	 */
	public boolean technical;

	/**
	 * Type of action executed in {@link fr.skiller.bean.ProjectHandler#saveGhosts(int, Pseudo[])} for this entry
	 */
	public Action action;

	/**
	 * @param pseudo
	 * @param technical
	 */
	public Pseudo(String pseudo, boolean technical) {
		super();
		this.pseudo = pseudo;
		this.technical = technical;
		this.idStaff = Ghost.NULL;
		this.login = "";
		this.fullName = "";
	}
	
	/**
	 * @param pseudo
	 * @param login
	 */
	public Pseudo(String pseudo, String login) {
		super();
		this.pseudo = pseudo;
		this.idStaff = Ghost.NULL;
		this.login = login;
		this.fullName = "";
		this.technical = false;
	}
	
	/**
	 * Empty constructor.
	 */
	public Pseudo() {}

	/**
	 * @param pseudo
	 * @param idStaff
	 * @param fullName
	 * @param login
	 * @param technical
	 * @param action
	 */
	public Pseudo(String pseudo, int idStaff, String fullName, String login, boolean technical, Action action) {
		super();
		this.pseudo = pseudo;
		this.idStaff = idStaff;
		this.fullName = fullName;
		this.login = login;
		this.technical = technical;
		this.action = action;
	}

	@Override
	public String toString() {
		return "Pseudo [pseudo=" + pseudo + ", idStaff=" + idStaff + ", fullName=" + fullName + ", login=" + login
				+ ", technical=" + technical + ", action=" + action + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + idStaff;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
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
		Pseudo other = (Pseudo) obj;
		if (action != other.action)
			return false;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (idStaff != other.idStaff)
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
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
