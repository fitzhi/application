/**
 * 
 */
package com.fitzhi.source.crawler.git;

import java.time.LocalDate;

import com.fitzhi.data.internal.Author;

/**
 * <p>
 * <b>Source Control Change : </b>
 * </p>
 * <p>
 * Raw extraction of the Source Control repository. <br/>
 * <i>Possible candidate for the future global aggregation</i>
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class SCMChange {
	
	/**
	 * identifier for the commit
	 */
	private String commitId;

	/**
	 * Full path filename.
	 */
	private String path;

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
	 * A numeric value representing the importance of the source file impacted by the change.
	 */
	private long importance;
		
	/**
	 * Constructor.
	 * @param commitId commit identifier
	 * @param fullPath full path of the source file (e.g. <code>org/springframework/boot/SpringApplication</code>)
	 * @param dateCommit Date of the commit 
	 * @param authorName author's name
	 * @param authorEmail author's email
	 */
	public SCMChange(String commitId, String fullPath, LocalDate dateCommit, String authorName, String authorEmail) {
		super();
		this.commitId = commitId;
		this.path = fullPath;
		this.dateCommit = dateCommit;
		this.author = new Author(authorName, authorEmail);
	}

	/**
	 * @return the file path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the commitId
	 */
	public String getCommitId() {
		return commitId;
	}

	/**
	 * @return the dateCommit
	 */
	public LocalDate getDateCommit() {
		return dateCommit;
	}

	/**
	 * @return the authorName
	 */
	public String getAuthorName() {
		return author.getName();
	}

	/**
	 * @return <code>true</code> if the author of the change is not anonymous. Author name is not <code>null</code>.
	 */
	public boolean isAuthorIdentified() {
		return (author.getName() != null);
	}

	/**
	 * @return the author
	 */
	public Author getAuthor() {
		return author;
	}

	
	/**
	 * @return the authorEmail
	 */
	public String getAuthorEmail() {
		return author.getEmail();
	}

	/**
	 * @return the Staff identifier
	 */
	public int getIdStaff() {
		return idStaff;
	}

	/**
	 * @param idStaff the Staff identifier to set
	 */
	public void setIdStaff(int idStaff) {
		this.idStaff = idStaff;
	}
	
	/**
	 * @return <code>true</code> if the change is identified (i.e. the staff identifier is known)
	 */
	public boolean isIdentified() {
		return (idStaff > 0);
	}

	/**
	 * @return the importance
	 */
	public long getImportance() {
		return importance;
	}

	/**
	 * @param importance to set
	 */
	public void setImportance(long importance) {
		this.importance = importance;
	}
	
	@Override
	public String toString() {
		return "SCMChange[path=" + path + ", dateCommit=" + dateCommit + ", author="
				+ author.toString()  + ", idStaff=" + idStaff + ", importance=" + importance
				+ "commitId=" + commitId + "]";
	}

}
