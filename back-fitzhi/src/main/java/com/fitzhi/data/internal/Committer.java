package com.fitzhi.data.internal;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fitzhi.data.external.Action;

import lombok.Data;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL This class is exchanged between the
 *         back-end and the front-end in the dialog project-ghosts.
 */
public @Data class Committer {

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
	 * Date of the first commit.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate firstCommit;

	/**
	 * Date of the latest commit.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate lastCommit;
	
	/**
	 * @return number of commit submitted by a developer inside the project.
	 */
	private int numberOfCommits;
	
	/**
	 * @return number of files modifier by a developer inside the project.
	 */
	private int numberOfFiles;

	/**
	 * Type of action executed in
	 * {@link com.fitzhi.bean.ProjectHandler#saveGhosts(int, Committer[])} for this
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
	 * {@link com.fitzhi.bean.ProjectHandler#saveGhosts(int, Committer[])} for this entry
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
