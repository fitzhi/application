/**
 * 
 */
package fr.skiller.source.crawler.git;

import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * <b>Source Control Change : </b><br/><br/>
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
	 * Date of commit.
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
		this.authorName = authorName;
		this.authorEmail = authorEmail;
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
		return authorName;
	}

	/**
	 * @return <code>true</code> if the author of the change is not anonymous. Author name is not <code>null</code>.
	 */
	public boolean isAuthorIdentified() {
		return (authorName != null);
	}
		
	/**
	 * @return the authorEmail
	 */
	public String getAuthorEmail() {
		return authorEmail;
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

	@Override
	public String toString() {
		return "SCMChange [commitId=" + commitId + ", path=" + path + ", dateCommit=" + dateCommit + ", authorName="
				+ authorName + ", authorEmail=" + authorEmail + ", idStaff=" + idStaff + "]";
	}
	
}
