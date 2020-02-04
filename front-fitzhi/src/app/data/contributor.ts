
export class Contributor {

	/**
	 * Staff's identifier.
	 */
	public idStaff: number;

	/**
	 * Staff's fullname (first + last)
	 */
	public fullname: string;

	/**
	 * True is this developer's still in activity in the company, False otherwise
	 */
	public active: boolean;

	/**
	 * True is this developer belong to the company, False otherwise.
	 */
	public external: boolean;

	/**
	 * Date of first commit for this developer
	 */
	public firstCommit: Date;

	/**
	 * Date of last commit for this developer
	 */
	public lastCommit: Date;

	/**
	 * Number of commits submitted by this developer
	 */
	public numberOfCommits: number;

	/**
	 * Number of files updated by this developer
	 */
	public numberOfFiles: number;
}
