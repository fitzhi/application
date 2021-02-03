package com.fitzhi.source.crawler.git;

import java.time.LocalDate;

import com.fitzhi.data.internal.SourceCodeDiffChange;

import lombok.Data;
import lombok.ToString;

/**
 * <p>
 * <b>Source Control Change : </b><br/>
 * <br/>
 * Raw extraction of the Source Control repository. <br/>
 * <i>Possible candidate for the future global aggregation</i>
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@ToString
public @Data class SourceChange {

	/**
	 * identifier for the commit
	 */
	private String commitId;

	/**
	 * Date of commit.
	 */
	private LocalDate dateCommit;

	/**
	 * Author 
	 */
	private Author author;

	/**
	 * Staff identifier retrieved for this commit.
	 */
	private int idStaff = 0;

	/**
	 * The number of lines added and deleted from the source file.
	 */
	private SourceCodeDiffChange diff;

	/**
	 * Constructor.
	 * 
	 * @param commitId    commit identifier
	 * @param dateCommit  Date of the commit
	 * @param authorName  author's name
	 * @param authorEmail author's email
	 * @param idStaff     staff identifier
	 * @param diff        the Source diff change
	 */
	public SourceChange(String commitId, LocalDate dateCommit, String authorName, String authorEmail, int idStaff,
			SourceCodeDiffChange diff) {
		super();
		this.commitId = commitId;
		this.dateCommit = dateCommit;
		this.author = new Author(authorName, authorEmail);
		this.idStaff = idStaff;
		this.diff = diff;
	}

	/**
	 * Constructor.
	 * 
	 * @param commitId    commit identifier
	 * @param dateCommit  Date of the commit
	 * @param authorName  author's name
	 * @param authorEmail author's email
	 * @param diff        the Source diff change
	 */
	public SourceChange(String commitId, LocalDate dateCommit, String authorName, String authorEmail,
			SourceCodeDiffChange diff) {
		this(commitId, dateCommit, authorName, authorEmail, 0, diff);
	}

	/**
	 * Constructor.
	 * 
	 * @param commitId    commit identifier
	 * @param dateCommit  Date of the commit
	 * @param authorName  author's name
	 * @param authorEmail author's email
	 */
	public SourceChange(String commitId, LocalDate dateCommit, String authorName, String authorEmail) {
		this(commitId, dateCommit, authorName, authorEmail, 0, new SourceCodeDiffChange());
	}

	/**
	 * Constructor.
	 * 
	 * @param dateCommit Date of the commit
	 * @param idStaff    staff identifier
	 */
	public SourceChange(LocalDate dateCommit, int idStaff) {
		this("", dateCommit, "", "", idStaff, new SourceCodeDiffChange());
	}

	/**
	 * @return <code>true</code> if the change is identified (i.e. the staff
	 *         identifier is known)
	 */
	public boolean isIdentified() {
		return (idStaff > 0);
	}

	/**
	 * @return <code>true</code> if the author of the change is not anonymous.
	 *         Author name is not <code>null</code>.
	 */
	public boolean isAuthorIdentified() {
		return (author.getName() != null);
	}

	/**
	 * @return the number of lines concerned this change.
	 */
	public int lines() {
		return (this.diff == null) ? 0 : this.diff.getLinesAdded() - this.diff.getLinesDeleted();

	}

}
