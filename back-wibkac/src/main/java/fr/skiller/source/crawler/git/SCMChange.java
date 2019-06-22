/**
 * 
 */
package fr.skiller.source.crawler.git;

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
	private Date dateCommit;

	/**
	 * Date of commit.
	 */
	private String authorName;

	/**
	 * Date of commit.
	 */
	private String authorEmail;

	/**
	 * Constructor.
	 * @param commitId commit identifier
	 * @param fullPath full path of the source file (e.g. <code>org/springframework/boot/SpringApplication</code>)
	 * @param dateCommit Date of the commit 
	 * @param authorName author's name
	 * @param authorEmail author's email
	 */
	public SCMChange(String commitId, String fullPath, Date dateCommit, String authorName, String authorEmail) {
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
	public Date getDateCommit() {
		return dateCommit;
	}

	@Override
	public String toString() {
		return "SCMChange [path=" + path + ", commitId=" + path + ", dateCommit=" + dateCommit + "]";
	}

	
}
