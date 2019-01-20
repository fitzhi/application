import {Skill} from './skill';

export class Project {

  public id: number;
  public name: string;
  public connection_settings: number;
  public urlRepository: string;
  public username: string;
  public password: string;
  public filename: string;
  public skills: Skill[];

  constructor() {}

}

