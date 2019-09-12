/**
 * Class representing a commit submitted on a project.
 */
export class Commit {

	/**
	 * @param idStaff Identifier of the staff member who has executed this commit.
	 * @param dateCommit Date of this commit.
	 */
	constructor(
		public idStaff: number,
		public firstName: string,
		public lastName: string,
		public dateCommit: Date) {}

	/**
	* @returns the complete name of the committer.
	*/
	fullname(): string {
		return this.firstName + ' ' + this.lastName;
	}
}
