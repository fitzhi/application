
export class Contributor {
  public id: number;
  public fullname: string;

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
