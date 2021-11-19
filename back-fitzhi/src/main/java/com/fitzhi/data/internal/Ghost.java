package com.fitzhi.data.internal;
import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fitzhi.data.source.GitMetrics;

import lombok.Data;

/**
 * <p>
 * Ghost identified in the repository.
 * Ghost are unregistered committers in the repository.
 * System failed to retrieve the read developer behind a {@link #pseudo pseudo}.
 * </p>
 * A ghost can be 
 * <ul>
 * <li>{@link #technical technical}</li>
 * <li>{@link #idStaff associate to a staff identifier}</li>
 * </ul>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Ghost implements Serializable, GitMetrics {

	/**
	 * serialVersionUID for serialization purpose.
	 */
	private static final long serialVersionUID = -2461869054149715543L;

	public static final int NULL = -1;
	
	/**
	 * Pseudo attached to a commit and not linked to a real and registered staff member. <br/> 
	 * Nothing forbids Bruce Wayne to use Batman as a commit pseudo.
	 */
	private String pseudo;
	
	/**
	 * Staff id associated to the {@link com.fitzhi.data.internal.Ghost#pseudo pseudo} property.
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
	 * the number of commit submitted by a developer inside the project.
	 */
	private int numberOfCommits;
	
	/**
	 * the number of files modified by a developer inside the project.
	 */
	private int numberOfFiles;

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
		this.setPseudo(pseudo);
		this.setIdStaff(idStaff);
		this.setTechnical(technical);
	}

	/**
	 * @param pseudo the committer's pseudo.
	 * @param technical Is this pseudo either a technical alias, or a human being.
	 * This parameter is a boolean value : 
	 * <ul>
	 * <li>{@code true} this pseudo is a technical user. Therefore, the {@code idStaff} is equal to {@code NULL}</li>
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
