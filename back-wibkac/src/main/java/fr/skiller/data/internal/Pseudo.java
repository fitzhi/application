/**
 * 
 */
package fr.skiller.data.internal;

import javax.annotation.Generated;

import fr.skiller.data.external.Action;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL This class is exchanged between the
 *         back-end and the front-end in the dialog project-ghosts.
 */
public class Pseudo {

	/**
	 * Trace attached to a commit and not linked to a real, registered staff
	 * member. <br/>
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo
	 */
	private String commitPseudo;

	/**
	 * Staff id associated to the {@code commitPseudo} property.
	 */
	private int idStaff;

	/**
	 * Staff full name associated to the {@code commitPseudo} property.
	 */
	private String fullName;

	/**
	 * Login associated to the {@code commitPseudo} property.
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
	 * {@link fr.skiller.bean.ProjectHandler#saveGhosts(int, Pseudo[])} for this
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
	public Pseudo(String pseudo, boolean technical) {
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
	public Pseudo(String pseudo, boolean technical, Action action) {
		this.setCommitPseudo(pseudo);
		this.setTechnical(technical);
		this.setIdStaff(Ghost.NULL);
		this.setLogin("");
		this.setFullName("");
		this.setAction(action);
	}

	/**
	 * @param pseudo
	 *            Pseudo used by a developer to commit changes on a version
	 *            control.
	 * @param login
	 *            login associated to this identifier
	 */
	public Pseudo(String pseudo, String login) {
		this(pseudo, Ghost.NULL, "", login, false);
	}

	/**
	 * Empty constructor.
	 */
	public Pseudo() {
	}

	/**
	 * @param pseudo
	 *            Pseudo used by a developer to commit changes on a version
	 *            control.
	 * @param idStaff
	 *            staff identifier
	 * @param fullName
	 *            full name associated to this identifier
	 * @param login
	 *            login associated to this identifier
	 * @param technical
	 *            {@code true} is this pseudo is a technical one (therefore the
	 *            idStaff, fullName will be null), {@code false} if it's a real
	 *            human being
	 */
	public Pseudo(String pseudo, int idStaff, String fullName, String login, boolean technical) {
		super();
		this.setCommitPseudo(pseudo);
		this.setIdStaff(idStaff);
		this.setFullName(fullName);
		this.setLogin(login);
		this.setTechnical(false);
	}

	/**
	 * @param pseudo
	 *            Pseudo used by a developer to commit changes on a version
	 *            control.
	 * @param idStaff
	 *            staff identifier
	 * @param fullName
	 *            full name associated to this identifier
	 * @param login
	 *            login associated to this identifier
	 * @param technical
	 *            {@code true} is this pseudo is a technical one (therefore the
	 *            idStaff, fullName will be null), {@code false} if it's a real
	 *            human being
	 * @param action
	 *            type of {@link Action action}
	 */
	public Pseudo(String pseudo, int idStaff, String fullName, String login, boolean technical, Action action) {
		super();
		this.setCommitPseudo(pseudo);
		this.setIdStaff(idStaff);
		this.setFullName(fullName);
		this.setLogin(login);
		this.setTechnical(technical);
		this.setAction(action);
	}

	@Override
	public String toString() {
		return "Pseudo [pseudo=" + getCommitPseudo() + ", idStaff=" + getIdStaff() + ", fullName=" + getFullName() + ", login=" + getLogin()
				+ ", technical=" + isTechnical() + ", action=" + getAction() + "]";
	}

	@Override
	@Generated ("eclipse")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getAction() == null) ? 0 : getAction().hashCode());
		result = prime * result + ((getFullName() == null) ? 0 : getFullName().hashCode());
		result = prime * result + getIdStaff();
		result = prime * result + ((getLogin() == null) ? 0 : getLogin().hashCode());
		result = prime * result + ((getCommitPseudo() == null) ? 0 : getCommitPseudo().hashCode());
		result = prime * result + (isTechnical() ? 1231 : 1237);
		return result;
	}

	@Override
	@Generated ("eclipse")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pseudo other = (Pseudo) obj;
		if (getAction() != other.getAction())
			return false;
		if (getFullName() == null) {
			if (other.getFullName() != null)
				return false;
		} else if (!getFullName().equals(other.getFullName()))
			return false;
		if (getIdStaff() != other.getIdStaff())
			return false;
		if (getLogin() == null) {
			if (other.getLogin() != null)
				return false;
		} else if (!getLogin().equals(other.getLogin()))
			return false;
		if (getCommitPseudo() == null) {
			if (other.getCommitPseudo() != null)
				return false;
		} else if (!getCommitPseudo().equals(other.getCommitPseudo()))
			return false;
		if (isTechnical() != other.isTechnical())
			return false;
		return true;
	}

	/**
	 * @return the pseudo of the developer<br/>
	 * Trace attached to a commit is not linked to a real, registered staff member.<br/>
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo
	 */
	public String getCommitPseudo() {
		return commitPseudo;
	}

	/**
	 * @param pseudo the pseudo to set
	 */
	public void setCommitPseudo(String pseudo) {
		this.commitPseudo = pseudo;
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
	 * @return the fullName
	 * Staff full name associated to the {@code commitPseudo} property.
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set, which is associated to the {@code commitPseudo} property.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
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
	 * {@link fr.skiller.bean.ProjectHandler#saveGhosts(int, Pseudo[])} for this entry
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

}
