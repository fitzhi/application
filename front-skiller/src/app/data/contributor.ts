
export class Contributor {
  public idStaff: number;
  public fullname: string;

  /**
   * Date of last commit of this developer
   */
  public lastCommit: string;

  /**
   * Number of commits submitted by this developer
   */
  public numberOfCommits: string;

  /**
   * Number of files updated by this developer
   */
  public numberOfFiles: string;
}
