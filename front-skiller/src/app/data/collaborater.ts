
import { Attribution } from './attribution';


export class Collaborater {
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

  constructor(id: number, firstName: string, lastName: string, nickName: string, email: string,
    level: string, projects: Attribution[]) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.nickName = nickName;
    this.email = email;
    this.level = level;
    this.projects = projects;
  }
}
