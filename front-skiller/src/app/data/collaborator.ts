
import { Attribution } from './attribution';


export class Collaborator {
  public id: number;
  public firstName: string;
  public lastName: string;
  public nickName: string;
  public email: string;
  public level: string;

  /**
   * List of projects where the developer is involved.
   */
  public projects: Attribution[];

}
