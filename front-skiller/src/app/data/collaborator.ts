import { Attribution } from './attribution';
import { Experience } from './experience';


export class Collaborator {
  public idStaff: number;
  public firstName: string;
  public lastName: string;
  public nickName: string;
  public login: string;
  public email: string;
  public level: string;
  public isActive: boolean;
  public dateInactive: Date;
  public application: string;
  public typeOfApplication: number;

  /**
   * The developer XP.
   */
  public experiences: Experience[];

  /**
   * List of missions where the developer has been involved.
   */
  public missions: Attribution[];

}
