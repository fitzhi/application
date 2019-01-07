import { Component, OnInit } from '@angular/core';

export interface Contributor {
  firstName: string;
  lastName: string;
  login: string;
  lastCommit: string;
  numberOfCommits: number;
  numberOfFiles: number;
}

/*
* Data store associated with the project staff team
*/
const TESTING_DATA: Contributor[] = [
  { firstName: 'jean', lastName: 'Dupont', login: 'jdupont', lastCommit: '15/12/2018 18:00', numberOfCommits: 15, numberOfFiles: 25 },
  { firstName: 'isidore', lastName: 'Dupond', login: 'idupond', lastCommit: '20/12/2018 18:00', numberOfCommits: 200, numberOfFiles: 328 },
];

@Component({
  selector: 'app-project-staff',
  templateUrl: './project-staff.component.html',
  styleUrls: ['./project-staff.component.css']
})
export class ProjectStaffComponent implements OnInit {

  public dataSource = TESTING_DATA;

  public  displayedColumns: string[] = ['name', 'login', 'lastCommit', 'numberOfCommits', 'numberOfFiles'];

  constructor() { }

  ngOnInit() {
  }

}
