/**
 * 
 */
package com.fitzhi.source.crawler.git;

import java.time.LocalDate;

import lombok.Data;

/**
 * <p>
 * <b>Source Control Change : </b><br/><br/>
 * Raw extraction of the Source Control repository. <br/>
 * <i>Possible candidate for the future global aggregation</i>
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
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
	 * Author name
	 */
	private String authorName;

	/**
	 * Date of commit.
	 */
	private String authorEmail;

	/**
	 * Staff identifier retrieved for this commit.
	 */
	private int idStaff = 0;
			
	/**
	 * Constructor.
	 * @param commitId commit identifier
	 * @param dateCommit Date of the commit 
	 * @param authorName author's name
	 * @param authorEmail author's email
	 * @param idStaff staff identifier
	 */
	public SourceChange(String commitId, LocalDate dateCommit, String authorName, String authorEmail, int idStaff) {
		super();
		this.commitId = commitId;
		this.dateCommit = dateCommit;
		this.authorName = authorName;
		this.authorEmail = authorEmail;
		this.idStaff = idStaff;
	}

	/**
	 * Constructor.
	 * @param commitId commit identifier
	 * @param dateCommit Date of the commit 
	 * @param authorName author's name
	 * @param authorEmail author's email
	 * @param idStaff staff identifier
	 */
	public SourceChange(String commitId, LocalDate dateCommit, String authorName, String authorEmail) {
		this(commitId, dateCommit, authorName, authorEmail, 0);
	}
	/**
	 * Constructor.
	 * @param dateCommit Date of the commit 
	 * @param idStaff staff identifier
	 */
	public SourceChange(LocalDate dateCommit, int idStaff) {
		this("", dateCommit, "", "", idStaff);
	}
	
	/**
	 * @return <code>true</code> if the change is identified (i.e. the staff identifier is known)
	 */
	public boolean isIdentified() {
		return (idStaff > 0);
	}

	/**
	 * @return <code>true</code> if the author of the change is not anonymous. Author name is not <code>null</code>.
	 */
	public boolean isAuthorIdentified() {
		return (authorName != null);
	}
	
	@Override
	public String toString() {
		return "SourceChange[dateCommit=" + dateCommit + ", authorName="
				+ authorName + ", authorEmail=" + authorEmail + ", idStaff=" + idStaff
				+ "commitId=" + commitId + "]";
	}

}
