export class Collaborater {
  id: number;
  firstName: string;
  lastName: string;
  nickName: string;
  email: string;
  level: string;

  constructor(id: number, firstName: string, lastName: string, nickName: string, email: string, level: string) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.nickName = nickName;
    this.email = email;
    this.level = level;
  }
}
