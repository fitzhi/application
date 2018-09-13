import { Attribution } from './attribution';
import { Experience } from './experience';


export class Collaborator {
  public id: number;
  public firstName: string;
  public lastName: string;
  public nickName: string;
  public login: string;
  public email: string;
  public level: string;
  public active: number;

  /**
   * The developer XP.
   */
  public experiences: Experience[];

  /**
   * List of projects where the developer is involved.
   */
  public projects: Attribution[];

}
