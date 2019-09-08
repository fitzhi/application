package fr.skiller.data.internal;

import javax.annotation.Generated;

import fr.skiller.data.external.Action;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL This class is exchanged between the
 *         back-end and the front-end in the dialog project-ghosts.
 */
public class Committer {

	/**
	 * Trace attached to a commit and not linked to a real, registered staff
	 * member. <br/>
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo
	 */
	private String pseudo;

	/**
	 * Staff id associated to the {@code pseudo} property.
	 */
	private int idStaff;

	/**
	 * Login associated to the {@code pseudo} property.
	 */
	private String login;

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
	private boolean technical;

	/**
	 * Type of action executed in
	 * {@link fr.skiller.bean.ProjectHandler#saveGhosts(int, Committer[])} for this
	 * entry
	 */
	private Action action;

	/**
	 * @param pseudo
	 *            Pseudo used by a developer to commit changes on a version
	 *            control.
	 * @param technical
	 *            {@code true} is this pseudo is a technical one (therefore the
	 *            idStaff, fullName will be null), {@code false} if it's a real
	 *            human being
	 */
	public Committer(String pseudo, boolean technical) {
		this(pseudo, technical, null);
	}

	/**
	 * @param pseudo
	 *            Pseudo used by a developer to commit changes on a version
	 *            control.
	 * @param technical
	 *            {@code true} is this pseudo is a technical one (therefore the
	 *            idStaff, fullName will be null), {@code false} if it's a real
	 *            human being
	 * @param action
	 *            type of action executed
	 */
	public Committer(String pseudo, boolean technical, Action action) {
		this.setPseudo(pseudo);
		this.setTechnical(technical);
		this.setIdStaff(Ghost.NULL);
		this.setLogin("");
		this.setAction(action);
	}

	/**
	 * @param pseudo
	 *            Pseudo used by a developer to commit changes on a version
	 *            control.
	 * @param login
	 *            login associated to this identifier
	 */
	public Committer(String pseudo, String login) {
		this(pseudo, Ghost.NULL, login, false);
	}

	/**
	 * Empty constructor.
	 */
	public Committer() {
	}

	/**
	 * @param pseudo
	 *            Pseudo used by a developer to commit changes on a version
	 *            control.
	 * @param idStaff
	 *            staff identifier
	 * @param login
	 *            login associated to this identifier
	 * @param technical
	 *            {@code true} is this pseudo is a technical one (therefore the
	 *            idStaff, fullName will be null), {@code false} if it's a real
	 *            human being
	 */
	public Committer(String pseudo, int idStaff, String login, boolean technical) {
		super();
		this.setPseudo(pseudo);
		this.setIdStaff(idStaff);
		this.setLogin(login);
		this.setTechnical(false);
	}

	/**
	 * @param pseudo
	 *            Pseudo used by a developer to commit changes on a version
	 *            control.
	 * @param idStaff
	 *            staff identifier
	 * @param login
	 *            login associated to this identifier
	 * @param technical
	 *            {@code true} is this pseudo is a technical one (therefore the
	 *            idStaff, fullName will be null), {@code false} if it's a real
	 *            human being
	 * @param action
	 *            type of {@link Action action}
	 */
	public Committer(String pseudo, int idStaff, String login, boolean technical, Action action) {
		super();
		this.setPseudo(pseudo);
		this.setIdStaff(idStaff);
		this.setLogin(login);
		this.setTechnical(technical);
		this.setAction(action);
	}

	@Override
	public String toString() {
		return "Pseudo [pseudo=" + getPseudo() + ", idStaff=" + getIdStaff()  + ", login=" + getLogin()
				+ ", technical=" + isTechnical() + ", action=" + getAction() + "]";
	}


	/**
	 * @return the pseudo of the developer<br/>
	 * Trace attached to a commit is not linked to a real, registered staff member.<br/>
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo
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
	 * @return the Staff identifier<br>
	 * Staff id associated to the {@code commitPseudo} property.
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
	 * @return the login associated to the {@code commitPseudo} property.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the technical status of this pseudo
	 * <p>
	 * If {@code technical} is equal to {@code true}, this pseudo does not
	 * correspond to a human being. It is a technical pseudo, used for
	 * administrative task. <i>(Sonar, for instance, might need an
	 * administrative user)</i>
	 * </p>
	 * <p>
	 * If {@code false}, there is a real staff member behind this pseudo.
	 * </p>
	 */
	public boolean isTechnical() {
		return technical;
	}

	/**
	 * @param technical the technical status to set for this pseudo
	 */
	public void setTechnical(boolean technical) {
		this.technical = technical;
	}

	/**
	 * @return the action
	 * Type of action executed in
	 * {@link fr.skiller.bean.ProjectHandler#saveGhosts(int, Committer[])} for this entry
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
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
		Committer other = (Committer) obj;
		if (action != other.action)
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
