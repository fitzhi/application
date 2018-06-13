
import { Attribution } from './attribution';


export class Collaborater {
  id: number;
  firstName: string;
  lastName: string;
  nickName: string;
  email: string;
  level: string;

  /**
   * List of projects where the developer is involved.
   */
  projects: Attribution[];

  constructor(id: number, firstName: string, lastName: string, nickName: string, email: string, level: string) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.nickName = nickName;
    this.email = email;
    this.level = level;
  }
}
