
export class Contributor {
  public id: number;
  public fullname: string;

  /**
   * Date of first commit for this developer
   */
  public firstCommit: string;

  /**
   * Date of last commit for this developer
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
